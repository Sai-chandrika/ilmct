
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
@XmlType(propOrder = { "LoadId", "StopNum", "CurrentLocation", "EventDt", "TruckID", "ETA", "ETD" })
public class ETAUpdateBean {

	@XmlElement(name = "LoadId")
	public String LoadId;

	@XmlElement(name = "StopNum")
	public Integer StopNum;

	@XmlElement(name = "CurrentLocation")
	public LocationBean CurrentLocation;

	@XmlElement(name = "EventDt")
	public EventDtBean EventDt;

	@XmlElement(name = "TruckID")
	public String TruckID;

	@XmlElement(name = "ETA")
	public ETABean ETA;

	@XmlElement(name = "ETD")
	public ETDBean ETD;

}
