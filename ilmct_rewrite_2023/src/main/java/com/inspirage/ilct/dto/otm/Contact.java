package com.inspirage.ilct.dto.otm;

import com.fasterxml.jackson.annotation.*;
import com.inspirage.ilct.dto.GidHolder;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"IsPrimaryContact",
"UseMessageHub",
"ContactGid",
"TransactionCode",
"LocationGid",
"EmailAddress",
"ConsolidatedNotifyOnly",
"IsNotificationOn",
"Phone1",
"Phone2",
"LanguageSpoken"
})
public class Contact {

@JsonProperty("IsPrimaryContact")
public String isPrimaryContact;
@JsonProperty("UseMessageHub")
public String useMessageHub;
@JsonProperty("ContactGid")
public GidHolder contactGid;
@JsonProperty("TransactionCode")
public String transactionCode;
@JsonProperty("LocationGid")
public GidHolder locationGid;
@JsonProperty("EmailAddress")
public String emailAddress;
@JsonProperty("ConsolidatedNotifyOnly")
public String consolidatedNotifyOnly;
@JsonProperty("IsNotificationOn")
public String isNotificationOn;
@JsonProperty("Phone1")
public Long phone1;
@JsonProperty("Phone2")
public Long phone2;
@JsonProperty("LanguageSpoken")
public String languageSpoken;
@JsonIgnore
private  Map<String, Object> additionalProperties = new HashMap<String, Object>();

@JsonAnyGetter
public Map<String, Object> getAdditionalProperties() {
return this.additionalProperties;
}

@JsonAnySetter
public void setAdditionalProperty(String name, Object value) {
this.additionalProperties.put(name, value);
}

}