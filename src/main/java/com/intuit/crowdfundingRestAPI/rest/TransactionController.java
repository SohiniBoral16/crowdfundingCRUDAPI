package com.intuit.crowdfundingRestAPI.rest;

import com.intuit.crowdfundingRestAPI.DTO.TransactionDTO;
import com.intuit.crowdfundingRestAPI.entity.Project;
import com.intuit.crowdfundingRestAPI.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import com.intuit.crowdfundingRestAPI.entity.Transaction;
import com.intuit.crowdfundingRestAPI.entity.User;
import com.intuit.crowdfundingRestAPI.service.TransactionService;
import com.intuit.crowdfundingRestAPI.service.UserService;

@RestController
@CrossOrigin
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private ProjectService projectService;
    
    @Autowired
    private UserService userService;

    // 1. Create Transaction
    @PostMapping
    public ResponseEntity<?> createTransaction(@RequestBody TransactionDTO transactionDTO) {
        // Get the user and project IDs from the transactionDTO
        Integer userId = transactionDTO.getUser().getId();
        Integer projectId = transactionDTO.getProject().getId();

        // Fetch the user and project entities based on these IDs
        User user = userService.getUser(userId);
        Project project = projectService.getProject(projectId);

        if (user == null) {
            return new ResponseEntity<String>("User not found", HttpStatus.BAD_REQUEST);
        }

        if (project == null) {
            return new ResponseEntity<String>("Project not found", HttpStatus.BAD_REQUEST);
        }

        // Create a new Transaction entity
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setProject(project);
        transaction.setAmount(transactionDTO.getAmount());

        // Update the raisedAmount in the project
        BigDecimal newRaisedAmount;
        BigDecimal existingRaisedAmount = project.getRaisedAmount();

        if (existingRaisedAmount != null) {
            newRaisedAmount = existingRaisedAmount.add(transaction.getAmount());
        } else {
            newRaisedAmount = transaction.getAmount();
        }
        project.setRaisedAmount(newRaisedAmount);
        projectService.saveProject(project);

        // Save the transaction
        Transaction newTransaction = transactionService.saveTransaction(transaction);
        return new ResponseEntity<Transaction>(newTransaction, HttpStatus.CREATED);
    }


}
