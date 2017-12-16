package com.get.edgepay.fms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.get.edgepay.fms.domain.model.FmsRuleEntity;

@Repository
public interface FmsRuleEntityRepository extends JpaRepository<FmsRuleEntity, Long> {

}
