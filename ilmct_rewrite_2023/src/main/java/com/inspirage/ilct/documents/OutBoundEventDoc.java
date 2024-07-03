package com.inspirage.ilct.documents;

import com.inspirage.ilct.xml.FeedBean;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@CompoundIndex(def = "{createdDate:-1}")
@Document(collection = "out_bound_event")
public class OutBoundEventDoc extends BaseDoc {
	private FeedBean feedBean;
	private String outBoundEventResponse;

	public FeedBean getFeedBean() {
		return feedBean;
	}

	public void setFeedBean(FeedBean feedBean) {
		this.feedBean = feedBean;
	}

	public String getOutBoundEventResponse() {
		return outBoundEventResponse;
	}

	public void setOutBoundEventResponse(String outBoundEventResponse) {
		this.outBoundEventResponse = outBoundEventResponse;
	}
}
