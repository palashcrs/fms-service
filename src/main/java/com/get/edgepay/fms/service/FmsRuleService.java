package com.get.edgepay.fms.service;

import java.util.List;

import com.get.edgepay.fms.domain.dto.FmsRuleDto;

public interface FmsRuleService {

	int createRule(List<FmsRuleDto> fmsRulesDtoList, String userId, String merchantId) throws Exception;

	List<FmsRuleDto> getAllRuleS() throws Exception;
	
	List<FmsRuleDto> getGlobalRules() throws Exception;
}
