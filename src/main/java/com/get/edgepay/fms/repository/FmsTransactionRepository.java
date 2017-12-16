package com.get.edgepay.fms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.get.edgepay.fms.domain.model.FmsTransaction;

@Repository
public interface FmsTransactionRepository extends JpaRepository<FmsTransaction, Long> {

	List<FmsTransaction> findByFmsTransactionStatus(String fmsTransactionStatus);
}
