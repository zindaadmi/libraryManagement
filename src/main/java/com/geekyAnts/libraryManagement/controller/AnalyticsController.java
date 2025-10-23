package com.geekyAnts.libraryManagement.controller;

import com.geekyAnts.libraryManagement.dto.*;
import com.geekyAnts.libraryManagement.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {
    
    private final AnalyticsService analyticsService;
    
    @GetMapping("/top-borrowed-books")
    public ResponseEntity<ApiResponse<List<TopBorrowedBookDTO>>> getTopBorrowedBooks() {
        List<TopBorrowedBookDTO> books = analyticsService.getTopBorrowedBooks();
        return ResponseEntity.ok(ApiResponse.success(books));
    }
    
    @GetMapping("/borrower-activity")
    public ResponseEntity<ApiResponse<List<BorrowerActivityDTO>>> getBorrowerActivity() {
        List<BorrowerActivityDTO> activity = analyticsService.getBorrowerActivity();
        return ResponseEntity.ok(ApiResponse.success(activity));
    }
    
    @GetMapping("/books/similar/{id}")
    public ResponseEntity<ApiResponse<List<SimilarBookDTO>>> getSimilarBooks(@PathVariable UUID id) {
        try {
            List<SimilarBookDTO> books = analyticsService.getSimilarBooks(id);
            return ResponseEntity.ok(ApiResponse.success(books));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/books/availability-summary")
    public ResponseEntity<ApiResponse<List<AvailabilitySummaryDTO>>> getAvailabilitySummary() {
        List<AvailabilitySummaryDTO> summary = analyticsService.getAvailabilitySummary();
        return ResponseEntity.ok(ApiResponse.success(summary));
    }
}
