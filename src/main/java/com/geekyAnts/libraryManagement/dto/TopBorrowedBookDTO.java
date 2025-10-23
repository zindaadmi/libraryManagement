package com.geekyAnts.libraryManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopBorrowedBookDTO {
    private UUID bookId;
    private String bookTitle;
    private String bookAuthor;
    private String bookCategory;
    private Long borrowCount;
}
