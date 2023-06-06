package com.intuit.crowdfundingRestAPI.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import com.intuit.crowdfundingRestAPI.entity.Transaction;
import com.intuit.crowdfundingRestAPI.repo.TransactionRepo;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepo transactionRepo;

    // Save transaction
    public Transaction saveTransaction(Transaction transaction) {
        return transactionRepo.save(transaction);
    }

    // Get transaction by id
    public Optional<Transaction> getTransactionById(int id) {
        return transactionRepo.findById(id);
    }

    // Get all transactions
    public List<Transaction> getAllTransactions() {
        return transactionRepo.findAll();
    }

    // Delete transaction
    public void deleteTransaction(int id) {
    	transactionRepo.deleteById(id);
    }
    
    public void deleteTransactionByProjectId(int id) {
    	transactionRepo.deleteByProjectID(id);
    }
    
    public boolean existsTransactionForProjId(int id) {
    	return (transactionRepo.existsByProjectID(id) > 0) ? true: false;
    }

}

