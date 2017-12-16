package com.get.edgepay.fms.service;

import java.util.List;
import java.util.concurrent.ExecutionException;

import com.get.edgepay.fms.domain.dto.FmsTransactionDto;
import com.get.edgepay.fms.exception.FmsException;

public interface FmsTransactionService {

	FmsTransactionDto calculateFraudAndSaveTxn(FmsTransactionDto fmsTxn, String merchantId) throws InterruptedException, FmsException, ExecutionException, Exception;

	List<FmsTransactionDto> getAllTransactions() throws Exception;
	
	List<FmsTransactionDto> searchTransactions(List<FmsTransactionDto> fmsTransactions, String searchStartDate, String searchEndDate) throws Exception;
}
