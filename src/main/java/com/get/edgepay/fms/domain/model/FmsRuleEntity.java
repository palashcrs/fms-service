package com.get.edgepay.fms.domain.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * The persistent class for the edgepay_fms_rule_entity database table.
 * 
 */
@Entity
@Table(name = "edgepay_fms_rule_entity")
@Data
@EqualsAndHashCode(exclude = "rules")
@ToString(exclude = "rules")
public class FmsRuleEntity implements Serializable {

	private final static long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "edgepay_fms_rule_entity_id")
	private Long ruleEntityId;

	@Column(name = "edgepay_fms_rule_entity_name")
	private String ruleEntityName;

	@OneToMany(mappedBy = "fmsRuleEntity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private Set<FmsRule> rules;

}
