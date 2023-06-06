package com.intuit.crowdfundingRestAPI.repo;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.intuit.crowdfundingRestAPI.entity.Transaction;

public interface TransactionRepo extends JpaRepository<Transaction, Integer> {

	@Query(value ="Select count(*) from Transactions where ProjectID = ?", nativeQuery =true)
	Integer existsByProjectID(int id);

	@Modifying
	@Transactional
	@Query(value = "DELETE FROM Transactions WHERE ProjectID = ?", nativeQuery =true)
	void deleteByProjectID(int id);

}

