package com.get.edgepay.fms.domain.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Set;

import lombok.Data;

@Data
public class FmsRuleDto implements Serializable {

	private final static long serialVersionUID = 1L;

	private Long ruleId;

	private String accessMode;

	private String action;

	private String ruleValue;

	private int ruleUsageLimit;

	private BigDecimal maxAllowedTxnAmount;

	private String timePeriod;

	private boolean isAvsCheckRequired;

	private String createdBy;

	private Timestamp creationTime;

	private String updatedBy;

	private Timestamp updationTime;

	private FmsRuleEntityDto fmsRuleEntity;

	private Set<FmsRuleMerchantDto> ruleMerchant;

}
