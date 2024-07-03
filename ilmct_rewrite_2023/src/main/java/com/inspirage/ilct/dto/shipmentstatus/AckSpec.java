package com.inspirage.ilct.dto.shipmentstatus;


import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.*;

@Getter
@Setter
@XmlRootElement(name = "otm:AckSpec")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"ComMethodGid", "AckOption", "ContactGid"})
public class AckSpec {

    @XmlElement(name = "otm:ComMethodGid")
    private ComMethodGid ComMethodGid;

    @XmlElement(name = "otm:ContactGid")
    private ContactGid ContactGid;

    @XmlElement(name = "otm:AckOption")
    private String AckOption;
}
