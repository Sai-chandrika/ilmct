package com.inspirage.ilct.xml;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "GeoFenceEvent","ILMCTOutXML","ILMCTOrderOutXML","LoadUrl","ShipmentCloseEvent","D1ProcessedEvent" })
public class FeedContentBean {
	@XmlElement(name = "GeoFenceEvent")
	public GeoFenceEvent GeoFenceEvent;

	@XmlElement(name = "ILMCTOutXML")
	public List<ILMCTOutXML> ILMCTOutXML;

	@XmlElement(name = "ILMCTOrderOutXML")
	public ILMCTOrderOutXML ILMCTOrderOutXML;

	@XmlElement(name = "LoadUrl")
	public List<LoadUrl> LoadUrl;

	@XmlElement(name = "ShipmentCloseEvent")
	public ShipmentCloseEvent ShipmentCloseEvent;

	@XmlElement(name = "D1ProcessedEvent")
	public com.inspirage.ilct.dto.bean.D1ProcessedEvent D1ProcessedEvent;
}
