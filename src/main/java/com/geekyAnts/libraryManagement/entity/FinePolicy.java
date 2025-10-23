package com.geekyAnts.libraryManagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "fine_policies")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinePolicy {
    
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;
    
    @NotBlank(message = "Category is required")
    @Column(nullable = false, unique = true)
    private String category;
    
    @NotNull(message = "Fine per day is required")
    @DecimalMin(value = "0.0", message = "Fine per day must be non-negative")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal finePerDay;
    
    @Column(nullable = false)
    private Boolean isActive = true;
}
