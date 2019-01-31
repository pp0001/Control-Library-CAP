package com.sap.grc.ctrl.lib;


import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.sap.cloud.sdk.service.prov.api.transaction.TransactionManager;
import com.sap.cloud.sdk.service.prov.api.transaction.TransactionManagerProvider;
import com.sap.cloud.servicesdk.spring.AppContextProvider;
import com.sap.cloud.servicesdk.spring.SpringConnectionProvider;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;


public class SpringTransactionManagerProvider implements TransactionManagerProvider {
    private static Logger logger = LoggerFactory.getLogger(SpringTransactionManager.class);
    private static SpringTransactionManager springTransactionManager = null;
    
    @Autowired
    HikariDataSource dataSource;

    SpringConnectionProvider springConnectionProvider;

    @Override
    public TransactionManager getTransactionManager() {
      PlatformTransactionManager ptm =
          AppContextProvider.getApplicationContext().getBean(PlatformTransactionManager.class);
      DataSource dataSource = AppContextProvider.getApplicationContext().getBean(DataSource.class);
      springConnectionProvider =
          AppContextProvider.getApplicationContext().getBean(SpringConnectionProvider.class);
      if (springTransactionManager == null) {
        springTransactionManager = new SpringTransactionManager(ptm, dataSource);
      }
      return springTransactionManager;
    }

    class SpringTransactionManager implements TransactionManager {

      private final PlatformTransactionManager ptm;
      private final DataSource dataSource;
      private ThreadLocal<TransactionStatus> status = new ThreadLocal<>();

      public SpringTransactionManager(PlatformTransactionManager ptm, DataSource dataSource) {
        this.ptm = ptm;
        this.dataSource = dataSource;
      }

      @Override
      public void commitTransaction() throws TransactionException {
        Long threadId = Thread.currentThread().getId();
        logger.info("Commit called, thread= " + threadId);
        if (status.get() == null) {
          logger.info("Transaction already finished, thread=" + threadId);
          throw new TransactionException("Transaction already finished, thread=" + threadId) {
          };
        }
        logger
            .info("commit transaction with status" + status.get().toString() + " thread=" + threadId);
        ptm.commit(status.get());
        status.set(null);
      }

      @Override
      public void rollbackTransaction() throws TransactionException {
        Long threadId = Thread.currentThread().getId();
        logger.info("Rollback called, thread= " + threadId);
        if (status.get() == null) {
          logger.info("Transaction already finished, thread=" + threadId);
          throw new TransactionException("Transaction already finished, thread=" + threadId) {
          };
        }
        logger.info(
            "rollback transaction with status" + status.get().toString() + " thread=" + threadId);
        ptm.rollback(status.get());
        status.set(null);
      }

      @Override
      public void startTransaction() throws TransactionException {
        Long threadId = Thread.currentThread().getId();
        logger.info("Start transaction called, thread=" + threadId);
        if (null != status.get()) {
          logger
              .info("Start transaction called but status is still set=>rollback, thread=" + threadId);
          ptm.rollback(status.get());
        }
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        status.set(ptm.getTransaction(def));
        logger.info("status is" + status.get().toString() + " thread=" + threadId);
      }

      @Override
      public void cleanupTransaction() {
        springConnectionProvider.cleanUpConnection();
        // to test the no of active and total connections
        try {
          HikariPoolMXBean bean = dataSource.unwrap(HikariDataSource.class).getHikariPoolMXBean();
          logger.debug("Total connections=" + bean.getTotalConnections());
          logger.debug("Active connections=" + bean.getActiveConnections());
        } catch (SQLException e) {
          Long threadId = Thread.currentThread().getId();
          logger.error("SQLException occured in method cleanupTransaction. Thread=" + threadId + ". "
              + "Check application log for more details.", e);
        }
      }
    }
  }