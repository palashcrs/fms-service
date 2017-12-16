package com.get.edgepay.fms.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FmsMainController {

	private static final Logger log = LoggerFactory.getLogger(FmsMainController.class);

	@RequestMapping(value = { "/", "/ping" }, method = RequestMethod.GET)
	public String ping() {
		return "FMS is up and running!...";
	}

}
