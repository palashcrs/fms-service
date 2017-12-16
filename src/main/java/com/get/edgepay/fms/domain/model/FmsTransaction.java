package com.get.edgepay.fms.domain.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * The persistent class for the edgepay_fms_transaction database table.
 * 
 */
@Entity
@Table(name = "edgepay_fms_transaction")
@Data
public class FmsTransaction implements Serializable {

	private final static long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "edgepay_fms_transaction_sl_id")
	private Long fmsSerialId;

	@Column(name = "edgepay_fms_transaction_id")
	private String fmsTransactionId;

	@Column(name = "edgepay_fms_generated_transaction_id")
	private String edgePayTransactionId;

	@Column(name = "edgepay_fms_transaction_type")
	private String transactionType;

	@Column(name = "edgepay_fms_transaction_status")
	private String fmsTransactionStatus;
	
	@Column(name = "edgepay_fms_transaction_total_amount")
	private BigDecimal amount;

	@Column(name = "edgepay_fms_transaction_email")
	private String email;

	@Column(name = "edgepay_fms_transaction_card_no")
	private String cardNumber;

	@Column(name = "edgepay_fms_transaction_ip")
	private String ip;

	@Column(name = "edgepay_fms_transaction_customername")
	private String customerName;

	@Column(name = "edgepay_fms_transaction_zip")
	private String zip;

	@Column(name = "edgepay_fms_transaction_street_address")
	private String streetAddress;

	@Column(name = "edgepay_fms_transaction_city")
	private String city;

	@Column(name = "edgepay_fms_transaction_state")
	private String state;

	@Column(name = "edgepay_fms_transaction_geoloc_ip")
	private String geoIp;

	@Column(name = "edgepay_fms_transaction_device_id")
	private String deviceId;

	@Column(name = "edgepay_fms_transaction_notes")
	private String notes;

	@Column(name = "edgepay_fms_transaction_violated_rules")
	private String violatedRules;

	@Column(name = "edgepay_fms_transaction_executed_by")
	private String executedBy;

	@Column(name = "edgepay_fms_transaction_execution_ts")
	private Timestamp executionTs;
}
