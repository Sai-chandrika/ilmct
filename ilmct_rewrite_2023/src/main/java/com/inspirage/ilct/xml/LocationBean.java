
package com.inspirage.ilct.xml;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@Getter
@Setter
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
//@XmlType(propOrder = {"Lat","Long"})
public class LocationBean {
	@XmlElement(name="Lat")
	public double Lat;
	@XmlElement(name="Long")
	public double Long;
}
