package com.sap.grc.ctrl;

import com.sap.grc.ctrl.jpa.com.sap.grc.ctrl.Controls;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface ControlRepository extends JpaRepository<Controls, Integer> {

}
