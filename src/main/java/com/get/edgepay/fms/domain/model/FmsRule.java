package com.get.edgepay.fms.domain.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * The persistent class for the edgepay_fms_rule database table.
 * 
 */
@Entity
@Table(name = "edgepay_fms_rule")
@Data
@EqualsAndHashCode(exclude = "ruleMerchant")
@ToString(exclude = "ruleMerchant")
public class FmsRule implements Serializable {

	private final static long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "edgepay_fms_rule_id")
	private Long ruleId;

	@Column(name = "edgepay_fms_rule_access_mode")
	private String accessMode;

	@Column(name = "edgepay_fms_rule_action")
	private String action;

	@Column(name = "edgepay_fms_rule_value")
	private String ruleValue;

	@Column(name = "edgepay_fms_rule_usage_limit")
	private int ruleUsageLimit;

	@Column(name = "edgepay_fms_rule_max_transaction_amount")
	private BigDecimal maxAllowedTxnAmount;

	@Column(name = "edgepay_fms_rule_time_period")
	private String timePeriod;

	@Column(name = "edgepay_fms_rule_avs_check_required")
	private boolean isAvsCheckRequired;

	@Column(name = "edgepay_fms_rule_created_by")
	private String createdBy;

	@Column(name = "edgepay_fms_rule_creation_time")
	private Timestamp creationTime;

	@Column(name = "edgepay_fms_rule_updated_by")
	private String updatedBy;

	@Column(name = "edgepay_fms_rule_updation_time")
	private Timestamp updationTime;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "edgepay_fms_rule_entity_id", referencedColumnName = "edgepay_fms_rule_entity_id")
	private FmsRuleEntity fmsRuleEntity;

	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name = "edgepay_fms_rule_merchant", joinColumns = @JoinColumn(name = "edgepay_fms_rule_id", referencedColumnName = "edgepay_fms_rule_id"), inverseJoinColumns = @JoinColumn(name = "edgepay_fms_merchant_id", referencedColumnName = "edgepay_fms_merchant_sl_id"))
	private Set<FmsRuleMerchant> ruleMerchant;
}
