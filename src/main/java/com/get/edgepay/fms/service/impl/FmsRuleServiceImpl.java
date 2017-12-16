package com.get.edgepay.fms.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.get.edgepay.fms.common.FMSRuleConstant;
import com.get.edgepay.fms.constant.FmsCacheConstant;
import com.get.edgepay.fms.converter.FmsRuleObjectConverter;
import com.get.edgepay.fms.domain.dto.FmsRuleCacheDto;
import com.get.edgepay.fms.domain.dto.FmsRuleDto;
import com.get.edgepay.fms.domain.dto.FmsRuleMerchantDto;
import com.get.edgepay.fms.domain.model.FmsRule;
import com.get.edgepay.fms.exception.CacheException;
import com.get.edgepay.fms.exception.DBException;
import com.get.edgepay.fms.exception.DuplicateRecordException;
import com.get.edgepay.fms.repository.FmsRuleRepository;
import com.get.edgepay.fms.service.FmsRuleService;
import com.get.edgepay.fms.util.CacheUtil;
import com.get.edgepay.fms.util.FmsUtil;

@Service
public class FmsRuleServiceImpl implements FmsRuleService {

	private static final Logger log = LoggerFactory.getLogger(FmsRuleServiceImpl.class);

	private static final String ALL_RULES = FmsCacheConstant.ALL_RULES.name();

	private static final String GLOBAL_RULES = FmsCacheConstant.GLOBAL_RULES.name();

	@Autowired
	private FmsRuleRepository fmsRuleRepository;

	@Autowired
	private CacheUtil cacheUtil;

	@Transactional(rollbackFor = { DBException.class, DuplicateRecordException.class, CacheException.class })
	@Override
	public int createRule(List<FmsRuleDto> fmsRuleDtoList, String userId, String merchantId) throws Exception {

		int rowsCreated = 0;

		if (!FmsUtil.getInstance().isAnyObjectNull(fmsRuleRepository)) {
			log.debug("Autowired object(s) null! Exit from method");
			return 0;
		}

		try {
			if (fmsRuleDtoList != null) {
				for (FmsRuleDto t : fmsRuleDtoList) {
					t.setCreatedBy(userId);
					t.setCreationTime(FmsUtil.getInstance().currentTimestamp());
					FmsRuleMerchantDto fmsRuleMerchant = new FmsRuleMerchantDto();
					fmsRuleMerchant.setMerchantId(merchantId);
					Set<FmsRuleMerchantDto> ruleMerchantSet = new HashSet<>();
					ruleMerchantSet.add(fmsRuleMerchant);
					t.setRuleMerchant(ruleMerchantSet);
				}
			}
			List<FmsRule> fmsRuleList = FmsRuleObjectConverter.getInstance().convertFMSRuleDtoToEntity(fmsRuleDtoList);
			if (fmsRuleList != null) {
				fmsRuleList = fmsRuleRepository.save(fmsRuleList);
				fmsRuleRepository.flush();
				rowsCreated = (fmsRuleList != null) ? fmsRuleList.size() : 0;
			}
			if (rowsCreated > 0) {
				List<FmsRule> fmsRuleListFromDB = fmsRuleRepository.findAll();
				if (fmsRuleListFromDB != null) {
					// ****Reload cache for all Rules:
					cacheUtil.removeFromCache(ALL_RULES, FmsRuleCacheDto.class);
					List<FmsRuleDto> allFmsRuleDtoList = FmsRuleObjectConverter.getInstance().convertFMSRuleEntityToDTO(fmsRuleListFromDB);
					if (allFmsRuleDtoList != null) {
						cacheUtil.addToCache(ALL_RULES, ALL_RULES, allFmsRuleDtoList);
					}
					// ****Reload cache for global Rules:
					if (allFmsRuleDtoList != null) {
						List<FmsRuleDto> fmsRuleDtoListPub = allFmsRuleDtoList.stream()
																			  .filter(p -> p.getAccessMode() != null)
																			  .filter(p -> p.getAccessMode().equalsIgnoreCase(FMSRuleConstant.RULE_ACCESSMODE_PUBLIC.getValue()))
																			  .collect(Collectors.toList());
						if (fmsRuleDtoListPub != null) {
							cacheUtil.removeFromCache(GLOBAL_RULES, FmsRuleCacheDto.class);
							cacheUtil.addToCache(GLOBAL_RULES, GLOBAL_RULES, fmsRuleDtoListPub);
						}
					}
				}
			}

		} catch (RedisConnectionFailureException e) {
			throw new CacheException();
		} catch (DataIntegrityViolationException ex) {
			throw new DuplicateRecordException();
		}

		return rowsCreated;
	}

	@Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = { DBException.class, CacheException.class })
	@Override
	public List<FmsRuleDto> getAllRuleS() throws Exception {

		List<FmsRuleDto> fmsRuleDtoListFromCache = null;

		if (!FmsUtil.getInstance().isAnyObjectNull(cacheUtil)) {
			log.debug("Autowired object(s) null! Exit from method");
			return null;
		}

		try {
			fmsRuleDtoListFromCache = (List<FmsRuleDto>) cacheUtil.getFromCache(ALL_RULES, ALL_RULES,FmsRuleCacheDto.class);

		} catch (RedisConnectionFailureException e) {
			throw new CacheException();
		}

		return fmsRuleDtoListFromCache;
	}

	@Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = { CacheException.class })
	@Override
	public List<FmsRuleDto> getGlobalRules() throws Exception {

		List<FmsRuleDto> fmsGlobalRuleDtoListFromCache = null;

		if (!FmsUtil.getInstance().isAnyObjectNull(cacheUtil)) {
			log.debug("Autowired object(s) null! Exit from method");
			return null;
		}

		try {
			fmsGlobalRuleDtoListFromCache = (List<FmsRuleDto>) cacheUtil.getFromCache(GLOBAL_RULES, GLOBAL_RULES,FmsRuleCacheDto.class);

		} catch (RedisConnectionFailureException e) {
			throw new CacheException();
		}

		return fmsGlobalRuleDtoListFromCache;
	}

}
