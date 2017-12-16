package com.get.edgepay.fms.domain.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.Data;

@Data
public class FmsTransactionDto implements Serializable {

	private final static long serialVersionUID = 1L;

	private String fmsTransactionId;

	private String edgePayTransactionId;

	private String transactionType;

	private String fmsTransactionStatus;

	private BigDecimal amount;

	private String email;

	private String cardNumber;

	private String ip;

	private String customerName;

	private String zip;

	private String streetAddress;

	private String city;

	private String state;

	private String geoIp;

	private String deviceId;

	private String notes;

	private String violatedRules;

	private String executedBy;

	private Timestamp executionTs;
}
