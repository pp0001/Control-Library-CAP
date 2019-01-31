package com.sap.grc.ctrl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sap.cloud.sdk.service.prov.api.EntityData;
import com.sap.cloud.sdk.service.prov.api.ExtensionHelper;
import com.sap.cloud.sdk.service.prov.api.MessageContainer;
import com.sap.cloud.sdk.service.prov.api.annotations.AfterQuery;
import com.sap.cloud.sdk.service.prov.api.annotations.AfterRead;
import com.sap.cloud.sdk.service.prov.api.annotations.BeforeCreate;
import com.sap.cloud.sdk.service.prov.api.annotations.BeforeRead;
import com.sap.cloud.sdk.service.prov.api.constants.HttpStatusCodes;
import com.sap.cloud.sdk.service.prov.api.exits.BeforeCreateResponse;
import com.sap.cloud.sdk.service.prov.api.exits.BeforeReadResponse;
import com.sap.cloud.sdk.service.prov.api.operations.Create;
import com.sap.cloud.sdk.service.prov.api.request.CreateRequest;
import com.sap.cloud.sdk.service.prov.api.request.GenericRequest;
import com.sap.cloud.sdk.service.prov.api.request.QueryRequest;
import com.sap.cloud.sdk.service.prov.api.request.ReadRequest;
import com.sap.cloud.sdk.service.prov.api.request.UpdateRequest;
import com.sap.cloud.sdk.service.prov.api.response.CreateResponse;
import com.sap.cloud.sdk.service.prov.api.response.ErrorResponse;
import com.sap.cloud.sdk.service.prov.api.response.ErrorResponseBuilder;
import com.sap.cloud.sdk.service.prov.api.response.QueryResponse;
import com.sap.cloud.sdk.service.prov.api.response.QueryResponseAccessor;
import com.sap.cloud.sdk.service.prov.api.response.ReadResponse;
import com.sap.cloud.sdk.service.prov.api.response.ReadResponseAccessor;

import jpa.com.sap.grc.ctrl.test.Controls;

@Component
public class ControlService {

	private static final Logger LOG = LoggerFactory.getLogger(ControlService.class.getName());

	@Autowired
	private ControlRepository controlRepository;

	@Create(entity = "Controls", serviceName = "ControlService")
	public CreateResponse create(CreateRequest createRequest, ExtensionHelper extensionHelper) {
//		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("Tutorial");
//		EntityManager entityManager = entityManagerFactory.createEntityManager();
//		entityManager.getTransaction().begin();
		EntityData data = createRequest.getData();
		Controls control = new Controls();
		control.setID((Integer) data.getElementValue("ID"));
		control.setControlName((String) data.getElementValue("controlName"));
		control.setControlGroup((String) data.getElementValue("controlGroup"));
		control.setValidFrom((Date) data.getElementValue("validFrom"));
		control.setCdf1((String) data.getElementValue("cdf1"));
//		entityManager.persist(control);
//		entityManager.getTransaction().commit();
//		entityManager.clear();
//		entityManager.close();
		controlRepository.save(control);
		return CreateResponse.setSuccess().setData(control).response();
	}

	private static final List<String> CONTROL_ELEMENTS_MANDATORY = Arrays.asList("ID", "controlName", "validFrom",
			"validTo");

	private static final Map<String, String> CONTROL_ELEMENTS_VALUEHELP = new HashMap<String, String>() {
		private static final long serialVersionUID = 5238989057589476607L;
		{
			put("controlGroup", "ControlGroups");
			put("controlRiskLevel", "ControlRiskLevels");
			put("controlSignificance", "ControlSignificances");
			put("operationFrequency", "OperationFrequencies");
		}
	};

	private static final Map<String, String> ID_ELEMENTS_IN_ENTITIES = new HashMap<String, String>() {
		private static final long serialVersionUID = 3000049089863922216L;
		{
//			put("controlGroup", "groupName");
			put("controlRiskLevel", "riskLevelId");
			put("controlSignificance", "significanceId");
			put("operationFrequency", "frequencyId");
			put("control_ID", "ID");
		}
	};

	private static final Map<String, String> CONTROL_OWNER_ELEMENTS_MANDATORY = new HashMap<String, String>() {
		private static final long serialVersionUID = 7744636325941625887L;
		{
			put("control_ID", "Controls");
		}
	};

	@BeforeCreate(entitySet = "ControlOwners", serviceName = "ControlService")
	public BeforeCreateResponse beforeCreateControlOwners(CreateRequest createRequest,
			ExtensionHelper extensionHelper) {
		// Check assigned control exist
		Map<String, Object> controlOwnerData = createRequest.getData().asMap();
		Map<String, String> validationErrors = validateAssignedControl(controlOwnerData, createRequest,
				extensionHelper);
		if (!validationErrors.isEmpty()) {
			return BeforeCreateResponse
					.setError(constructErrorResponse(validationErrors, createRequest.getMessageContainer()).response());
		}
		return BeforeCreateResponse.setSuccess()
				.setEntityData(EntityData.createFromMap(controlOwnerData, Arrays.asList("ID"), "ControlOwners"))
				.response();
	}

	private Map<String, String> validateAssignedControl(Map<String, Object> data, GenericRequest request,
			ExtensionHelper extensionHelper) {
		Map<String, String> validationErrors = new HashMap<String, String>();

		CONTROL_OWNER_ELEMENTS_MANDATORY.forEach((element, entityName) -> checkElementExists(element, entityName, data,
				validationErrors, extensionHelper));
		if (request instanceof CreateRequest) {
			checkElementDoesNotExist("ID", "ControlOwners", data, validationErrors, extensionHelper);
		}
		return validationErrors;
	}

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

	private Map<String, String> validateControl(Map<String, Object> controlData, GenericRequest request,
			ExtensionHelper extensionHelper) {
		Map<String, String> validationErrors = new HashMap<String, String>();

		CONTROL_ELEMENTS_MANDATORY.forEach(element -> checkMandatory(controlData, validationErrors, element, request));
		CONTROL_ELEMENTS_VALUEHELP.forEach((element, entityName) -> checkElementExists(element, entityName, controlData,
				validationErrors, extensionHelper));
		if (request instanceof CreateRequest) {
			checkElementDoesNotExist("ID", "Controls", controlData, validationErrors, extensionHelper);
		}
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

	private void checkElementExists(String element, String entityName, Map<String, Object> entityData,
			Map<String, String> validationErrors, ExtensionHelper extensionHelper) {
		Object elementValue = entityData.get(element);
		if (!entityData.containsKey(element) || elementValue.toString().length() == 0) {
			return;
		}
		String entityId = ID_ELEMENTS_IN_ENTITIES.get(element);
		Map<String, Object> keys = new HashMap<String, Object>();
		keys.put(entityId, String.valueOf(elementValue));
		try {
			if (extensionHelper.getHandler().executeRead(entityName, keys, Arrays.asList(entityId)) == null) {
				validationErrors.put(element, "valueDoesNotExist");
			}
		} catch (Exception e) {
			LOG.warn(e.getMessage());
		}
	}

	private void checkElementDoesNotExist(String elementID, String entityName, Map<String, Object> entityData,
			Map<String, String> validationErrors, ExtensionHelper extensionHelper) {
		Object elementValue = entityData.get(elementID);
		if (elementValue == null) {
			return;
		}
		Map<String, Object> keys = new HashMap<String, Object>();
		keys.put(elementID, String.valueOf(elementValue));
		try {
			if (extensionHelper.getHandler().executeRead(entityName, keys, Arrays.asList(elementID)) != null) {
				validationErrors.put(elementID, "valueExists");
			}
		} catch (Exception e) {
			LOG.warn(e.getMessage());
		}
	}

	private ErrorResponseBuilder constructErrorResponse(Map<String, String> validationErrors,
			MessageContainer messageContainer) {
		validationErrors.entrySet().stream()
				.forEach(entry -> messageContainer.addErrorMessage(entry.getValue(), entry.getKey(), entry.getKey()));
		return ErrorResponse.getBuilder().setMessage("error").setStatusCode(HttpStatusCodes.BAD_REQUEST.getStatusCode())
				.addContainerMessages();
	}

	@BeforeRead(entity = "Controls", serviceName = "ControlService")
	public BeforeReadResponse beforeReadControls(ReadRequest req, ExtensionHelper h) {
		LOG.error("##### Orders - beforeReadOrders ########");
		return BeforeReadResponse.setSuccess().response();
	}

	@AfterRead(entity = "Controls", serviceName = "ControlService")
	public ReadResponse afterReadOrders(ReadRequest req, ReadResponseAccessor res, ExtensionHelper h) {
		EntityData ed = res.getEntityData();
		EntityData ex = EntityData.getBuilder(ed).addElement("age", 1000).buildEntityData("Controls");
		return ReadResponse.setSuccess().setData(ex).response();
	}

	@AfterQuery(entity = "Controls", serviceName = "ControlService")
	public QueryResponse afterQueryOrders(QueryRequest req, QueryResponseAccessor res, ExtensionHelper h) {
		List<EntityData> dataList = res.getEntityDataList(); // original list
		List<EntityData> modifiedList = new ArrayList<EntityData>(dataList.size()); // modified list
		for (EntityData ed : dataList) {
			String cdf1 = (String) ed.getElementValue("cdf1");
			EntityData ex = EntityData.getBuilder(ed).addElement("age", cdf1).buildEntityData("Controls");
			modifiedList.add(ex);
		}
		return QueryResponse.setSuccess().setData(modifiedList).response();
	}
}
