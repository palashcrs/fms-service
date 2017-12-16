package com.get.edgepay.fms.converter;

import java.util.ArrayList;
import java.util.List;

import com.get.edgepay.fms.domain.dto.FmsRuleEntityDto;
import com.get.edgepay.fms.domain.model.FmsRuleEntity;

public class FmsRuleEntityObjectConverter {

	private static FmsRuleEntityObjectConverter fmsRuleEntityObjectConverter = new FmsRuleEntityObjectConverter();

	private FmsRuleEntityObjectConverter() {

	}

	public static FmsRuleEntityObjectConverter getInstance() {
		return fmsRuleEntityObjectConverter;
	}

	public List<FmsRuleEntityDto> convertFMSRuleTypeEntityToDTO(List<FmsRuleEntity> fmsRuleEntityList) {
		List<FmsRuleEntityDto> ruleTypeDtoList = null;
		if (fmsRuleEntityList != null && fmsRuleEntityList.size() > 0) {
			ruleTypeDtoList = new ArrayList<>();
			for (FmsRuleEntity t : fmsRuleEntityList) {
				FmsRuleEntityDto rt = new FmsRuleEntityDto();
				rt.setRuleEntityId(t.getRuleEntityId());
				rt.setRuleEntityName(t.getRuleEntityName());
				//rt.setRules(t.getRules());//need to change here
				ruleTypeDtoList.add(rt);
			}
		}

		return ruleTypeDtoList;
	}

	public List<FmsRuleEntity> convertFMSRuleTypeDtoToEntity(List<FmsRuleEntityDto> fmsRuleTypeDtoList) {
		List<FmsRuleEntity> ruleTypeList = null;
		if (fmsRuleTypeDtoList != null && fmsRuleTypeDtoList.size() > 0) {
			ruleTypeList = new ArrayList<>();
			for (FmsRuleEntityDto t : fmsRuleTypeDtoList) {
				FmsRuleEntity fmsRuleType = new FmsRuleEntity();
				fmsRuleType.setRuleEntityId(t.getRuleEntityId());
				fmsRuleType.setRuleEntityName(t.getRuleEntityName());
				ruleTypeList.add(fmsRuleType);
			}
		}

		return ruleTypeList;
	}

}
