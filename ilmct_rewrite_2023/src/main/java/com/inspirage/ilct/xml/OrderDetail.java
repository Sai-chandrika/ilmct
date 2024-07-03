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
@XmlType(propOrder = { "OrderId", "OrderURL" })
public class OrderDetail {

    @XmlElement(name = "OrderId")
    public String OrderId;

    @XmlElement(name = "OrderURL")
    public String OrderURL;

}
