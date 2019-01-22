namespace com.sap.grc.ctrl;
using { User, Country } from '@sap/cds/common';

entity Controls {
  key ID : Integer;
  controlName  : localized String;
  controlDesc  : localized String;
  country : Country;
  controlGroup : String;
  validFrom : Date;
  validTo : Date;
  cdf1 : String;
  cdf2 : String;
  cdf3 : String;
  cdf4 : String;
  cdf5 : String;
  cdf6 : String;
  cdf7 : String;
  cdf8 : String;
  cdf9 : String;
  cdf10 : String;
  controlOwners : Association to many ControlOwners on controlOwners.control = $self;
  localized : Association to  Controls_texts
  on localized.locale = session_context('locale')
  and localized.control = $self ;
}

entity Controls_texts {
    key control  : Association to Controls;
    key locale : String(2);
    controlName : String;
    controlDesc : String;
  }
  
entity ControlOwners {
  key ID : Integer;
  control : Association to Controls;
  ownerName   : String;
  ownerEmail : String;
}
