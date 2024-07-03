package com.inspirage.ilct.dto.shipmentstatus;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"DomainName", "Xid"})
public class GidBean {

    @XmlElement(name = "otm:DomainName")
    private String DomainName;

    @XmlElement(name = "otm:Xid")
    private String Xid;
}
