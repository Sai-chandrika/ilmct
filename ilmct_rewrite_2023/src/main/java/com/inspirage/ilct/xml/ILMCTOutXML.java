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
@XmlType(propOrder = { "StatusUpdate", "ETAUpdate", "LoadId", "LoadRefNum", "LoadUrl" })
public class ILMCTOutXML {

	@XmlElement(name = "StatusUpdate")
	public StatusUpdateBean StatusUpdate;

	@XmlElement(name = "ETAUpdate")
	public ETAUpdateBean ETAUpdate;

	@XmlElement(name = "LoadId")
	public String LoadId;

	@XmlElement(name = "LoadRefNum")
	public String LoadRefNum;

	@XmlElement(name = "LoadUrl")
	public String LoadUrl;
}
