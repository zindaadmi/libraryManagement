package com.geekyAnts.libraryManagement.controller;

import com.geekyAnts.libraryManagement.dto.ApiResponse;
import com.geekyAnts.libraryManagement.dto.BorrowRecordDTO;
import com.geekyAnts.libraryManagement.dto.BorrowRequestDTO;
import com.geekyAnts.libraryManagement.dto.ReturnRequestDTO;
import com.geekyAnts.libraryManagement.service.BorrowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BorrowController {
    
    private final BorrowService borrowService;
    
    @PostMapping("/borrow")
    public ResponseEntity<ApiResponse<BorrowRecordDTO>> borrowBook(@Valid @RequestBody BorrowRequestDTO request) {
        try {
            BorrowRecordDTO record = borrowService.borrowBook(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Book borrowed successfully", record));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping("/return")
    public ResponseEntity<ApiResponse<BorrowRecordDTO>> returnBook(@Valid @RequestBody ReturnRequestDTO request) {
        try {
            BorrowRecordDTO record = borrowService.returnBook(request);
            return ResponseEntity.ok(ApiResponse.success("Book returned successfully", record));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/records/active")
    public ResponseEntity<ApiResponse<List<BorrowRecordDTO>>> getActiveBorrowRecords() {
        List<BorrowRecordDTO> records = borrowService.getActiveBorrowRecords();
        return ResponseEntity.ok(ApiResponse.success(records));
    }
    
    @GetMapping("/records/overdue")
    public ResponseEntity<ApiResponse<List<BorrowRecordDTO>>> getOverdueBorrowRecords() {
        List<BorrowRecordDTO> records = borrowService.getOverdueBorrowRecords();
        return ResponseEntity.ok(ApiResponse.success(records));
    }
    
    @GetMapping("/records/borrower/{borrowerId}")
    public ResponseEntity<ApiResponse<List<BorrowRecordDTO>>> getBorrowHistoryByBorrower(@PathVariable UUID borrowerId) {
        List<BorrowRecordDTO> records = borrowService.getBorrowHistoryByBorrower(borrowerId);
        return ResponseEntity.ok(ApiResponse.success(records));
    }
}
