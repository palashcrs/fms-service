package com.get.edgepay.fms.controller;

import java.util.List;
import java.util.concurrent.ExecutionException;

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
import com.get.edgepay.fms.domain.dto.FmsTransactionDto;
import com.get.edgepay.fms.exception.CacheException;
import com.get.edgepay.fms.exception.DBException;
import com.get.edgepay.fms.exception.FmsException;
import com.get.edgepay.fms.exception.InputParamNotFoundException;
import com.get.edgepay.fms.exception.RecordFetchException;
import com.get.edgepay.fms.exception.RequestNotFoundException;
import com.get.edgepay.fms.service.FmsCacheService;
import com.get.edgepay.fms.service.FmsTransactionService;
import com.get.edgepay.fms.util.FmsCommonUtil;


@RestController
public class FmsTxnController {

	private static final Logger log = LoggerFactory.getLogger(FmsTxnController.class);

	@Autowired
	private FmsTransactionService fmsTransactionService;

	/**
	 * REST API to detect fraud in a transaction
	 * @param fmsRequest
	 * @return fmsResponse
	 */
	@RequestMapping(value = "/fraudCalc", method = RequestMethod.POST)
	public FMSResponse calcFraud(@RequestBody FMSRequest fmsRequest) {
		log.info("*********/fms/fraudcalc | FMSRequest : " + fmsRequest);
		log.info("test");
		FMSResponse fmsResponse = new FMSResponse();
		Object fmsOb = null;
		FmsTransactionDto fmsTxnReq = null;

		try {
			FmsCommonUtil.getInstance().validateInput(fmsRequest, FmsConstant.TXN_FRAUD_CALC_FLAG);
			
			String merchantId = fmsRequest.getMerchantId();
			fmsTxnReq = fmsRequest.getFmsTransactions().get(0);

			fmsTxnReq = fmsTransactionService.calculateFraudAndSaveTxn(fmsTxnReq, merchantId);
			fmsOb = fmsTxnReq;

			fmsResponse.setFmsResponseCode(FmsResponseStatus.SUCCESS.toString());

		} catch (Exception e) {
			fmsResponse.setFmsResponseCode(FmsResponseStatus.FAILURE.toString());
			if (e instanceof RequestNotFoundException) {
				FmsCommonUtil.getInstance().addError(fmsResponse, FmsErrorCode.REQ_NOT_FOUND);
			} else if (e instanceof InputParamNotFoundException) {
				FmsCommonUtil.getInstance().addError(fmsResponse, FmsErrorCode.INPUT_PARAM_NOT_FOUND);
			} else if (e instanceof InterruptedException) {
				FmsCommonUtil.getInstance().addError(fmsResponse, FmsErrorCode.TIMEOUT_ERROR);
			} else if (e instanceof ExecutionException) {
				FmsCommonUtil.getInstance().addError(fmsResponse, FmsErrorCode.EXECUTE_ERROR);
			} else if (e instanceof DBException) {
				FmsCommonUtil.getInstance().addError(fmsResponse, FmsErrorCode.DB_ERROR);
			} else if (e instanceof CacheException) {
				FmsCommonUtil.getInstance().addError(fmsResponse, FmsErrorCode.CACHE_ERROR);
			} else if (e instanceof FmsException) {
				FmsCommonUtil.getInstance().addError(fmsResponse, FmsErrorCode.APPLICATION_ERROR);
			}
			log.error(e.getMessage(), e);
		} finally {
			fmsResponse.setFmsResult(fmsOb);
		}

		return fmsResponse;
	}
	
	/**
	 * REST API to update a transaction
	 * @param fmsRequest
	 * @return fmsResponse
	 */
	@RequestMapping(value = "/updateTxn", method = RequestMethod.POST)
	public FMSResponse updateTxn(@RequestBody FMSRequest fmsRequest) {
		log.info("*********/fms/updateTxn | FMSRequest : " + fmsRequest);
		FMSResponse fmsResponse = new FMSResponse();
		Object fmsOb = null;
		
		return fmsResponse;
	}
	
	/**
	 * REST API to fetch all Transactions
	 * @return fmsResponse
	 */
	@RequestMapping(value = "/allTxns", method = RequestMethod.GET)
	public FMSResponse getAllTransactions() {
		log.info("*********/fms/allTxns");
		FMSResponse fmsResponse = new FMSResponse();
		List<FmsTransactionDto> allTxns = null;
		
		try {
			allTxns = fmsTransactionService.getAllTransactions();
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
			fmsResponse.setFmsResult(allTxns);
		}

		return fmsResponse;
	}
	
	/**
	 * REST API to fetch search Transactions
	 * @return fmsResponse
	 */
	@RequestMapping(value = "/filterTxns", method = RequestMethod.POST)
	public FMSResponse searchTransaction(@RequestBody FMSRequest fmsRequest) {
		log.info("*********/fms/filterTxns" + fmsRequest);
		FMSResponse fmsResponse = new FMSResponse();
		List<FmsTransactionDto> filteredTxns = null;
		
		try {
			FmsCommonUtil.getInstance().validateInput(fmsRequest, FmsConstant.TXN_SEARCH_FLAG);
			
			List<FmsTransactionDto> fmsTransactions = fmsRequest.getFmsTransactions();
			String searchStartDate = fmsRequest.getSearchStartDate();
			String searchEndDate = fmsRequest.getSearchEndDate();
			filteredTxns = fmsTransactionService.searchTransactions(fmsTransactions, searchStartDate, searchEndDate);
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
			fmsResponse.setFmsResult(filteredTxns);
		}

		return fmsResponse;
		
	}

}
