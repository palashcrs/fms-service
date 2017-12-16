package com.get.edgepay.fms.converter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.get.edgepay.fms.domain.dto.FmsRuleDto;
import com.get.edgepay.fms.domain.dto.FmsRuleEntityDto;
import com.get.edgepay.fms.domain.dto.FmsRuleMerchantDto;
import com.get.edgepay.fms.domain.model.FmsRule;
import com.get.edgepay.fms.domain.model.FmsRuleEntity;
import com.get.edgepay.fms.domain.model.FmsRuleMerchant;

public class FmsRuleObjectConverter {

	private static FmsRuleObjectConverter fmsRuleObjectConverter = new FmsRuleObjectConverter();

	private FmsRuleObjectConverter() {

	}

	public static FmsRuleObjectConverter getInstance() {
		return fmsRuleObjectConverter;
	}

	public List<FmsRuleDto> convertFMSRuleEntityToDTO(List<FmsRule> fmsRuleList) {
		List<FmsRuleDto> ruleDtoList = null;
		if (fmsRuleList != null && fmsRuleList.size() > 0) {
			ruleDtoList = new ArrayList<>();
			for (FmsRule t : fmsRuleList) {
				if (t != null) {
					FmsRuleDto fmsRuleDto = new FmsRuleDto();
					fmsRuleDto.setRuleId(t.getRuleId());
					fmsRuleDto.setAccessMode(t.getAccessMode());
					fmsRuleDto.setAction(t.getAction());
					fmsRuleDto.setRuleValue(t.getRuleValue());
					fmsRuleDto.setRuleUsageLimit(t.getRuleUsageLimit());
					fmsRuleDto.setMaxAllowedTxnAmount(t.getMaxAllowedTxnAmount());
					fmsRuleDto.setTimePeriod(t.getTimePeriod());
					fmsRuleDto.setAvsCheckRequired(t.isAvsCheckRequired());
					fmsRuleDto.setCreatedBy(t.getCreatedBy());
					fmsRuleDto.setCreationTime(t.getCreationTime());
					fmsRuleDto.setUpdatedBy(t.getUpdatedBy());
					fmsRuleDto.setUpdationTime(t.getUpdationTime());

					FmsRuleEntity fmsRuleEntity = t.getFmsRuleEntity();
					if (fmsRuleEntity != null) {
						FmsRuleEntityDto fmsRuleEntityDto = new FmsRuleEntityDto();
						fmsRuleEntityDto.setRuleEntityId(fmsRuleEntity.getRuleEntityId());
						fmsRuleEntityDto.setRuleEntityName(fmsRuleEntity.getRuleEntityName());
						fmsRuleDto.setFmsRuleEntity(fmsRuleEntityDto);
					}

					Set<FmsRuleMerchant> ruleMerchant = t.getRuleMerchant();
					if (ruleMerchant != null) {
						for (FmsRuleMerchant rm : ruleMerchant) {
							if (rm != null) {
								FmsRuleMerchantDto fmsRuleMerchantDto = new FmsRuleMerchantDto();
								fmsRuleMerchantDto.setId(rm.getId());
								fmsRuleMerchantDto.setMerchantId(rm.getMerchantId());
								Set<FmsRuleMerchantDto> rmDto = new HashSet<>();
								rmDto.add(fmsRuleMerchantDto);
								fmsRuleDto.setRuleMerchant(rmDto);
							}
						}
					}

					ruleDtoList.add(fmsRuleDto);
				}
			}
		}

		return ruleDtoList;
	}

	public List<FmsRule> convertFMSRuleDtoToEntity(List<FmsRuleDto> fmsRuleDtoList) {
		List<FmsRule> ruleList = null;
		if (fmsRuleDtoList != null && fmsRuleDtoList.size() > 0) {
			ruleList = new ArrayList<>();
			for (FmsRuleDto t : fmsRuleDtoList) {
				if (t != null) {
					FmsRule fmsRule = new FmsRule();
					fmsRule.setRuleId(t.getRuleId());
					fmsRule.setAccessMode(t.getAccessMode());
					fmsRule.setAction(t.getAction());
					fmsRule.setRuleValue(t.getRuleValue());
					fmsRule.setRuleUsageLimit(t.getRuleUsageLimit());
					fmsRule.setMaxAllowedTxnAmount(t.getMaxAllowedTxnAmount());
					fmsRule.setTimePeriod(t.getTimePeriod());
					fmsRule.setAvsCheckRequired(t.isAvsCheckRequired());
					fmsRule.setCreatedBy(t.getCreatedBy());
					fmsRule.setCreationTime(t.getCreationTime());
					fmsRule.setUpdatedBy(t.getUpdatedBy());
					fmsRule.setUpdationTime(t.getUpdationTime());

					FmsRuleEntityDto fmsRuleTypeDto = t.getFmsRuleEntity();
					if (fmsRuleTypeDto != null) {
						FmsRuleEntity fmsRuleType = new FmsRuleEntity();
						fmsRuleType.setRuleEntityId(fmsRuleTypeDto.getRuleEntityId());
						fmsRuleType.setRuleEntityName(fmsRuleTypeDto.getRuleEntityName());
						fmsRule.setFmsRuleEntity(fmsRuleType);
					}

					Set<FmsRuleMerchantDto> ruleMerchant = t.getRuleMerchant();
					if (ruleMerchant != null) {
						for (FmsRuleMerchantDto rmdto : ruleMerchant) {
							if (rmdto != null) {
								FmsRuleMerchant fmsRuleMerchant = new FmsRuleMerchant();
								fmsRuleMerchant.setId(rmdto.getId());
								fmsRuleMerchant.setMerchantId(rmdto.getMerchantId());
								Set<FmsRuleMerchant> rm = new HashSet<>();
								rm.add(fmsRuleMerchant);
								fmsRule.setRuleMerchant(rm);
							}
						}
					}

					ruleList.add(fmsRule);
				}
			}
		}

		return ruleList;
	}

}
