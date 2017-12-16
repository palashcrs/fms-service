package com.get.edgepay.fms.domain.dto;

import java.io.Serializable;

import com.get.edgepay.fms.common.FmsError;

import lombok.Data;

@Data
public class FMSResponse implements Serializable {

	private final static long serialVersionUID = 1L;

	private Object fmsResult;

	private String fmsResponseCode;

	private FmsError fmsError;

}
