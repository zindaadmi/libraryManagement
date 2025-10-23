package com.geekyAnts.libraryManagement.dto;

import com.geekyAnts.libraryManagement.entity.Book;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {
    private UUID id;
    private String title;
    private String author;
    private String category;
    private Integer totalCopies;
    private Integer availableCopies;
    private Boolean isAvailable;
    
    public static BookDTO fromEntity(Book book) {
        return new BookDTO(
            book.getId(),
            book.getTitle(),
            book.getAuthor(),
            book.getCategory(),
            book.getTotalCopies(),
            book.getAvailableCopies(),
            book.getIsAvailable()
        );
    }
}
