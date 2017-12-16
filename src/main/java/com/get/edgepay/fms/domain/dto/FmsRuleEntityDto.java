package com.get.edgepay.fms.domain.dto;

import java.io.Serializable;
import java.util.Set;

import lombok.Data;

@Data
public class FmsRuleEntityDto implements Serializable {

	private final static long serialVersionUID = 1L;

	private Long ruleEntityId;

	private String ruleEntityName;

	private Set<FmsRuleDto> rules;
}
