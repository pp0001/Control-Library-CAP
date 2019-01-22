using { com.sap.grc.ctrl, sap.common } from '../db/data-model';

service ControlService {
  entity Owners @readonly as projection on ctrl.ControlOwners;
  entity Controls @readonly as projection on ctrl.Controls;
  entity texts @readonly as projection on ctrl.Controls_texts;
}
