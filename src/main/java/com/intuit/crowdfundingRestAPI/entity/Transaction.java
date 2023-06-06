package com.intuit.crowdfundingRestAPI.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Integer id;

	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "UserID", referencedColumnName = "ID")
	    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	    @JsonIgnore
	    private User user;

	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "ProjectID", referencedColumnName = "ID")
	    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	    @JsonIgnore
	    private Project project;

	    @Column(name = "Amount", precision = 10, scale = 2)
	    private BigDecimal amount;

	    @CreationTimestamp
	    @Column(name = "Date", updatable = false)
	    private LocalDateTime date;
}

