
package com.inspirage.ilct.xml;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Getter
@Setter
@XmlRootElement(name = "Feed")
@XmlAccessorType(XmlAccessType.FIELD)
public class FeedBean {

	@XmlElement(name = "FeedHeader")
	public FeedHeaderBean FeedHeader;

	@XmlElement(name="FeedContent")
	public FeedContentBean FeedContent;
}
