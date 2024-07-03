package com.inspirage.ilct.enums;

public enum FencingEventType {
	FENCE_IN, FENCE_OUT;

	public String eventXMLFormattedValue() {
		return this.name().replaceAll("_", "-");

	}
}
