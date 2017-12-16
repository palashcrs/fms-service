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
import com.get.edgepay.fms.domain.dto.FmsRuleEntityDto;
import com.get.edgepay.fms.exception.CacheException;
import com.get.edgepay.fms.exception.DuplicateRecordException;
import com.get.edgepay.fms.exception.InputParamNotFoundException;
import com.get.edgepay.fms.exception.RecordFetchException;
import com.get.edgepay.fms.exception.RequestNotFoundException;
import com.get.edgepay.fms.service.FmsRuleEntityService;
import com.get.edgepay.fms.util.FmsCommonUtil;


@RestController
public class FmsRuleEntityController {

	private static final Logger log = LoggerFactory.getLogger(FmsRuleEntityController.class);

	@Autowired
	private FmsRuleEntityService fmsRuleEntityService;

	/**
	 * REST API to create a new RuleEntity
	 * @param fmsRequest
	 * @return fmsResponse
	 */
	@RequestMapping(value = "/newRuleEntity", method = RequestMethod.POST)
	public FMSResponse addRuleEntity(@RequestBody FMSRequest fmsRequest) {
		//log.info("*********/fms/newRuleEntity | FMSRequest : " + fmsRequest);
		FMSResponse fmsResponse = new FMSResponse();
		Object fmsOb = null;

		try {
			FmsCommonUtil.getInstance().validateInput(fmsRequest, FmsConstant.NEW_RULEENTITY_FLAG);

			int recordsCreated = fmsRuleEntityService.createNewRuleEntity(fmsRequest.getFmsRuleEntities());
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
	 * REST API to fetch all RuleEntities
	 * @param fmsRequest
	 * @return fmsResponse
	 */
	@RequestMapping(value = "/allRuleEntities", method = RequestMethod.POST)
	public FMSResponse getAllRuleEntities(@RequestBody FMSRequest fmsRequest) {
		log.info("*********/fms/allRuleEntities | FMSRequest : " + fmsRequest);
		FMSResponse fmsResponse = new FMSResponse();
		List<FmsRuleEntityDto> ruleEntityDtoList = null;

		try {
			ruleEntityDtoList = fmsRuleEntityService.getAllRuleEntities();
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
			fmsResponse.setFmsResult(ruleEntityDtoList);
		}

		return fmsResponse;
	}

}
