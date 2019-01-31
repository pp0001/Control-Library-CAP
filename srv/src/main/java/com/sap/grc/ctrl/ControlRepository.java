package com.sap.grc.ctrl;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jpa.com.sap.grc.ctrl.test.Controls;

@Repository
public interface ControlRepository extends JpaRepository<Controls, String> {

}
