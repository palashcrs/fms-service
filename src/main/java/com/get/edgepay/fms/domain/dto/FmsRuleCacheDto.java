package com.get.edgepay.fms.domain.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class FmsRuleCacheDto implements Serializable {

	private final static long serialVersionUID = 1L;

	private List<FmsRuleDto> ruleList;

}
