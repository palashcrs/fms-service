package com.get.edgepay.fms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.get.edgepay.fms.domain.model.FmsRuleMerchant;

@Repository
public interface FmsRuleMerchantRepository extends JpaRepository<FmsRuleMerchant, Long> {

}
