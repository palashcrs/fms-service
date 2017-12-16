package com.get.edgepay.fms.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.get.edgepay.fms.constant.FmsCacheConstant;
import com.get.edgepay.fms.converter.FmsRuleEntityObjectConverter;
import com.get.edgepay.fms.domain.dto.FmsRuleEntityCacheDto;
import com.get.edgepay.fms.domain.dto.FmsRuleEntityDto;
import com.get.edgepay.fms.domain.model.FmsRuleEntity;
import com.get.edgepay.fms.exception.CacheException;
import com.get.edgepay.fms.exception.DuplicateRecordException;
import com.get.edgepay.fms.exception.RecordFetchException;
import com.get.edgepay.fms.repository.FmsRuleEntityRepository;
import com.get.edgepay.fms.service.FmsRuleEntityService;
import com.get.edgepay.fms.util.CacheUtil;
import com.get.edgepay.fms.util.FmsUtil;

@Service
public class FmsRuleEntityServiceImpl implements FmsRuleEntityService {

	private static final Logger log = LoggerFactory.getLogger(FmsRuleEntityServiceImpl.class);
	
	private static final String ALL_RULEENTITY = FmsCacheConstant.ALL_RULEENTITY.name();

	@Autowired
	private FmsRuleEntityRepository fmsRuleEntityRepository;

	@Autowired
	private CacheUtil cacheUtil;

	@Transactional(rollbackFor = { DuplicateRecordException.class, CacheException.class })
	@Override
	public int createNewRuleEntity(List<FmsRuleEntityDto> fmsRuleEntities) throws Exception {

		int rowsCreated = 0;

		if (!FmsUtil.getInstance().isAnyObjectNull(fmsRuleEntityRepository)) {
			log.debug("Autowired object(s) null! Exit from method");
			return 0;
		}

		try {
			List<FmsRuleEntity> ruleEntityList = FmsRuleEntityObjectConverter.getInstance().convertFMSRuleTypeDtoToEntity(fmsRuleEntities);
			if (ruleEntityList != null) {
				ruleEntityList = fmsRuleEntityRepository.save(ruleEntityList);
				fmsRuleEntityRepository.flush();
			}
			rowsCreated = (ruleEntityList != null) ? ruleEntityList.size() : 0;
			if (rowsCreated >0) {
				List<FmsRuleEntity> ruleTypeListFromDB = fmsRuleEntityRepository.findAll();
				if (ruleTypeListFromDB != null) {
					cacheUtil.removeFromCache(ALL_RULEENTITY, FmsRuleEntityCacheDto.class);
					List<FmsRuleEntityDto> allRuleEntityDtoList = FmsRuleEntityObjectConverter.getInstance().convertFMSRuleTypeEntityToDTO(ruleTypeListFromDB);
					if (allRuleEntityDtoList != null) {
						cacheUtil.addToCache(ALL_RULEENTITY, ALL_RULEENTITY, allRuleEntityDtoList);
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

	@Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = { RecordFetchException.class, CacheException.class })
	@Override
	public List<FmsRuleEntityDto> getAllRuleEntities() throws Exception {

		List<FmsRuleEntityDto> ruleEntityistFromCache = null;

		if (!FmsUtil.getInstance().isAnyObjectNull(cacheUtil)) {
			log.debug("Autowired object(s) null! Exit from method");
			return null;
		}

		try {
			ruleEntityistFromCache = (List<FmsRuleEntityDto>) cacheUtil.getFromCache(ALL_RULEENTITY, ALL_RULEENTITY, FmsRuleEntityCacheDto.class);

		} catch (RedisConnectionFailureException e) {
			throw new CacheException();
		}

		return ruleEntityistFromCache;
	}
}
