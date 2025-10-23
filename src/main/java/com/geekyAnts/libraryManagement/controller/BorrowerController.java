package com.geekyAnts.libraryManagement.controller;

import com.geekyAnts.libraryManagement.dto.ApiResponse;
import com.geekyAnts.libraryManagement.dto.BorrowerDTO;
import com.geekyAnts.libraryManagement.dto.BorrowerRequestDTO;
import com.geekyAnts.libraryManagement.dto.BorrowRecordDTO;
import com.geekyAnts.libraryManagement.service.BorrowerService;
import com.geekyAnts.libraryManagement.service.BorrowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/borrowers")
@RequiredArgsConstructor
public class BorrowerController {
    
    private final BorrowerService borrowerService;
    private final BorrowService borrowService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<BorrowerDTO>> registerBorrower(@Valid @RequestBody BorrowerRequestDTO request) {
        try {
            BorrowerDTO borrower = borrowerService.registerBorrower(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Borrower registered successfully", borrower));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<BorrowerDTO>>> getAllBorrowers() {
        List<BorrowerDTO> borrowers = borrowerService.getAllBorrowers();
        return ResponseEntity.ok(ApiResponse.success(borrowers));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BorrowerDTO>> getBorrowerById(@PathVariable UUID id) {
        return borrowerService.getBorrowerById(id)
                .map(borrower -> ResponseEntity.ok(ApiResponse.success(borrower)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{id}/records")
    public ResponseEntity<ApiResponse<List<BorrowRecordDTO>>> getBorrowerRecords(@PathVariable UUID id) {
        List<BorrowRecordDTO> records = borrowService.getBorrowHistoryByBorrower(id);
        return ResponseEntity.ok(ApiResponse.success(records));
    }
    
    @GetMapping("/overdue")
    public ResponseEntity<ApiResponse<List<BorrowerDTO>>> getBorrowersWithOverdueBooks() {
        List<BorrowerDTO> borrowers = borrowerService.getBorrowersWithOverdueBooks();
        return ResponseEntity.ok(ApiResponse.success(borrowers));
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<BorrowerDTO>>> searchBorrowers(
            @RequestParam String searchTerm) {
        List<BorrowerDTO> borrowers = borrowerService.searchBorrowers(searchTerm);
        return ResponseEntity.ok(ApiResponse.success(borrowers));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BorrowerDTO>> updateBorrower(
            @PathVariable UUID id, 
            @Valid @RequestBody BorrowerRequestDTO request) {
        try {
            BorrowerDTO borrower = borrowerService.updateBorrower(id, request);
            return ResponseEntity.ok(ApiResponse.success("Borrower updated successfully", borrower));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deactivateBorrower(@PathVariable UUID id) {
        try {
            borrowerService.deactivateBorrower(id);
            return ResponseEntity.ok(ApiResponse.success("Borrower deactivated successfully", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
