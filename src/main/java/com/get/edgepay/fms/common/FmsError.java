package com.get.edgepay.fms.common;

import java.io.Serializable;

public class FmsError implements Serializable {

	private final static long serialVersionUID = 1L;

	private String fmsErrorCode;

	private String fmsErrorMessage;

	public FmsError() {

	}

	public String getFmsErrorCode() {
		return fmsErrorCode;
	}

	public void setFmsErrorCode(String fmsErrorCode) {
		this.fmsErrorCode = fmsErrorCode;
	}

	public String getFmsErrorMessage() {
		return fmsErrorMessage;
	}

	public void setFmsErrorMessage(String fmsErrorMessage) {
		this.fmsErrorMessage = fmsErrorMessage;
	}

	@Override
	public String toString() {
		return "FMSError [fmsErrorCode=" + fmsErrorCode + ", fmsErrorMessage=" + fmsErrorMessage + "]";
	}

}
