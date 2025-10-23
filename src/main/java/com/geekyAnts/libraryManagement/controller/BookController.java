package com.geekyAnts.libraryManagement.controller;

import com.geekyAnts.libraryManagement.dto.ApiResponse;
import com.geekyAnts.libraryManagement.dto.BookDTO;
import com.geekyAnts.libraryManagement.dto.BookRequestDTO;
import com.geekyAnts.libraryManagement.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Slf4j
public class BookController {
    
    private final BookService bookService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<BookDTO>> addBook(@Valid @RequestBody BookRequestDTO request) {
        log.info("Received request to add book: {}", request.getTitle());
        try {
            BookDTO book = bookService.addBook(request);
            log.info("Successfully added book with ID: {}", book.getId());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Book added successfully", book));
        } catch (Exception e) {
            log.error("Error adding book: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to add book: " + e.getMessage()));
        }
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<Page<BookDTO>>> getAllBooks(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean available,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<BookDTO> books;
        if (title != null || author != null) {
            books = bookService.searchBooks(title, author, pageable);
        } else if (category != null || available != null) {
            books = bookService.getBooksWithFilters(category, available, pageable);
        } else {
            books = bookService.getAllBooks(pageable);
        }
        
        return ResponseEntity.ok(ApiResponse.success(books));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookDTO>> getBookById(@PathVariable UUID id) {
        return bookService.getBookById(id)
                .map(book -> ResponseEntity.ok(ApiResponse.success(book)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BookDTO>> updateBook(
            @PathVariable UUID id, 
            @Valid @RequestBody BookRequestDTO request) {
        try {
            BookDTO book = bookService.updateBook(id, request);
            return ResponseEntity.ok(ApiResponse.success("Book updated successfully", book));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBook(@PathVariable UUID id) {
        try {
            bookService.deleteBook(id);
            return ResponseEntity.ok(ApiResponse.success("Book deleted successfully", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<BookDTO>>> getAvailableBooks() {
        List<BookDTO> books = bookService.getAvailableBooks();
        return ResponseEntity.ok(ApiResponse.success(books));
    }
    
    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<List<BookDTO>>> getBooksByCategory(@PathVariable String category) {
        List<BookDTO> books = bookService.getBooksByCategory(category);
        return ResponseEntity.ok(ApiResponse.success(books));
    }
    
    @GetMapping("/test-exception")
    public ResponseEntity<ApiResponse<Void>> testException() {
        throw new RuntimeException("Test exception for Global Exception Handler");
    }
}
