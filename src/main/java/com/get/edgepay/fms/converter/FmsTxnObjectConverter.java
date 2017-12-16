package com.get.edgepay.fms.converter;

import java.util.ArrayList;
import java.util.List;

import com.get.edgepay.fms.domain.dto.FmsTransactionDto;
import com.get.edgepay.fms.domain.model.FmsTransaction;

public class FmsTxnObjectConverter {

	private static FmsTxnObjectConverter fmsTxnObjectConverter = new FmsTxnObjectConverter();

	private FmsTxnObjectConverter() {

	}

	public static FmsTxnObjectConverter getInstance() {
		return fmsTxnObjectConverter;
	}

	public List<FmsTransactionDto> convertFMSTxnEntityToDTO(List<FmsTransaction> fmsTxnList) {
		List<FmsTransactionDto> fmsTxnDtoList = null;
		if (fmsTxnList != null && fmsTxnList.size() > 0) {
			fmsTxnDtoList = new ArrayList<>();
			for (FmsTransaction t : fmsTxnList) {
				FmsTransactionDto fmsTransactionDto = new FmsTransactionDto();
				fmsTransactionDto.setFmsTransactionId(t.getFmsTransactionId());
				fmsTransactionDto.setEdgePayTransactionId(t.getEdgePayTransactionId());
				fmsTransactionDto.setTransactionType(t.getTransactionType());
				fmsTransactionDto.setAmount(t.getAmount());
				fmsTransactionDto.setFmsTransactionStatus(t.getFmsTransactionStatus());
				fmsTransactionDto.setFmsTransactionStatus(t.getFmsTransactionStatus());
				fmsTransactionDto.setEmail(t.getEmail());
				fmsTransactionDto.setCardNumber(t.getCardNumber());
				fmsTransactionDto.setIp(t.getIp());
				fmsTransactionDto.setCustomerName(t.getCustomerName());
				fmsTransactionDto.setZip(t.getZip());
				fmsTransactionDto.setStreetAddress(t.getStreetAddress());
				fmsTransactionDto.setCity(t.getCity());
				fmsTransactionDto.setState(t.getState());
				fmsTransactionDto.setGeoIp(t.getGeoIp());
				fmsTransactionDto.setDeviceId(t.getDeviceId());
				fmsTransactionDto.setNotes(t.getNotes());
				fmsTransactionDto.setViolatedRules(t.getViolatedRules());
				fmsTransactionDto.setExecutedBy(t.getExecutedBy());
				fmsTransactionDto.setExecutionTs(t.getExecutionTs());
				fmsTxnDtoList.add(fmsTransactionDto);
			}
		}

		return fmsTxnDtoList;
	}

	public List<FmsTransaction> convertFMSTxnDtoToEntity(List<FmsTransactionDto> fmsTxnDtoList) {
		List<FmsTransaction> fmsTxnList = null;
		if (fmsTxnDtoList != null && fmsTxnDtoList.size() > 0) {
			fmsTxnList = new ArrayList<>();
			for (FmsTransactionDto t : fmsTxnDtoList) {
				FmsTransaction fmsTransaction = new FmsTransaction();
				fmsTransaction.setFmsTransactionId(t.getFmsTransactionId());
				fmsTransaction.setEdgePayTransactionId(t.getEdgePayTransactionId());
				fmsTransaction.setTransactionType(t.getTransactionType());
				fmsTransaction.setAmount(t.getAmount());
				fmsTransaction.setFmsTransactionStatus(t.getFmsTransactionStatus());
				fmsTransaction.setFmsTransactionStatus(t.getFmsTransactionStatus());
				fmsTransaction.setEmail(t.getEmail());
				fmsTransaction.setCardNumber(t.getCardNumber());
				fmsTransaction.setIp(t.getIp());
				fmsTransaction.setCustomerName(t.getCustomerName());
				fmsTransaction.setZip(t.getZip());
				fmsTransaction.setStreetAddress(t.getStreetAddress());
				fmsTransaction.setCity(t.getCity());
				fmsTransaction.setState(t.getState());
				fmsTransaction.setGeoIp(t.getGeoIp());
				fmsTransaction.setDeviceId(t.getDeviceId());
				fmsTransaction.setNotes(t.getNotes());
				fmsTransaction.setViolatedRules(t.getViolatedRules());
				fmsTransaction.setExecutedBy(t.getExecutedBy());
				fmsTransaction.setExecutionTs(t.getExecutionTs());
				fmsTxnList.add(fmsTransaction);
			}
		}

		return fmsTxnList;

	}

}
