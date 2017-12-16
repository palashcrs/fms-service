package com.get.edgepay.fms.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.get.edgepay.fms.constant.FmsResponseStatus;
import com.get.edgepay.fms.domain.dto.FMSResponse;
import com.get.edgepay.fms.service.FmsCacheService;


@RestController
public class FmsCacheController {

	private static final Logger log = LoggerFactory.getLogger(FmsCacheController.class);

	@Autowired
	private FmsCacheService fmsCacheService;

	/**
	 * REST API to reload all RuleTypes
	 * @return fmsResponse
	 */
	@RequestMapping(value = "/loadAllRuleTypes", method = RequestMethod.GET)
	public FMSResponse loadAllRuleTypes() {
		log.info("*********/fms/loadAllRuleTypes");
		FMSResponse fmsResponse = new FMSResponse();
		Object fmsOb = null;

		try {
			fmsCacheService.loadAllRuleEntities();
			fmsResponse.setFmsResponseCode(FmsResponseStatus.SUCCESS.toString());

		} catch (Exception e) {
			fmsResponse.setFmsResponseCode(FmsResponseStatus.FAILURE.toString());
			log.info("Exception occurs : " + e);
		} finally {
			fmsResponse.setFmsResult(fmsOb);
		}

		return fmsResponse;
	}

	/**
	 * REST API to reload all Rules
	 * @return fmsResponse
	 */
	@RequestMapping(value = "/loadAllRules", method = RequestMethod.GET)
	public FMSResponse loadAllPubRules() {
		log.info("*********/fms/loadAllRules");
		FMSResponse fmsResponse = new FMSResponse();
		Object fmsOb = null;

		try {
			fmsCacheService.loadRules();
			fmsResponse.setFmsResponseCode(FmsResponseStatus.SUCCESS.toString());

		} catch (Exception e) {
			fmsResponse.setFmsResponseCode(FmsResponseStatus.FAILURE.toString());
			log.info("Exception occurs : " + e);
		} finally {
			fmsResponse.setFmsResult(fmsOb);
		}

		return fmsResponse;
	}

	/**
	 * REST API to reload all Transactions
	 * @return fmsResponse
	 */
	@RequestMapping(value = "/loadAllTransactions", method = RequestMethod.GET)
	public FMSResponse loadAllTransactions() {
		log.info("*********/fms/loadAllTransactions");
		FMSResponse fmsResponse = new FMSResponse();
		Object fmsOb = null;

		try {
			fmsCacheService.loadAllTransactions();
			fmsResponse.setFmsResponseCode(FmsResponseStatus.SUCCESS.toString());

		} catch (Exception e) {
			fmsResponse.setFmsResponseCode(FmsResponseStatus.FAILURE.toString());
			log.info("Exception occurs : " + e);
		} finally {
			fmsResponse.setFmsResult(fmsOb);
		}

		return fmsResponse;
	}

}
