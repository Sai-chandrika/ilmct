package com.inspirage.ilct.xml;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.*;

@Getter
@Setter
@XmlRootElement(name = "FeedHeader")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"FeedSource", "FeedRefNum", "FeedGenDtTime"})
public class FeedHeaderBean {
	@XmlElement(name = "FeedSource")
	public String FeedSource = "ILMCT";
	@XmlElement(name = "FeedRefNum")
	public String FeedRefNum;
	@XmlElement(name = "FeedGenDtTime")
	public String FeedGenDtTime;

}
