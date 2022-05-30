package com.experitest.versiononeintegration;

public enum TEST_STATUS {
	PASSED(129), FAILED(155);

	private int value;

	public int getValue() {
		return value;
	}

	private TEST_STATUS(int value) {
		this.value = value;
	}

}
