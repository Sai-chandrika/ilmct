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
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "RefNum", "Url" })
@AllArgsConstructor
public class LoadUrl {
	@XmlElement(name = "RefNum")
	public String RefNum;

	@XmlElement(name = "Url")
	public String Url;
}
