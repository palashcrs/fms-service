package com.get.edgepay.fms.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.get.edgepay.fms.common.FmsErrorCode;
import com.get.edgepay.fms.constant.FmsConstant;
import com.get.edgepay.fms.constant.FmsResponseStatus;
import com.get.edgepay.fms.domain.dto.FMSRequest;
import com.get.edgepay.fms.domain.dto.FMSResponse;
import com.get.edgepay.fms.domain.dto.FmsRuleDto;
import com.get.edgepay.fms.exception.CacheException;
import com.get.edgepay.fms.exception.DBException;
import com.get.edgepay.fms.exception.DuplicateRecordException;
import com.get.edgepay.fms.exception.InputParamNotFoundException;
import com.get.edgepay.fms.exception.RecordFetchException;
import com.get.edgepay.fms.exception.RequestNotFoundException;
import com.get.edgepay.fms.service.FmsRuleService;
import com.get.edgepay.fms.util.FmsCommonUtil;


@RestController
public class FmsRuleController {

	private static final Logger log = LoggerFactory.getLogger(FmsRuleController.class);

	@Autowired
	private FmsRuleService fmsRuleService;

	/**
	 * REST API to create new Rule
	 * @param fmsRequest
	 * @return fmsResponse
	 */
	@RequestMapping(value = "/newRule", method = RequestMethod.POST)
	public FMSResponse addRule(@RequestBody FMSRequest fmsRequest) {
		log.info("*********/fms/newrule | FMSRequest : " + fmsRequest);
		FMSResponse fmsResponse = new FMSResponse();
		Object fmsOb = null;

		try {
			FmsCommonUtil.getInstance().validateInput(fmsRequest, FmsConstant.NEW_RULE_FLAG);
			
			String userId = fmsRequest.getUserId();
			String merchantId = fmsRequest.getMerchantId();
			int recordsCreated = fmsRuleService.createRule(fmsRequest.getFmsRules(), userId, merchantId);
			if (recordsCreated > 0) {
				fmsResponse.setFmsResponseCode(FmsResponseStatus.SUCCESS.toString());
			}

		} catch (Exception e) {
			fmsResponse.setFmsResponseCode(FmsResponseStatus.FAILURE.toString());
			log.info("Exception occurs : " + e);
			if (e instanceof RequestNotFoundException) {
				FmsCommonUtil.getInstance().addError(fmsResponse, FmsErrorCode.REQ_NOT_FOUND);
			} else if (e instanceof InputParamNotFoundException) {
				FmsCommonUtil.getInstance().addError(fmsResponse, FmsErrorCode.INPUT_PARAM_NOT_FOUND);
			} else if (e instanceof DBException) {
				FmsCommonUtil.getInstance().addError(fmsResponse, FmsErrorCode.DB_ERROR);
			} else if (e instanceof DuplicateRecordException) {
				FmsCommonUtil.getInstance().addError(fmsResponse, FmsErrorCode.DUPLICATE_RECORD);
			} else if (e instanceof CacheException) {
				FmsCommonUtil.getInstance().addError(fmsResponse, FmsErrorCode.CACHE_ERROR);
			}
		} finally {
			fmsResponse.setFmsResult(fmsOb);
		}

		return fmsResponse;
	}
	
	/**
	 * REST API to fetch all Rules
	 * @param fmsRequest
	 * @return fmsResponse
	 */
	@RequestMapping(value = "/allRules", method = RequestMethod.POST)
	public FMSResponse getAllRules(@RequestBody FMSRequest fmsRequest) {
		log.info("*********/fms/allrules | FMSRequest : " + fmsRequest);
		FMSResponse fmsResponse = new FMSResponse();
		List<FmsRuleDto> allRuleS = null;
		
		try {
			FmsCommonUtil.getInstance().validateInput(fmsRequest, FmsConstant.ALL_RULE_FLAG);
			
			allRuleS = fmsRuleService.getAllRuleS();
			fmsResponse.setFmsResponseCode(FmsResponseStatus.SUCCESS.toString());

		} catch (Exception e) {
			fmsResponse.setFmsResponseCode(FmsResponseStatus.FAILURE.toString());
			log.info("Exception occurs : " + e);
			if (e instanceof RecordFetchException) {
				FmsCommonUtil.getInstance().addError(fmsResponse, FmsErrorCode.RECORD_FETCH_ERROR);
			} else if (e instanceof CacheException) {
				FmsCommonUtil.getInstance().addError(fmsResponse, FmsErrorCode.CACHE_ERROR);
			}
		} finally {
			fmsResponse.setFmsResult(allRuleS);
		}

		return fmsResponse;
	}
	
	/**
	 * REST API to fetch global Rules
	 * @param fmsRequest
	 * @return fmsResponse
	 */
	@RequestMapping(value = "/globalRules", method = RequestMethod.POST)
	public FMSResponse getGlobalRules(@RequestBody FMSRequest fmsRequest) {
		log.info("*********/fms/globalRules | FMSRequest : " + fmsRequest);
		FMSResponse fmsResponse = new FMSResponse();
		List<FmsRuleDto> globalRuleS = null;
		
		try {
			FmsCommonUtil.getInstance().validateInput(fmsRequest, FmsConstant.GLOBAL_RULE_FLAG);
			
			globalRuleS = fmsRuleService.getGlobalRules();
			fmsResponse.setFmsResponseCode(FmsResponseStatus.SUCCESS.toString());

		} catch (Exception e) {
			fmsResponse.setFmsResponseCode(FmsResponseStatus.FAILURE.toString());
			log.info("Exception occurs : " + e);
			if (e instanceof CacheException) {
				FmsCommonUtil.getInstance().addError(fmsResponse, FmsErrorCode.CACHE_ERROR);
			}
		} finally {
			fmsResponse.setFmsResult(globalRuleS);
		}
		
		return fmsResponse;
	}
}
