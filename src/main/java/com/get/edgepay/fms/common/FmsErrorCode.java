
package com.get.edgepay.fms.common;

public enum FmsErrorCode {

	REQ_NOT_FOUND("ERR00000"),
	INPUT_PARAM_NOT_FOUND("ERR00001"),
	RECORD_FETCH_ERROR("ERR00002"),
	DUPLICATE_RECORD("ERR00003"),
	CACHE_ERROR("ERR00004"),
	TIMEOUT_ERROR("ERR00005"),
	EXECUTE_ERROR("ERR00006"),
	DB_ERROR("ERR00007"),
	APPLICATION_ERROR("ERR00008");

	private String errorCode;

	private FmsErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorCode() {
		return errorCode;
	}

}