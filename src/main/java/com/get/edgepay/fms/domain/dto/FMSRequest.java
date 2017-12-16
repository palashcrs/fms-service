package com.get.edgepay.fms.domain.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class FMSRequest implements Serializable {

	private final static long serialVersionUID = 1L;

	private String userId;

	private String merchantId;

	private List<FmsRuleEntityDto> fmsRuleEntities;

	private List<FmsRuleDto> fmsRules;

	private List<FmsTransactionDto> fmsTransactions;
	
	private String searchStartDate;
	
	private String searchEndDate;

}
