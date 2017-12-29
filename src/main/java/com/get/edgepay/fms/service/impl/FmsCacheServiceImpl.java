package com.get.edgepay.fms.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.get.edgepay.fms.common.FMSRuleConstant;
import com.get.edgepay.fms.constant.FmsCacheConstant;
import com.get.edgepay.fms.converter.FmsRuleEntityObjectConverter;
import com.get.edgepay.fms.converter.FmsRuleObjectConverter;
import com.get.edgepay.fms.converter.FmsTxnObjectConverter;
import com.get.edgepay.fms.domain.dto.FmsRuleCacheDto;
import com.get.edgepay.fms.domain.dto.FmsRuleDto;
import com.get.edgepay.fms.domain.dto.FmsRuleEntityCacheDto;
import com.get.edgepay.fms.domain.dto.FmsRuleEntityDto;
import com.get.edgepay.fms.domain.dto.FmsTransactionCacheDto;
import com.get.edgepay.fms.domain.dto.FmsTransactionDto;
import com.get.edgepay.fms.domain.model.FmsRule;
import com.get.edgepay.fms.domain.model.FmsRuleEntity;
import com.get.edgepay.fms.domain.model.FmsTransaction;
import com.get.edgepay.fms.repository.FmsRuleEntityRepository;
import com.get.edgepay.fms.repository.FmsRuleRepository;
import com.get.edgepay.fms.repository.FmsTransactionRepository;
import com.get.edgepay.fms.service.FmsCacheService;
import com.get.edgepay.fms.util.CacheUtil;

@Service
public class FmsCacheServiceImpl implements FmsCacheService {

	private static final Logger log = LoggerFactory.getLogger(FmsCacheServiceImpl.class);

	private static final String MERCHANT_TOKEN = FmsCacheConstant.MERCHANT_TOKEN.name();
	
	private static final String ALL_RULEENTITY = FmsCacheConstant.ALL_RULEENTITY.name();

	private static final String ALL_RULES = FmsCacheConstant.ALL_RULES.name();

	private static final String GLOBAL_RULES = FmsCacheConstant.GLOBAL_RULES.name();

	private static final String ALL_TXN = FmsCacheConstant.ALL_TXN.name();

	@Autowired
	private FmsRuleEntityRepository fmsRuleEntityRepository;

	@Autowired
	private FmsRuleRepository fmsRuleRepository;

	@Autowired
	private FmsTransactionRepository fmsTransactionRepository;

	@Autowired
	private CacheUtil cacheUtil;

	/**
	 * Method to load all data from Cache on startup.
	 * 
	 * @throws Exception
	 */
	@PostConstruct
	public void loadAll() throws Exception {
		log.info("******Reload all data on startup!");
		loadAllTokens();
		loadAllRuleEntities();
		loadRules();
		loadAllTransactions();
	}
	
	@Override
	public void loadAllTokens() throws Exception {
		cacheUtil.addToCache(MERCHANT_TOKEN, MERCHANT_TOKEN, "778965");
	}

	@Override
	public void loadAllRuleEntities() throws Exception {
		List<FmsRuleEntity> allRuleEntityList = fmsRuleEntityRepository.findAll();
		if (allRuleEntityList != null) {
			cacheUtil.removeFromCache(ALL_RULEENTITY, FmsRuleEntityCacheDto.class);
			List<FmsRuleEntityDto> allRuleTypeDtoList = FmsRuleEntityObjectConverter.getInstance().convertFMSRuleTypeEntityToDTO(allRuleEntityList);
			if (allRuleTypeDtoList != null) {
				cacheUtil.addToCache(ALL_RULEENTITY, ALL_RULEENTITY, allRuleTypeDtoList);
			}
		}
	}

	@Override
	public void loadRules() throws Exception {
		List<FmsRule> fmsRuleListFromDB = fmsRuleRepository.findAll();

		if (fmsRuleListFromDB != null) {
			// ****Load cache for all Rules:
			cacheUtil.removeFromCache(ALL_RULES, FmsRuleCacheDto.class);
			List<FmsRuleDto> fmsRuleDtoList = FmsRuleObjectConverter.getInstance().convertFMSRuleEntityToDTO(fmsRuleListFromDB);
			if (fmsRuleDtoList != null) {
				cacheUtil.addToCache(ALL_RULES, ALL_RULES, fmsRuleDtoList);
			}

			// ****Load cache for global Rules:
			if (fmsRuleDtoList != null) {
				List<FmsRuleDto> fmsRuleDtoListPub = fmsRuleDtoList.stream().filter(p -> p.getAccessMode() != null)
															       .filter(p -> p.getAccessMode().equalsIgnoreCase(FMSRuleConstant.RULE_ACCESSMODE_PUBLIC.getValue()))
															       .collect(Collectors.toList());
				if (fmsRuleDtoListPub != null) {
					cacheUtil.removeFromCache(GLOBAL_RULES, FmsRuleCacheDto.class);
					cacheUtil.addToCache(GLOBAL_RULES, GLOBAL_RULES, fmsRuleDtoListPub);
				}
			}
		}
	}

	@Override
	public List<FmsTransactionDto> loadAllTransactions() throws Exception {
		List<FmsTransactionDto> fmsTxnDtoList = null;
		List<FmsTransaction> fmsTxnListFromDB = fmsTransactionRepository.findAll();
		if (fmsTxnListFromDB != null) {
			cacheUtil.removeFromCache(ALL_TXN, FmsTransactionCacheDto.class);
			fmsTxnDtoList = FmsTxnObjectConverter.getInstance().convertFMSTxnEntityToDTO(fmsTxnListFromDB);
			if (fmsTxnDtoList != null) {
				cacheUtil.addToCache(ALL_TXN, ALL_TXN, fmsTxnDtoList);
			}
		}
		
		return fmsTxnDtoList;
	}

}
