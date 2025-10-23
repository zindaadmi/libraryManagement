package com.geekyAnts.libraryManagement.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReturnRequestDTO {
    @NotNull(message = "Borrow record ID is required")
    private UUID borrowRecordId;
}
