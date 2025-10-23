package com.geekyAnts.libraryManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilitySummaryDTO {
    private String category;
    private Long total;
    private Long available;
}
