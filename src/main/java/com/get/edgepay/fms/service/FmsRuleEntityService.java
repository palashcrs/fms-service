package com.get.edgepay.fms.service;

import java.util.List;

import com.get.edgepay.fms.domain.dto.FmsRuleEntityDto;

public interface FmsRuleEntityService {

	int createNewRuleEntity(List<FmsRuleEntityDto> fmsRuleTypes) throws Exception;

	public List<FmsRuleEntityDto> getAllRuleEntities() throws Exception;
}
