package com.sap.grc.ctrl;

import java.util.ArrayList;
import java.util.List;

import com.sap.cloud.sdk.service.prov.api.*;
import com.sap.cloud.sdk.service.prov.api.annotations.*;
import com.sap.cloud.sdk.service.prov.api.exits.*;
import com.sap.cloud.sdk.service.prov.api.request.*;
import com.sap.cloud.sdk.service.prov.api.response.*;
import org.slf4j.*;

public class ControlService {
 
  private static final Logger LOG = LoggerFactory.getLogger (ControlService.class.getName());

  @BeforeRead (entity="Controls", serviceName="ControlService")
  public BeforeReadResponse beforeReadControls(ReadRequest req, ExtensionHelper h){
    LOG.error ("##### Orders - beforeReadOrders ########");
    return BeforeReadResponse.setSuccess().response();
  }

  @AfterRead (entity = "Controls", serviceName="ControlService")
  public ReadResponse afterReadOrders (ReadRequest req, ReadResponseAccessor res, ExtensionHelper h) {
    EntityData ed = res.getEntityData();
    EntityData ex = EntityData.getBuilder(ed).addElement("age", 1000).buildEntityData("Controls");
    return ReadResponse.setSuccess().setData(ex).response();
  }
  
  @AfterQuery (entity = "Controls", serviceName="ControlService")
  public QueryResponse afterQueryOrders (QueryRequest req, QueryResponseAccessor res, ExtensionHelper h) {
    List<EntityData> dataList = res.getEntityDataList(); //original list
    List<EntityData> modifiedList = new ArrayList<EntityData>(dataList.size()); //modified list
    for(EntityData ed : dataList){ 
    	  String cdf1 = (String)ed.getElementValue("cdf1");
		  EntityData ex = EntityData.getBuilder(ed).addElement("age", cdf1).buildEntityData("Controls");
		  modifiedList.add(ex);
	  }
    return QueryResponse.setSuccess().setData(modifiedList).response();
  }
}
