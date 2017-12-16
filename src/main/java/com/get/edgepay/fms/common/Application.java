package com.get.edgepay.fms.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
public class Application {

	private static final Logger log = LoggerFactory.getLogger(Application.class);
	public static int TOMCAT_THREAD_COUNT = 100;

	static {
		try {
			TOMCAT_THREAD_COUNT = Integer.parseInt(System.getProperty("tomcat.threads"));
			log.info("Total Tomcat threads configured in properties file: " + TOMCAT_THREAD_COUNT);
		} catch (Exception e) {
			log.info("Exception occurs! Total Tomcat threads: " + TOMCAT_THREAD_COUNT);
		}
	}
}
