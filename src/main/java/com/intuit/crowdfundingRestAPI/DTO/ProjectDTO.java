package com.intuit.crowdfundingRestAPI.DTO;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDTO {
	
	private Integer id;
    private String name;
    private String description;
    private BigDecimal goalAmount;
    private BigDecimal raisedAmount;
    private Integer userId;

}
