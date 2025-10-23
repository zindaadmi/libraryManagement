package com.geekyAnts.libraryManagement.service;

import com.geekyAnts.libraryManagement.dto.*;
import com.geekyAnts.libraryManagement.entity.Book;
import com.geekyAnts.libraryManagement.repository.BookRepository;
import com.geekyAnts.libraryManagement.repository.BorrowRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalyticsService {
    
    private final BorrowRecordRepository borrowRecordRepository;
    private final BookRepository bookRepository;
    
    public List<TopBorrowedBookDTO> getTopBorrowedBooks() {
        List<Object[]> results = borrowRecordRepository.findTopBorrowedBooks();
        
        return results.stream()
                .limit(5) // Top 5
                .map(result -> {
                    UUID bookId = (UUID) result[0];
                    Long borrowCount = (Long) result[1];
                    
                    // Get book details
                    Book book = bookRepository.findById(bookId).orElse(null);
                    if (book == null) return null;
                    
                    return new TopBorrowedBookDTO(
                            bookId,
                            book.getTitle(),
                            book.getAuthor(),
                            book.getCategory(),
                            borrowCount
                    );
                })
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }
    
    public List<BorrowerActivityDTO> getBorrowerActivity() {
        List<Object[]> results = borrowRecordRepository.findBorrowerActivity();
        
        return results.stream()
                .map(result -> {
                    UUID borrowerId = (UUID) result[0];
                    Long totalBorrowed = (Long) result[1];
                    Long overdueCount = (Long) result[2];
                    java.math.BigDecimal totalFines = (java.math.BigDecimal) result[3];
                    
                    // Get borrower details (you might want to join this in the query for better performance)
                    // For now, we'll return basic info
                    return new BorrowerActivityDTO(
                            borrowerId,
                            "Borrower " + borrowerId.toString().substring(0, 8), // Simplified
                            "email@example.com", // Simplified
                            totalBorrowed,
                            overdueCount,
                            totalFines
                    );
                })
                .collect(Collectors.toList());
    }
    
    public List<SimilarBookDTO> getSimilarBooks(UUID bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        
        // Find books with same category or author
        List<Book> similarBooks = bookRepository.findByCategoryAndIsDeletedFalse(book.getCategory())
                .stream()
                .filter(b -> !b.getId().equals(bookId))
                .limit(5)
                .collect(Collectors.toList());
        
        return similarBooks.stream()
                .map(b -> new SimilarBookDTO(
                        b.getId(),
                        b.getTitle(),
                        b.getAuthor(),
                        b.getCategory(),
                        b.getAvailableCopies(),
                        b.getIsAvailable()
                ))
                .collect(Collectors.toList());
    }
    
    public List<AvailabilitySummaryDTO> getAvailabilitySummary() {
        List<Object[]> results = bookRepository.getAvailabilitySummary();
        
        return results.stream()
                .map(result -> new AvailabilitySummaryDTO(
                        (String) result[0],
                        (Long) result[1],
                        (Long) result[2]
                ))
                .collect(Collectors.toList());
    }
}
