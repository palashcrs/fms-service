package com.get.edgepay.fms.service;

import java.util.List;

import com.get.edgepay.fms.domain.dto.FmsTransactionDto;

public interface FmsCacheService {

	public void loadAllRuleEntities() throws Exception;

	public void loadRules() throws Exception;

	public List<FmsTransactionDto> loadAllTransactions() throws Exception;

}
