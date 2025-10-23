package com.geekyAnts.libraryManagement.service;

import com.geekyAnts.libraryManagement.dto.BookDTO;
import com.geekyAnts.libraryManagement.dto.BookRequestDTO;
import com.geekyAnts.libraryManagement.entity.Book;
import com.geekyAnts.libraryManagement.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BookService {
    
    private final BookRepository bookRepository;
    
    public BookDTO addBook(BookRequestDTO request) {
        try {
            log.info("Adding new book: title={}, author={}, category={}, copies={}", 
                    request.getTitle(), request.getAuthor(), request.getCategory(), request.getTotalCopies());
            
            // Check if book with same title and author already exists
            Optional<Book> existingBook = bookRepository.findByTitleAndAuthor(request.getTitle(), request.getAuthor());
            
            if (existingBook.isPresent()) {
                log.info("Book already exists, increasing copies. Current copies: {}, Adding: {}", 
                        existingBook.get().getTotalCopies(), request.getTotalCopies());
                // Increase total copies and available copies
                Book book = existingBook.get();
                book.setTotalCopies(book.getTotalCopies() + request.getTotalCopies());
                book.setAvailableCopies(book.getAvailableCopies() + request.getTotalCopies());
                book.setIsAvailable(book.getAvailableCopies() > 0);
                Book savedBook = bookRepository.save(book);
                log.info("Successfully updated book copies. New total: {}, Available: {}", 
                        savedBook.getTotalCopies(), savedBook.getAvailableCopies());
                return BookDTO.fromEntity(savedBook);
            } else {
                log.info("Creating new book entry");
                // Create new book
                Book book = new Book();
                book.setTitle(request.getTitle());
                book.setAuthor(request.getAuthor());
                book.setCategory(request.getCategory());
                book.setTotalCopies(request.getTotalCopies());
                book.setAvailableCopies(request.getTotalCopies());
                book.setIsAvailable(request.getTotalCopies() > 0);
                book.setIsDeleted(false);
                Book savedBook = bookRepository.save(book);
                log.info("Successfully created new book with ID: {}", savedBook.getId());
                return BookDTO.fromEntity(savedBook);
            }
        } catch (Exception e) {
            log.error("Error adding book: title={}, author={}, error={}", 
                    request.getTitle(), request.getAuthor(), e.getMessage(), e);
            throw new RuntimeException("Failed to add book: " + e.getMessage(), e);
        }
    }
    
    @Transactional(readOnly = true)
    public Page<BookDTO> getAllBooks(Pageable pageable) {
        try {
            log.debug("Fetching all books with pagination: page={}, size={}", 
                    pageable.getPageNumber(), pageable.getPageSize());
            return bookRepository.findByIsDeletedFalse(pageable)
                    .map(BookDTO::fromEntity);
        } catch (Exception e) {
            log.error("Error fetching all books: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch books: " + e.getMessage(), e);
        }
    }
    
    @Transactional(readOnly = true)
    public Page<BookDTO> getBooksWithFilters(String category, Boolean available, Pageable pageable) {
        return bookRepository.findBooksWithFilters(category, available, pageable)
                .map(BookDTO::fromEntity);
    }
    
    @Transactional(readOnly = true)
    public Page<BookDTO> searchBooks(String title, String author, Pageable pageable) {
        return bookRepository.searchBooks(title, author, pageable)
                .map(BookDTO::fromEntity);
    }
    
    @Transactional(readOnly = true)
    public Optional<BookDTO> getBookById(UUID id) {
        return bookRepository.findByIdAndIsDeletedFalse(id)
                .map(BookDTO::fromEntity);
    }
    
    public BookDTO updateBook(UUID id, BookRequestDTO request) {
        try {
            log.info("Updating book with ID: {}", id);
            Book book = bookRepository.findByIdAndIsDeletedFalse(id)
                    .orElseThrow(() -> {
                        log.error("Book not found with id: {}", id);
                        return new RuntimeException("Book not found with id: " + id);
                    });
            
            log.info("Updating book: {} by {} - changing copies from {} to {}", 
                    book.getTitle(), book.getAuthor(), book.getTotalCopies(), request.getTotalCopies());
            
            book.setTitle(request.getTitle());
            book.setAuthor(request.getAuthor());
            book.setCategory(request.getCategory());
            
            // Update copies if needed
            int copyDifference = request.getTotalCopies() - book.getTotalCopies();
            book.setTotalCopies(request.getTotalCopies());
            book.setAvailableCopies(book.getAvailableCopies() + copyDifference);
            book.setIsAvailable(book.getAvailableCopies() > 0);
            
            Book savedBook = bookRepository.save(book);
            log.info("Successfully updated book. New total copies: {}, Available copies: {}", 
                    savedBook.getTotalCopies(), savedBook.getAvailableCopies());
            return BookDTO.fromEntity(savedBook);
        } catch (Exception e) {
            log.error("Error updating book with ID: {}, error={}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to update book: " + e.getMessage(), e);
        }
    }
    
    public void deleteBook(UUID id) {
        try {
            log.info("Deleting book with ID: {}", id);
            Book book = bookRepository.findByIdAndIsDeletedFalse(id)
                    .orElseThrow(() -> {
                        log.error("Book not found with id: {}", id);
                        return new RuntimeException("Book not found with id: " + id);
                    });
            
            // Check if book has active borrow records
            if (!book.getBorrowRecords().stream().allMatch(record -> !record.getIsActive() || record.getReturnDate() != null)) {
                log.warn("Cannot delete book with active borrow records. Book ID: {}", id);
                throw new RuntimeException("Cannot delete book with active borrow records");
            }
            
            book.setIsDeleted(true);
            bookRepository.save(book);
            log.info("Successfully deleted book with ID: {}", id);
        } catch (Exception e) {
            log.error("Error deleting book with ID: {}, error={}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to delete book: " + e.getMessage(), e);
        }
    }
    
    @Cacheable("availableBooks")
    @Transactional(readOnly = true)
    public List<BookDTO> getAvailableBooks() {
        return bookRepository.findAvailableBooks()
                .stream()
                .map(BookDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<BookDTO> getBooksByCategory(String category) {
        return bookRepository.findByCategoryAndIsDeletedFalse(category)
                .stream()
                .map(BookDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public void updateAvailableCopies(UUID bookId, int change) {
        Book book = bookRepository.findByIdAndIsDeletedFalse(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + bookId));
        
        int newAvailableCopies = book.getAvailableCopies() + change;
        if (newAvailableCopies < 0) {
            throw new RuntimeException("Cannot reduce available copies below 0");
        }
        
        book.setAvailableCopies(newAvailableCopies);
        book.setIsAvailable(newAvailableCopies > 0);
        bookRepository.save(book);
    }
}
