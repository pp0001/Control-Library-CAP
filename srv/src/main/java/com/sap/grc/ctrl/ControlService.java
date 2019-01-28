package com.sap.grc.ctrl;

import java.util.ArrayList;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.cloud.sdk.service.prov.api.*;
import com.sap.cloud.sdk.service.prov.api.annotations.*;
import com.sap.cloud.sdk.service.prov.api.exits.*;
import com.sap.cloud.sdk.service.prov.api.request.*;
import com.sap.cloud.sdk.service.prov.api.response.*;
import com.sap.cloud.sdk.service.prov.api.constants.HttpStatusCodes;
import org.slf4j.*;
import com.sap.cloud.sdk.service.prov.api.operations.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import com.sap.grc.ctrl.jpa.com.sap.grc.ctrl.Controls;
import com.sap.grc.ctrl.jpa.com.sap.grc.ctrl.ControlOwners;
import org.apache.olingo.odata2.api.exception.ODataApplicationException;
import org.springframework.stereotype.Component;
import com.sap.cloud.sdk.service.prov.api.exception.DatasourceException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class ControlService {

	private static final Logger LOG = LoggerFactory.getLogger(ControlService.class.getName());

	@BeforeCreate(entitySet = "Controls", serviceName = "ControlService")
	public BeforeCreateResponse beforeCreateControls(CreateRequest createRequest, ExtensionHelper extensionHelper) {
		// Validate data
		Map<String, Object> controlData = createRequest.getData().asMap();
		Map<String, String> validationErrors = validateControl(controlData, createRequest, extensionHelper);
		if (!validationErrors.isEmpty()) {
			return BeforeCreateResponse
					.setError(constructErrorResponse(validationErrors, createRequest.getMessageContainer()).response());
		}
		return BeforeCreateResponse.setSuccess()
				.setEntityData(EntityData.createFromMap(controlData, Arrays.asList("ID"), "Controls")).response();
	}

	private static final List<String> CONTROL_ELEMENTS_MANDATORY = Arrays.asList("ID", "controlName", "validFrom", "validTo");
	private static final Map<String, String> CONTROL_ELEMENTS_VALUEHELP = new HashMap<String, String>() {
		private static final long serialVersionUID = 5238989057589476607L;
		{
			put("country", "country");
			put("controlGroup", "controlGroup");
		}
	};

	private Map<String, String> validateControl(Map<String, Object> controlData, GenericRequest request,
			ExtensionHelper extensionHelper) {
		Map<String, String> validationErrors = new HashMap<String, String>();

		CONTROL_ELEMENTS_MANDATORY.forEach(element -> checkMandatory(controlData, validationErrors, element, request));
		// CONTROL_ELEMENTS_VALUEHELP.forEach((element, entityName) ->
		// checkElementExists(element, entityName, controlData, validationErrors,
		// extensionHelper));
		// if (request instanceof CreateRequest) {
		// checkElementDoesNotExist(ELEMENT_PRODUCT_ID, ENTITY_PRODUCTS, controlData,
		// validationErrors, extensionHelper);
		// }
		return validationErrors;
	}

	private void checkMandatory(Map<String, Object> entityData, Map<String, String> validationErrors, String element,
			GenericRequest request) {
		Object value = entityData.get(element);
		boolean valueIsEmpty = value instanceof String && ((String) value).trim().isEmpty();
		if (request instanceof CreateRequest && (value == null || valueIsEmpty) || request instanceof UpdateRequest
				&& entityData.containsKey(element) && (value == null || valueIsEmpty)) {
			validationErrors.put(element, "valueIsMandatory");
		}
	}

	private ErrorResponseBuilder constructErrorResponse(Map<String, String> validationErrors,
			MessageContainer messageContainer) {
		validationErrors.entrySet().stream()
				.forEach(entry -> messageContainer.addErrorMessage(entry.getValue(), entry.getKey(), entry.getKey()));
		return ErrorResponse.getBuilder().setMessage("error").setStatusCode(HttpStatusCodes.BAD_REQUEST.getStatusCode())
				.addContainerMessages();
	}
	
	@Autowired
	private ControlRepository controlRepository;

	@Create(entity = "Controls", serviceName = "ControlService")
	public CreateResponse create(CreateRequest createRequest, ExtensionHelper extensionHelper){
		Controls control = new Controls();
		  control.setID(1);		
		  control.setControlName("controlName");
		controlRepository.save(control);
		return CreateResponse.setSuccess().setData(createRequest.getData()).response();
	}
	// public CreateResponse createOrder(CreateRequest createRequest, ExtensionHelper extensionHelper)
	// 		throws NamingException, ODataApplicationException, DatasourceException {
	// 	// EntityManagerFactory emfactory = Persistence.createEntityManagerFactory( "default" );
 //      //EntityManager em = (EntityManager) (new InitialContext()).lookup("java:comp/env/jpa/default/pc");
 //      Map addedOrOverridenProperties = new HashMap();

	// 	// Let's suppose we are using Hibernate as JPA provider
	// 	addedOrOverridenProperties.put("javax.persistence.jdbc.url", "jdbc:sap://10.253.93.93:30041/?currentschema=CONTROL_LIBRARY_CAP_CONTROL_HDI_CONTAINER_1");
	// 	addedOrOverridenProperties.put("javax.persistence.jdbc.driver", "com.sap.db.jdbc.Driver");
	// 	addedOrOverridenProperties.put("javax.persistence.jdbc.user", "SBSS_80684906683260027450210628599554660760477956452509382328397847281");
 //   	addedOrOverridenProperties.put("javax.persistence.jdbc.password", 
 //   		"Pi0PVrER34lJkFRM79JBRekiA4SOZp71w0tYTQboiHRh-IIzBCu0XlaTliLZJpD4JqxiueJPK73ROgXbJ-XHT5YgClxr-fLA7Z9dJrXZV.U934C52L4rn4hrAEffcJtA");
 //     EntityManagerFactory emfactory = Persistence.createEntityManagerFactory("test", addedOrOverridenProperties);
 //     EntityManager entitymanager = emfactory.createEntityManager( );
 //     entitymanager.getTransaction( ).begin( );


 //     Controls control = new Controls();
	//   control.setID(1);		
	//   control.setControlName("controlName");
 //     entitymanager.persist( control );
	// 	return CreateResponse.setSuccess().setData(createRequest.getData()).response();
	// }

	@BeforeRead(entity = "Controls", serviceName = "ControlService")
	public BeforeReadResponse beforeReadControls(ReadRequest req, ExtensionHelper h) {
		LOG.error("##### Orders - beforeReadOrders ########");
		return BeforeReadResponse.setSuccess().response();
	}

	@AfterRead(entity = "Controls", serviceName = "ControlService")
	public ReadResponse afterReadOrders(ReadRequest req, ReadResponseAccessor res, ExtensionHelper h) {
		EntityData ed = res.getEntityData();
		EntityData ex = EntityData.getBuilder(ed).addElement("cdf2", "1000").buildEntityData("Controls");
		return ReadResponse.setSuccess().setData(ex).response();
	}

	@AfterQuery(entity = "Controls", serviceName = "ControlService")
	public QueryResponse afterQueryOrders(QueryRequest req, QueryResponseAccessor res, ExtensionHelper h) {
		List<EntityData> dataList = res.getEntityDataList(); // original list
		List<EntityData> modifiedList = new ArrayList<EntityData>(dataList.size()); // modified list
		for (EntityData ed : dataList) {
			String cdf1 = (String) ed.getElementValue("cdf1");
			EntityData ex = EntityData.getBuilder(ed).addElement("cdf1", cdf1).buildEntityData("Controls");
			modifiedList.add(ex);
		}
		return QueryResponse.setSuccess().setData(modifiedList).response();
	}
}
