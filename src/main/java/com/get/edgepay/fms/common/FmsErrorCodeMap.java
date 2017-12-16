package com.get.edgepay.fms.common;

import java.util.HashMap;
import java.util.Map;

public class FmsErrorCodeMap {
	private static final Map<String, String> fmsErrCodeMap = new HashMap<String, String>();

	static {
		fmsErrCodeMap.put("ERR00000", "Request is not found!");
		fmsErrCodeMap.put("ERR00001", "Required Input parameters not found!");
		fmsErrCodeMap.put("ERR00002", "Record fetch error!");
		fmsErrCodeMap.put("ERR00003", "Record exists!");
		fmsErrCodeMap.put("ERR00004", "Cache db errors!");
		fmsErrCodeMap.put("ERR00005", "Timed Out!");
		fmsErrCodeMap.put("ERR00006", "Could not process request!");
		fmsErrCodeMap.put("ERR00007", "Database error! Please try again");
		fmsErrCodeMap.put("ERR00008", "Internal Error occured, Plesase try again later");
	}

	public static String getMsg(String key) {
		return fmsErrCodeMap.get(key);
	}
}
