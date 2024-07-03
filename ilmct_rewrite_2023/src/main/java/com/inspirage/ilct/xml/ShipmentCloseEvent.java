package com.inspirage.ilct.xml;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@Getter
@Setter
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "LoadId",  "EventType",  "EventDt" })
public class ShipmentCloseEvent {

	@XmlElement(name = "LoadId")
	public String LoadId;

	@XmlElement(name = "EventType")
	public String EventType;

	@XmlElement(name = "EventDt")
	public EventDtBean EventDt;

}
