package com.geekyAnts.libraryManagement.repository;

import com.geekyAnts.libraryManagement.entity.Borrower;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BorrowerRepository extends JpaRepository<Borrower, UUID> {
    
    List<Borrower> findByIsActiveTrue();
    
    Optional<Borrower> findByIdAndIsActiveTrue(UUID id);
    
    Optional<Borrower> findByEmailAndIsActiveTrue(String email);
    
    boolean existsByEmailAndIsActiveTrue(String email);
    
    @Query("SELECT b FROM Borrower b WHERE b.isActive = true AND " +
           "b.id IN (SELECT br.borrower.id FROM BorrowRecord br WHERE " +
           "br.isActive = true AND br.returnDate IS NULL AND br.dueDate < CURRENT_DATE)")
    List<Borrower> findBorrowersWithOverdueBooks();
    
    @Query("SELECT b FROM Borrower b WHERE b.isActive = true AND " +
           "(LOWER(b.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(b.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Borrower> searchBorrowers(@Param("searchTerm") String searchTerm);
}
