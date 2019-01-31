using com.sap.grc.ctrl.test from '../db/data-model';

service ControlService {
  entity ControlOwners @insert as projection on test.ControlOwners;
  entity ControlGroups @insert as projection on test.ControlGroup;
  entity Controls @read @insert @update as projection on test.Controls;
  entity texts @readonly as projection on test.Controls_texts;
}
