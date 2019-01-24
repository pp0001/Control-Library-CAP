using com.sap.grc.ctrl from '../db/data-model';

service ControlService {
  entity ControlOwners @insert as projection on ctrl.ControlOwners;
  entity ControlGroups @insert as projection on ctrl.ControlGroup;
  entity Controls @read @insert @update as projection on ctrl.Controls;
  entity texts @readonly as projection on ctrl.Controls_texts;
}
