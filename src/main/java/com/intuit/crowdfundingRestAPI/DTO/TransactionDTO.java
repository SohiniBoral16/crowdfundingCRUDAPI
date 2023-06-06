package com.intuit.crowdfundingRestAPI.DTO;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDTO {
	private UserDTO user;
    private ProjectDTO project;
    private BigDecimal amount;

}
