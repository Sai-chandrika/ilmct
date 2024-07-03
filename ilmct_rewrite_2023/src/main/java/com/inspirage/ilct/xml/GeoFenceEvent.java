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
@XmlType(propOrder = {"LoadId","StopNum","EventType","Location","EventDt","TruckID"})
public class GeoFenceEvent {

	@XmlElement(name="LoadId")
	public String LoadId;

	@XmlElement(name="StopNum")
	public Integer StopNum;

	@XmlElement(name="EventType")
	public String EventType;

	@XmlElement(name="Location")
	public LocationBean Location;

	@XmlElement(name="EventDt")
	public EventDtBean EventDt;

	@XmlElement(name="TruckID")
	public String TruckID;



}
