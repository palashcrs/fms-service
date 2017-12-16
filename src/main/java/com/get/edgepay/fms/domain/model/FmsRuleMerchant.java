package com.get.edgepay.fms.domain.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import lombok.Data;

/**
 * The persistent class for the edgepay_fms_merchant_rule_mapping database
 * table.
 * 
 */
@Entity
@Table(name = "edgepay_fms_merchant")
@Data
public class FmsRuleMerchant {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "edgepay_fms_merchant_sl_id")
	private Long id;

	@Column(name = "edgepay_merchant_mid")
	private String merchantId;

	@ManyToMany(mappedBy = "ruleMerchant")
	private Set<FmsRule> rules;

}
