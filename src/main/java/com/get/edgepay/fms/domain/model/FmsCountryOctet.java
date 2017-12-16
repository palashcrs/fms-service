package com.get.edgepay.fms.domain.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "edgepay_fms_country_octet")
@Getter
@Setter
public class FmsCountryOctet {
	@Id
	@Column(name = "edgepay_fms_country_octet_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "edgepay_fms_country_octet_iso")
	private String countryIso;
	
	@Column(name = "edgepay_fms_country_octet_1")
	private String octetOne;
	
	@Column(name = "edgepay_fms_country_name")
	private String countryName;
}
