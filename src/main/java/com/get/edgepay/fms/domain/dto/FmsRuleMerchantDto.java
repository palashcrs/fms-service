package com.get.edgepay.fms.domain.dto;

import java.util.Set;

import com.get.edgepay.fms.domain.model.FmsRule;

import lombok.Data;

@Data
public class FmsRuleMerchantDto {
	private Long id;

	private String merchantId;

	private Set<FmsRule> rules;
}
