package com.geekyAnts.libraryManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowerActivityDTO {
    private UUID borrowerId;
    private String borrowerName;
    private String borrowerEmail;
    private Long totalBorrowed;
    private Long overdueCount;
    private BigDecimal totalFines;
}
