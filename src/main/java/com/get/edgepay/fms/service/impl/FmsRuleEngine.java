package com.get.edgepay.fms.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.get.edgepay.fms.common.FMSRuleConstant;
import com.get.edgepay.fms.constant.FmsCacheConstant;
import com.get.edgepay.fms.domain.dto.FmsRuleDto;
import com.get.edgepay.fms.domain.dto.FmsTransactionCacheDto;
import com.get.edgepay.fms.domain.dto.FmsTransactionDto;
import com.get.edgepay.fms.util.CacheUtil;
import com.get.edgepay.fms.util.FmsCommonUtil;
import com.get.edgepay.fms.util.FmsUtil;

@Service
public class FmsRuleEngine {

	private static final Logger log = LoggerFactory.getLogger(FmsRuleEngine.class);

	private static final String ALL_TXN = FmsCacheConstant.ALL_TXN.name();

	@Autowired
	private CacheUtil cacheUtil;

	/**
	 * This method validates all Public and Private Rules.
	 * 
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public Map<Long, String> triggerRule(List<FmsRuleDto> merchantRules, FmsTransactionDto fmsTxnReq) throws InterruptedException, ExecutionException {
		Map<Long, String> triggeredRulesResponse = new HashMap<>();
		
		if (!FmsUtil.getInstance().isAnyObjectNull(cacheUtil)) {
			log.debug("Autowired object(s) null! Exit from method");
			return null;
		}
		
		List<FmsTransactionDto> allTxns = (List<FmsTransactionDto>) cacheUtil.getFromCache(ALL_TXN, ALL_TXN, FmsTransactionCacheDto.class);
		log.debug("allTxns = " + allTxns);
		
		ExecutorService executorService = Executors.newFixedThreadPool(9);
		Set<Callable<Map<Long, String>>> callables = new HashSet<Callable<Map<Long, String>>>();

		if (merchantRules != null && fmsTxnReq != null) {

			// ******************Trigger RuleEntity = EMAIL**********//
			if (fmsTxnReq.getEmail() != null || "".equals(fmsTxnReq.getEmail())) {
				callables.add(new Callable<Map<Long, String>>() {
					public Map<Long, String> call() throws Exception {
						List<FmsRuleDto> filteredEmailRules = getRulesByRuleEntity(merchantRules, FMSRuleConstant.RULE_ENTITY_EMAIL.getValue());
						Map<Long, String> m = triggerIsEqualCheckRules(filteredEmailRules, fmsTxnReq.getEmail());
						return m;
					}
				});
			}

			// ******************Trigger RuleEntity = CARDNO**********//
			if (fmsTxnReq.getCardNumber() != null || "".equals(fmsTxnReq.getCardNumber())) {
				callables.add(new Callable<Map<Long, String>>() {
					public Map<Long, String> call() throws Exception {
						List<FmsRuleDto> filteredCardNoRules = getRulesByRuleEntity(merchantRules, FMSRuleConstant.RULE_ENTITY_CARDNO.getValue());
						Map<Long, String> m = triggerIsEqualCheckRules(filteredCardNoRules, fmsTxnReq.getCardNumber());
						return m;
					}
				});
			}

			// ******************Trigger RuleEntity = StreetAddress**********//
			if (fmsTxnReq.getStreetAddress() != null || "".equals(fmsTxnReq.getStreetAddress())) {
				callables.add(new Callable<Map<Long, String>>() {
					public Map<Long, String> call() throws Exception {
						List<FmsRuleDto> filteredStrAddrRules = getRulesByRuleEntity(merchantRules, FMSRuleConstant.RULE_ENTITY_STREET_ADDRESS.getValue());
						Map<Long, String> m = triggerIsEqualCheckRules(filteredStrAddrRules, fmsTxnReq.getStreetAddress());
						return m;
					}
				});
			}

			// ******************Trigger RuleEntity = IPAddress**********//
			if (fmsTxnReq.getIp() != null || "".equals(fmsTxnReq.getIp())) {
				callables.add(new Callable<Map<Long, String>>() {
					public Map<Long, String> call() throws Exception {
						List<FmsRuleDto> filteredIpRules = getRulesByRuleEntity(merchantRules, FMSRuleConstant.RULE_ENTITY_IP.getValue());
						Map<Long, String> m = triggerIsEqualCheckRules(filteredIpRules, fmsTxnReq.getIp());
						return m;
					}
				});
			}

			// ******************Trigger RuleEntity = WORD**********//
			if (fmsTxnReq.getCustomerName() != null || "".equals(fmsTxnReq.getCustomerName())) {
				callables.add(new Callable<Map<Long, String>>() {
					public Map<Long, String> call() throws Exception {
						List<FmsRuleDto> filteredWordNoRules = getRulesByRuleEntity(merchantRules, FMSRuleConstant.RULE_ENTITY_WORD.getValue());
						Map<Long, String> m = triggerIsContainCheckRules(filteredWordNoRules, fmsTxnReq.getCustomerName());
						return m;
					}
				});
			}

			// ******************Trigger RuleEntity = Order-Limit::MaxAmount**********//
			if (fmsTxnReq.getAmount() != null || "".equals(fmsTxnReq.getAmount())) {
				callables.add(new Callable<Map<Long, String>>() {
					public Map<Long, String> call() throws Exception {
						List<FmsRuleDto> filteredMaxAmtRules = getRulesByRuleEntity(merchantRules, FMSRuleConstant.RULE_ENTITY_MAX_AMOUNT.getValue());
						Map<Long, String> m = triggerMaxAmountCheckRules(filteredMaxAmtRules, fmsTxnReq.getAmount());
						return m;
					}
				});
			}

			// ******************Trigger RuleEntity = Order-Limit::CardUse**********//
			if (fmsTxnReq.getCardNumber() != null || "".equals(fmsTxnReq.getCardNumber())) {
				if (allTxns != null) {
					List<FmsTransactionDto> filteredTxns = allTxns.stream()
								                                  .filter(p -> p.getCardNumber() != null)
								                                  .filter(p -> p.getCardNumber().equalsIgnoreCase(fmsTxnReq.getCardNumber()))
								                                  .collect(Collectors.toList());
					callables.add(new Callable<Map<Long, String>>() {
						public Map<Long, String> call() throws Exception {
							List<FmsRuleDto> filteredOrderLimitRules = getRulesByRuleEntity(merchantRules, FMSRuleConstant.RULE_ENTITY_ORDER_LIMIT_CARD.getValue());
							Map<Long, String> m = triggerOrderLimitCheckRules(filteredOrderLimitRules, filteredTxns, fmsTxnReq.getCardNumber());
							return m;
						}
					});
				}
			}
			
			// ******************Trigger RuleEntity = Order-Limit::IPUseLimit**********//
			if (fmsTxnReq.getIp() != null || "".equals(fmsTxnReq.getIp())) {
				if (allTxns != null) {
					List<FmsTransactionDto> filteredTxns = allTxns.stream()
                            									  .filter(p -> p.getIp() != null)
                            									  .filter(p -> p.getIp().equalsIgnoreCase(fmsTxnReq.getIp()))
                            									  .collect(Collectors.toList());
					callables.add(new Callable<Map<Long, String>>() {
						public Map<Long, String> call() throws Exception {
							List<FmsRuleDto> filteredOrderLimitRules = getRulesByRuleEntity(merchantRules, FMSRuleConstant.RULE_ENTITY_ORDER_LIMIT_IP.getValue());
							Map<Long, String> m = triggerOrderLimitCheckRules(filteredOrderLimitRules, filteredTxns, fmsTxnReq.getIp());
							return m;
						}
					});
				}
			}

			List<Future<Map<Long, String>>> futures = executorService.invokeAll(callables);
			if (futures != null) {
				for (Future<Map<Long, String>> future : futures) {
					if (future != null) {
						triggeredRulesResponse.putAll(future.get());
					}
				}
			}
			executorService.shutdown();
		}

		return triggeredRulesResponse;
	}

	private static List<FmsRuleDto> getRulesByRuleEntity(List<FmsRuleDto> merchantRules, String ruleEntityName) {
		List<FmsRuleDto> filteredRules = null;
		if (merchantRules != null) {
			filteredRules = merchantRules.stream()
					                     .filter(p -> p.getFmsRuleEntity() != null)
					                     .filter(p -> p.getFmsRuleEntity().getRuleEntityName() != null)
					                     .filter(p -> p.getFmsRuleEntity().getRuleEntityName().equalsIgnoreCase(ruleEntityName))
					                     .collect(Collectors.toList());
		}

		return filteredRules;
	}

	private static Map<Long, String> triggerIsEqualCheckRules(List<FmsRuleDto> filteredRules, String data) {
		Map<Long, String> triggeredRuleResponse = new HashMap<>();
		String triggeredRuleStatus = null;
		for (FmsRuleDto r : filteredRules) {
			if (r != null) {
				triggeredRuleStatus = FmsCommonUtil.getInstance().isEqualAndSetStatus(r.getRuleValue(), r.getAccessMode(), r.getAction(), data);
				log.info("Rule : Data : Status : " + r.getRuleValue() + " : " + data + " : " + triggeredRuleStatus);
				if (triggeredRuleStatus != null) {
					triggeredRuleResponse.put(r.getRuleId(), triggeredRuleStatus);
				}
			}
		}

		return triggeredRuleResponse;
	}

	private static Map<Long, String> triggerIsContainCheckRules(List<FmsRuleDto> filteredWordNoRules, String data) {
		Map<Long, String> triggeredRuleResponse = new HashMap<>();
		String triggeredRuleStatus = null;
		for (FmsRuleDto r : filteredWordNoRules) {
			if (r != null) {
				triggeredRuleStatus = FmsCommonUtil.getInstance().isContainsAndSetStatus(r.getRuleValue(), r.getAccessMode(), r.getAction(), data);
				log.info("Rule : Data : Status : " + r.getRuleValue() + " : " + data + " : " + triggeredRuleStatus);
				if (triggeredRuleStatus != null) {
					triggeredRuleResponse.put(r.getRuleId(), triggeredRuleStatus);
				}
			}
		}

		return triggeredRuleResponse;
	}

	private static Map<Long, String> triggerMaxAmountCheckRules(List<FmsRuleDto> filteredMaxAmtRules, BigDecimal data) {
		Map<Long, String> triggeredRuleResponse = new HashMap<>();
		String triggeredRuleStatus = null;
		for (FmsRuleDto r : filteredMaxAmtRules) {
			if (r != null) {
				triggeredRuleStatus = FmsCommonUtil.getInstance().isMaxAndSetStatus(r.getMaxAllowedTxnAmount(), r.getAccessMode(), r.getAction(), data);
				log.info("Rule : Data : Status : " + r.getMaxAllowedTxnAmount() + " : " + data + " : " + triggeredRuleStatus);
				if (triggeredRuleStatus != null) {
					triggeredRuleResponse.put(r.getRuleId(), triggeredRuleStatus);
				}
			}
		}

		return triggeredRuleResponse;
	}

	private static Map<Long, String> triggerOrderLimitCheckRules(List<FmsRuleDto> filteredOrderLimitRules, List<FmsTransactionDto> filteredTxns, String data) {
		Map<Long, String> triggeredRuleResponse = new HashMap<>();
		String triggeredRuleStatus = null;
		for (FmsRuleDto r : filteredOrderLimitRules) {
			if (r != null) {
				triggeredRuleStatus = FmsCommonUtil.getInstance().isOrderLimitAndSetStatus(r.getRuleValue(), r.getRuleUsageLimit(), r.getTimePeriod(), r.getAccessMode(), r.getAction(), filteredTxns, data);
				log.info("Rule : Limit : TimePeriod : Data : Status : " + r.getRuleValue() + " : " + r.getRuleUsageLimit() + " : " + r.getTimePeriod() + " : " + data + " : " + triggeredRuleStatus);
				if (triggeredRuleStatus != null) {
					triggeredRuleResponse.put(r.getRuleId(), triggeredRuleStatus);
				}
			}
		}

		return triggeredRuleResponse;
	}

}
