package com.geekyAnts.libraryManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimilarBookDTO {
    private UUID bookId;
    private String title;
    private String author;
    private String category;
    private Integer availableCopies;
    private Boolean isAvailable;
}
