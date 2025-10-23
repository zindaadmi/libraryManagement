package com.geekyAnts.libraryManagement.service;

import com.geekyAnts.libraryManagement.dto.BorrowRecordDTO;
import com.geekyAnts.libraryManagement.dto.BorrowRequestDTO;
import com.geekyAnts.libraryManagement.dto.ReturnRequestDTO;
import com.geekyAnts.libraryManagement.entity.Book;
import com.geekyAnts.libraryManagement.entity.BorrowRecord;
import com.geekyAnts.libraryManagement.entity.Borrower;
import com.geekyAnts.libraryManagement.entity.FinePolicy;
import com.geekyAnts.libraryManagement.repository.BookRepository;
import com.geekyAnts.libraryManagement.repository.BorrowRecordRepository;
import com.geekyAnts.libraryManagement.repository.BorrowerRepository;
import com.geekyAnts.libraryManagement.repository.FinePolicyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BorrowService {
    
    private final BorrowRecordRepository borrowRecordRepository;
    private final BookRepository bookRepository;
    private final BorrowerRepository borrowerRepository;
    private final FinePolicyRepository finePolicyRepository;
    
    public BorrowRecordDTO borrowBook(BorrowRequestDTO request) {
        try {
            log.info("Processing borrow request: borrowerId={}, bookId={}", 
                    request.getBorrowerId(), request.getBookId());
            
            // Validate borrower exists and is active
            Borrower borrower = borrowerRepository.findByIdAndIsActiveTrue(request.getBorrowerId())
                    .orElseThrow(() -> {
                        log.error("Borrower not found or inactive: {}", request.getBorrowerId());
                        return new RuntimeException("Borrower not found or inactive");
                    });
            
            // Validate book exists and is not deleted
            Book book = bookRepository.findByIdAndIsDeletedFalse(request.getBookId())
                    .orElseThrow(() -> {
                        log.error("Book not found or deleted: {}", request.getBookId());
                        return new RuntimeException("Book not found or deleted");
                    });
            
            log.info("Borrower: {} ({}), Book: {} by {} - Available copies: {}", 
                    borrower.getName(), borrower.getMembershipType(), 
                    book.getTitle(), book.getAuthor(), book.getAvailableCopies());
            
            // Check if borrower has reached max borrow limit
            long activeBorrowCount = borrowRecordRepository.countActiveBorrowRecordsByBorrower(request.getBorrowerId());
            if (activeBorrowCount >= borrower.getMaxBorrowLimit()) {
                log.warn("Borrower {} has reached maximum borrow limit: {}/{}", 
                        borrower.getName(), activeBorrowCount, borrower.getMaxBorrowLimit());
                throw new RuntimeException("Borrower has reached maximum borrow limit of " + borrower.getMaxBorrowLimit());
            }
            
            // Check if book is available
            if (book.getAvailableCopies() <= 0) {
                log.warn("Book {} is not available for borrowing. Available copies: {}", 
                        book.getTitle(), book.getAvailableCopies());
                throw new RuntimeException("Book is not available for borrowing");
            }
            
            // Check if borrower already has this book borrowed
            Optional<BorrowRecord> existingRecord = borrowRecordRepository
                    .findActiveBorrowRecordByBorrowerAndBook(request.getBorrowerId(), request.getBookId());
            if (existingRecord.isPresent()) {
                log.warn("Borrower {} already has book {} borrowed", borrower.getName(), book.getTitle());
                throw new RuntimeException("Borrower already has this book borrowed");
            }
            
            // Create borrow record
            BorrowRecord borrowRecord = new BorrowRecord();
            borrowRecord.setBook(book);
            borrowRecord.setBorrower(borrower);
            borrowRecord.setBorrowDate(LocalDate.now());
            borrowRecord.setDueDate(LocalDate.now().plusDays(14));
            borrowRecord.setIsActive(true);
            
            // Reduce available copies
            book.setAvailableCopies(book.getAvailableCopies() - 1);
            book.setIsAvailable(book.getAvailableCopies() > 0);
            bookRepository.save(book);
            
            BorrowRecord savedRecord = borrowRecordRepository.save(borrowRecord);
            log.info("Successfully borrowed book. Record ID: {}, Due date: {}, Remaining copies: {}", 
                    savedRecord.getId(), savedRecord.getDueDate(), book.getAvailableCopies());
            
            return BorrowRecordDTO.fromEntity(savedRecord);
        } catch (Exception e) {
            log.error("Error processing borrow request: borrowerId={}, bookId={}, error={}", 
                    request.getBorrowerId(), request.getBookId(), e.getMessage(), e);
            throw new RuntimeException("Failed to borrow book: " + e.getMessage(), e);
        }
    }
    
    public BorrowRecordDTO returnBook(ReturnRequestDTO request) {
        try {
            log.info("Processing return request for borrow record: {}", request.getBorrowRecordId());
            
            BorrowRecord borrowRecord = borrowRecordRepository.findById(request.getBorrowRecordId())
                    .orElseThrow(() -> {
                        log.error("Borrow record not found: {}", request.getBorrowRecordId());
                        return new RuntimeException("Borrow record not found");
                    });
            
            if (!borrowRecord.getIsActive() || borrowRecord.getReturnDate() != null) {
                log.warn("Book is already returned or record is inactive. Record ID: {}, Active: {}, Return Date: {}", 
                        borrowRecord.getId(), borrowRecord.getIsActive(), borrowRecord.getReturnDate());
                throw new RuntimeException("Book is already returned or record is inactive");
            }
            
            log.info("Returning book: {} by {} to borrower: {}", 
                    borrowRecord.getBook().getTitle(), borrowRecord.getBook().getAuthor(), 
                    borrowRecord.getBorrower().getName());
            
            // Set return date
            borrowRecord.setReturnDate(LocalDate.now());
            
            // Calculate fine if overdue
            if (borrowRecord.isOverdue()) {
                BigDecimal fineAmount = calculateFine(borrowRecord);
                borrowRecord.setFineAmount(fineAmount);
                log.warn("Book returned overdue. Fine calculated: ${}, Days overdue: {}", 
                        fineAmount, borrowRecord.getDaysOverdue());
            } else {
                log.info("Book returned on time. No fine applied.");
            }
            
            // Increase available copies
            Book book = borrowRecord.getBook();
            book.setAvailableCopies(book.getAvailableCopies() + 1);
            book.setIsAvailable(book.getAvailableCopies() > 0);
            bookRepository.save(book);
            
            BorrowRecord savedRecord = borrowRecordRepository.save(borrowRecord);
            log.info("Successfully returned book. Record ID: {}, Return date: {}, Available copies: {}", 
                    savedRecord.getId(), savedRecord.getReturnDate(), book.getAvailableCopies());
            
            return BorrowRecordDTO.fromEntity(savedRecord);
        } catch (Exception e) {
            log.error("Error processing return request: borrowRecordId={}, error={}", 
                    request.getBorrowRecordId(), e.getMessage(), e);
            throw new RuntimeException("Failed to return book: " + e.getMessage(), e);
        }
    }
    
    @Transactional(readOnly = true)
    public List<BorrowRecordDTO> getActiveBorrowRecords() {
        return borrowRecordRepository.findActiveBorrowRecords()
                .stream()
                .map(BorrowRecordDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<BorrowRecordDTO> getBorrowHistoryByBorrower(UUID borrowerId) {
        return borrowRecordRepository.findBorrowHistoryByBorrower(borrowerId)
                .stream()
                .map(BorrowRecordDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<BorrowRecordDTO> getOverdueBorrowRecords() {
        return borrowRecordRepository.findOverdueBorrowRecords()
                .stream()
                .map(BorrowRecordDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    private BigDecimal calculateFine(BorrowRecord borrowRecord) {
        // Get fine policy for book category
        Optional<FinePolicy> finePolicy = finePolicyRepository.findByCategoryAndIsActiveTrue(borrowRecord.getBook().getCategory());
        
        BigDecimal finePerDay = finePolicy.map(FinePolicy::getFinePerDay)
                .orElse(BigDecimal.valueOf(1.0)); // Default fine of $1 per day
        
        long daysOverdue = borrowRecord.getDaysOverdue();
        return finePerDay.multiply(BigDecimal.valueOf(daysOverdue));
    }
}
