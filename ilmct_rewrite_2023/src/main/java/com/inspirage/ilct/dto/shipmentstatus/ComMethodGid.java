package com.inspirage.ilct.dto.shipmentstatus;


import javax.xml.bind.annotation.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XmlRootElement(name = "otm:ComMethodGid")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"Gid"})
public class ComMethodGid {

    @XmlElement(name = "otm:Gid")
    private GidBean Gid;
}
