package com.get.edgepay.fms.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.get.edgepay.fms.common.FMSRuleConstant;
import com.get.edgepay.fms.constant.FmsCacheConstant;
import com.get.edgepay.fms.constant.FmsTxnStatusConstant;
import com.get.edgepay.fms.converter.FmsTxnObjectConverter;
import com.get.edgepay.fms.domain.dto.FmsRuleCacheDto;
import com.get.edgepay.fms.domain.dto.FmsRuleDto;
import com.get.edgepay.fms.domain.dto.FmsRuleMerchantDto;
import com.get.edgepay.fms.domain.dto.FmsTransactionCacheDto;
import com.get.edgepay.fms.domain.dto.FmsTransactionDto;
import com.get.edgepay.fms.domain.model.FmsTransaction;
import com.get.edgepay.fms.exception.CacheException;
import com.get.edgepay.fms.exception.DBException;
import com.get.edgepay.fms.exception.FmsException;
import com.get.edgepay.fms.repository.FmsTransactionRepository;
import com.get.edgepay.fms.service.FmsTransactionService;
import com.get.edgepay.fms.util.CacheUtil;
import com.get.edgepay.fms.util.FmsCommonUtil;
import com.get.edgepay.fms.util.FmsUtil;

@Service
public class FmsTransactionServiceImpl implements FmsTransactionService {

	private static final Logger log = LoggerFactory.getLogger(FmsTransactionServiceImpl.class);

	private static final String ALL_RULES = FmsCacheConstant.ALL_RULES.name();

	private static final String ALL_TXN = FmsCacheConstant.ALL_TXN.name();

	@Autowired
	private FmsTransactionRepository fmsTransactionRepository;
	
	@Autowired
	private FmsRuleEngine FmsRuleEngine;

	@Autowired
	private CacheUtil cacheUtil;

	@Transactional(rollbackFor = { FmsException.class, DBException.class, InterruptedException.class, ExecutionException.class, CacheException.class })
	@Override
	public FmsTransactionDto calculateFraudAndSaveTxn(FmsTransactionDto fmsTxnReq, String merchantId) throws Exception {
		String fmsTransactionStatus = null;
		Map<Long, String> rulesTriggeredResponse = null;
		Set<Long> fmsRulesViolated = null;
		List<String> fmsMergedStatusList = null;

		if (fmsTxnReq != null && merchantId != null) {
			List<FmsRuleDto> allMerchantRules = (List<FmsRuleDto>) cacheUtil.getFromCache(ALL_RULES, ALL_RULES, FmsRuleCacheDto.class);
			List<FmsRuleDto> merchantRules = getMerchantRules(allMerchantRules, merchantId);
			rulesTriggeredResponse = FmsRuleEngine.triggerRule(merchantRules, fmsTxnReq);

			if (rulesTriggeredResponse != null && rulesTriggeredResponse.size() > 0) {
				fmsRulesViolated = rulesTriggeredResponse.keySet();
				log.info("FMSRulesViolated set : " + fmsRulesViolated.toString());

				fmsMergedStatusList = new ArrayList<>();
				for (Long key : fmsRulesViolated) {
					fmsMergedStatusList.add(rulesTriggeredResponse.get(key));
				}
				log.info("FMSMergedStatusList : " + fmsMergedStatusList);
			}

			fmsTransactionStatus = calculateFMSTxnStatus(fmsMergedStatusList);

			fmsTxnReq.setFmsTransactionId(FmsCommonUtil.getInstance().generateFMSTxnId("FMSTXN"));
			fmsTxnReq.setFmsTransactionStatus(fmsTransactionStatus);
			if (fmsRulesViolated != null) {
				fmsTxnReq.setViolatedRules(fmsRulesViolated.toString());
			}
			fmsTxnReq.setExecutedBy(merchantId);
			fmsTxnReq.setExecutionTs(FmsUtil.getInstance().currentTimestamp());

			List<FmsTransactionDto> fmsTransactionDtoList = new ArrayList<>();
			fmsTransactionDtoList.add(fmsTxnReq);
			saveOrUpdateFMSTxn(fmsTransactionDtoList);
		}

		return fmsTxnReq;
	}

	private String calculateFMSTxnStatus(List<String> mergedList) {
		String fmsTxnStatus = null;

		if (mergedList != null) {
			if (mergedList.contains(FMSRuleConstant.RULE_ACTION_DECLINE_STATUS.getValue())) {
				fmsTxnStatus = FmsTxnStatusConstant.DECLINE.toString();
			} else {
				int mergedListSize = mergedList.size();
				int occurrencesOfReview = Collections.frequency(mergedList, FMSRuleConstant.RULE_ACTION_REVIEW_STATUS.getValue());
				int occurrencesOfOthers = mergedListSize - occurrencesOfReview;
				if (occurrencesOfReview > occurrencesOfOthers) {
					fmsTxnStatus = FmsTxnStatusConstant.REVIEW.toString();
				} else {
					fmsTxnStatus = FmsTxnStatusConstant.APPROVED.toString();
				}
			}
		} else {
			fmsTxnStatus = FmsTxnStatusConstant.APPROVED.toString();
		}

		return fmsTxnStatus;
	}

	private List<FmsRuleDto> getMerchantRules(List<FmsRuleDto> allRules, String merchantId) {
		List<FmsRuleDto> toRemove = new ArrayList<>();
		if (allRules != null && merchantId != null) {
			for (FmsRuleDto f : allRules) {
				if (f != null) {
					Set<FmsRuleMerchantDto> ruleMerchants = f.getRuleMerchant();
					if (ruleMerchants != null) {
						boolean isExists = ruleMerchants.stream().anyMatch(p -> p.getMerchantId().equalsIgnoreCase(merchantId));
						if (!isExists) {
							toRemove.add(f);
						}
					}
				}
			}
		}
		allRules.removeAll(toRemove);

		return allRules;
	}

	public int saveOrUpdateFMSTxn(List<FmsTransactionDto> fmsTransactionDtoList) throws Exception {
		List<FmsTransaction> fmsTransactionList = FmsTxnObjectConverter.getInstance().convertFMSTxnDtoToEntity(fmsTransactionDtoList);
		fmsTransactionList = fmsTransactionRepository.save(fmsTransactionList);
		fmsTransactionRepository.flush();
		int rowsCreated = (fmsTransactionList != null) ? fmsTransactionList.size() : 0;
		if (rowsCreated > 0) {
			List<FmsTransaction> fmsTxnListFromDB = fmsTransactionRepository.findAll();
			if (fmsTxnListFromDB != null) {
				cacheUtil.removeFromCache(ALL_TXN, FmsTransactionCacheDto.class);
				List<FmsTransactionDto> fmsTxnDtoList = FmsTxnObjectConverter.getInstance().convertFMSTxnEntityToDTO(fmsTxnListFromDB);
				if (fmsTxnDtoList != null) {
					cacheUtil.addToCache(ALL_TXN, ALL_TXN, fmsTxnDtoList);
				}
			}
		}
		return rowsCreated;
	}

	@Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = { CacheException.class })
	@Override
	public List<FmsTransactionDto> getAllTransactions() throws Exception {

		List<FmsTransactionDto> allTxns = null;

		if (!FmsUtil.getInstance().isAnyObjectNull(cacheUtil)) {
			log.debug("Autowired object(s) null! Exit from method");
			return null;
		}

		try {
			allTxns = (List<FmsTransactionDto>) cacheUtil.getFromCache(ALL_TXN, ALL_TXN, FmsTransactionCacheDto.class);

		} catch (RedisConnectionFailureException e) {
			throw new CacheException();
		}

		return allTxns;
	}
	
	@Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = { CacheException.class })
	@Override
	public List<FmsTransactionDto> searchTransactions(List<FmsTransactionDto> fmsTransactions, String searchStartDate, String searchEndDate) throws Exception {
		List<FmsTransactionDto> allTxns = null;

		if (!FmsUtil.getInstance().isAnyObjectNull(cacheUtil)) {
			log.debug("Autowired object(s) null! Exit from method");
			return null;
		}

		try {
			allTxns = (List<FmsTransactionDto>) cacheUtil.getFromCache(ALL_TXN, ALL_TXN, FmsTransactionCacheDto.class);
			if (allTxns != null) {
				FmsTransactionDto fmsTransactionDto = fmsTransactions.get(0);
				String fmsTransactionStatus = fmsTransactionDto.getFmsTransactionStatus();
				String cardNumber = fmsTransactionDto.getCardNumber();
				String customerName = fmsTransactionDto.getCustomerName();
				log.info("Required input = " + fmsTransactionStatus + " | " + cardNumber + " | " + customerName + " | " + searchStartDate + " | " + searchEndDate);
				
				Predicate<FmsTransactionDto> fmsTransactionStatusPredicate = p -> p.getFmsTransactionStatus() != null && p.getFmsTransactionStatus().equalsIgnoreCase(fmsTransactionStatus);
				Predicate<FmsTransactionDto> cardNumberPredicate = p -> p.getCardNumber() != null && p.getCardNumber().equalsIgnoreCase(cardNumber);
				Predicate<FmsTransactionDto> customerNamePredicate = p -> p.getCustomerName() != null && p.getCustomerName().equalsIgnoreCase(customerName);
				
				if (fmsTransactionStatus != null) {
					allTxns = allTxns.stream()
						      		 .filter(p -> p != null)
						      		 .filter(fmsTransactionStatusPredicate)
						      		 .collect(Collectors.toList());
				}
				if (cardNumber != null) {
					allTxns = allTxns.stream()
						      		 .filter(p -> p != null)
						      		 .filter(cardNumberPredicate)
						      		 .collect(Collectors.toList());
				}
				if (customerName != null) {
					allTxns = allTxns.stream()
						      		 .filter(p -> p != null)
						      		 .filter(customerNamePredicate)
						      		 .collect(Collectors.toList());
				}
				if (searchStartDate != null && searchEndDate != null) {
					Timestamp startTs = FmsUtil.getInstance().getTimestampFromString(searchStartDate);
					Timestamp endTs = FmsUtil.getInstance().getTimestampFromString(searchEndDate);
					allTxns = allTxns.stream()
				      		 		 .filter(p -> p != null)
				      		         .filter(p -> p.getExecutionTs().after(startTs) && p.getExecutionTs().before(endTs))
				      		         .collect(Collectors.toList());
				}
				if (searchStartDate != null && searchEndDate == null) {
					allTxns = allTxns.stream()
		      		 		 		 .filter(p -> p != null)
		      		 		 		 .filter(p -> {
		      		 		 			 			boolean b = false;
		      		 		 			 			try {
		      		 		 			 				String executionTsText = FmsUtil.getInstance().getStringFromTimeStamp(p.getExecutionTs());
														Timestamp executionTsTextTs = FmsUtil.getInstance().getTimestampFromString(executionTsText);
														b = executionTsTextTs.equals(FmsUtil.getInstance().getTimestampFromString(searchStartDate));
													} catch (Exception e) {
													}
		      		 		 			 			return b;
		      		 		 		 })
		      		 		 		 .collect(Collectors.toList());
				}
				if (searchStartDate == null && searchEndDate != null) {
					allTxns = allTxns.stream()
     		 		 		 		 .filter(p -> p != null)
     		 		 		 		 .filter(p -> {
     		 		 			 					boolean b = false;
     		 		 			 					try {
     		 		 			 						String executionTsText = FmsUtil.getInstance().getStringFromTimeStamp(p.getExecutionTs());
     		 		 			 						Timestamp executionTsTextTs = FmsUtil.getInstance().getTimestampFromString(executionTsText);
     		 		 			 						b = executionTsTextTs.equals(FmsUtil.getInstance().getTimestampFromString(searchEndDate));
     		 		 			 					} catch (Exception e) {
     		 		 			 					}
     		 		 			 					return b;
     		 		 		 		 })
     		 		 		 		 .collect(Collectors.toList());
				}
			}

		} catch (RedisConnectionFailureException e) {
			throw new CacheException();
		}

		return allTxns;
	}

}
