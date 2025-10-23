package com.geekyAnts.libraryManagement.service;

import com.geekyAnts.libraryManagement.dto.BorrowerDTO;
import com.geekyAnts.libraryManagement.dto.BorrowerRequestDTO;
import com.geekyAnts.libraryManagement.entity.Borrower;
import com.geekyAnts.libraryManagement.repository.BorrowerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class BorrowerService {
    
    private final BorrowerRepository borrowerRepository;
    
    public BorrowerDTO registerBorrower(BorrowerRequestDTO request) {
        try {
            log.info("Registering new borrower: name={}, email={}, membershipType={}", 
                    request.getName(), request.getEmail(), request.getMembershipType());
            
            // Check if email already exists
            if (borrowerRepository.existsByEmailAndIsActiveTrue(request.getEmail())) {
                log.warn("Borrower with email {} already exists", request.getEmail());
                throw new RuntimeException("Borrower with email " + request.getEmail() + " already exists");
            }
            
            Borrower borrower = new Borrower();
            borrower.setName(request.getName());
            borrower.setEmail(request.getEmail());
            borrower.setMembershipType(request.getMembershipType());
            borrower.setIsActive(true);
            
            Borrower savedBorrower = borrowerRepository.save(borrower);
            log.info("Successfully registered borrower with ID: {}", savedBorrower.getId());
            return BorrowerDTO.fromEntity(savedBorrower);
        } catch (Exception e) {
            log.error("Error registering borrower: name={}, email={}, error={}", 
                    request.getName(), request.getEmail(), e.getMessage(), e);
            throw new RuntimeException("Failed to register borrower: " + e.getMessage(), e);
        }
    }
    
    @Transactional(readOnly = true)
    public List<BorrowerDTO> getAllBorrowers() {
        return borrowerRepository.findByIsActiveTrue()
                .stream()
                .map(BorrowerDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public Optional<BorrowerDTO> getBorrowerById(UUID id) {
        return borrowerRepository.findByIdAndIsActiveTrue(id)
                .map(BorrowerDTO::fromEntity);
    }
    
    @Transactional(readOnly = true)
    public List<BorrowerDTO> getBorrowersWithOverdueBooks() {
        return borrowerRepository.findBorrowersWithOverdueBooks()
                .stream()
                .map(BorrowerDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<BorrowerDTO> searchBorrowers(String searchTerm) {
        return borrowerRepository.searchBorrowers(searchTerm)
                .stream()
                .map(BorrowerDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public BorrowerDTO updateBorrower(UUID id, BorrowerRequestDTO request) {
        try {
            log.info("Updating borrower with ID: {}", id);
            Borrower borrower = borrowerRepository.findByIdAndIsActiveTrue(id)
                    .orElseThrow(() -> {
                        log.error("Borrower not found with id: {}", id);
                        return new RuntimeException("Borrower not found with id: " + id);
                    });
            
            // Check if email is being changed and if new email already exists
            if (!borrower.getEmail().equals(request.getEmail()) && 
                borrowerRepository.existsByEmailAndIsActiveTrue(request.getEmail())) {
                log.warn("Borrower with email {} already exists", request.getEmail());
                throw new RuntimeException("Borrower with email " + request.getEmail() + " already exists");
            }
            
            borrower.setName(request.getName());
            borrower.setEmail(request.getEmail());
            borrower.setMembershipType(request.getMembershipType());
            
            Borrower savedBorrower = borrowerRepository.save(borrower);
            log.info("Successfully updated borrower with ID: {}", savedBorrower.getId());
            return BorrowerDTO.fromEntity(savedBorrower);
        } catch (Exception e) {
            log.error("Error updating borrower with ID: {}, error={}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to update borrower: " + e.getMessage(), e);
        }
    }
    
    public void deactivateBorrower(UUID id) {
        try {
            log.info("Deactivating borrower with ID: {}", id);
            Borrower borrower = borrowerRepository.findByIdAndIsActiveTrue(id)
                    .orElseThrow(() -> {
                        log.error("Borrower not found with id: {}", id);
                        return new RuntimeException("Borrower not found with id: " + id);
                    });
            
            // Check if borrower has active borrow records
            if (!borrower.getBorrowRecords().stream().allMatch(record -> !record.getIsActive() || record.getReturnDate() != null)) {
                log.warn("Cannot deactivate borrower with active borrow records. Borrower ID: {}", id);
                throw new RuntimeException("Cannot deactivate borrower with active borrow records");
            }
            
            borrower.setIsActive(false);
            borrowerRepository.save(borrower);
            log.info("Successfully deactivated borrower with ID: {}", id);
        } catch (Exception e) {
            log.error("Error deactivating borrower with ID: {}, error={}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to deactivate borrower: " + e.getMessage(), e);
        }
    }
}
