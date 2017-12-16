package com.get.edgepay.fms.test;

import java.text.ParseException;

public class FMSTest {

	public static void main(String[] args) throws ParseException {
		String[] octets = "192.168.9.9".split(java.util.regex.Pattern.quote("."));
		System.out.println(octets[0]);
	}

}
