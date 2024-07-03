package com.inspirage.ilct.dto.shipmentstatus;


import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.*;

@Getter
@Setter
@XmlRootElement(name = "otm:ContactGid")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"Gid"})
public class ContactGid {

    @XmlElement(name = "otm:Gid")
    private GidBean Gid;
}
