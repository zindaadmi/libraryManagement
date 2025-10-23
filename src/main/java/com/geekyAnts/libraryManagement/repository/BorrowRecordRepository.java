package com.geekyAnts.libraryManagement.repository;

import com.geekyAnts.libraryManagement.entity.BorrowRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, UUID> {
    
    List<BorrowRecord> findByIsActiveTrue();
    
    List<BorrowRecord> findByBorrowerIdAndIsActiveTrue(UUID borrowerId);
    
    List<BorrowRecord> findByBookIdAndIsActiveTrue(UUID bookId);
    
    @Query("SELECT br FROM BorrowRecord br WHERE br.isActive = true AND br.returnDate IS NULL")
    List<BorrowRecord> findActiveBorrowRecords();
    
    @Query("SELECT br FROM BorrowRecord br WHERE br.isActive = true AND " +
           "br.returnDate IS NULL AND br.dueDate < CURRENT_DATE")
    List<BorrowRecord> findOverdueBorrowRecords();
    
    @Query("SELECT br FROM BorrowRecord br WHERE br.isActive = true AND " +
           "br.borrower.id = :borrowerId AND br.returnDate IS NULL")
    List<BorrowRecord> findActiveBorrowRecordsByBorrower(@Param("borrowerId") UUID borrowerId);
    
    @Query("SELECT br FROM BorrowRecord br WHERE br.isActive = true AND " +
           "br.book.id = :bookId AND br.returnDate IS NULL")
    Optional<BorrowRecord> findActiveBorrowRecordByBook(@Param("bookId") UUID bookId);
    
    @Query("SELECT br FROM BorrowRecord br WHERE br.isActive = true AND " +
           "br.borrower.id = :borrowerId AND br.book.id = :bookId AND br.returnDate IS NULL")
    Optional<BorrowRecord> findActiveBorrowRecordByBorrowerAndBook(@Param("borrowerId") UUID borrowerId, 
                                                                  @Param("bookId") UUID bookId);
    
    @Query("SELECT br FROM BorrowRecord br WHERE br.isActive = true AND " +
           "br.borrower.id = :borrowerId ORDER BY br.borrowDate DESC")
    List<BorrowRecord> findBorrowHistoryByBorrower(@Param("borrowerId") UUID borrowerId);
    
    @Query("SELECT br.book.id, COUNT(br) as borrowCount FROM BorrowRecord br " +
           "WHERE br.isActive = true GROUP BY br.book.id ORDER BY borrowCount DESC")
    List<Object[]> findTopBorrowedBooks();
    
    @Query("SELECT br.borrower.id, COUNT(br) as totalBorrowed, " +
           "SUM(CASE WHEN br.returnDate IS NULL AND br.dueDate < CURRENT_DATE THEN 1 ELSE 0 END) as overdueCount, " +
           "SUM(br.fineAmount) as totalFines " +
           "FROM BorrowRecord br WHERE br.isActive = true " +
           "GROUP BY br.borrower.id")
    List<Object[]> findBorrowerActivity();
    
    @Query("SELECT COUNT(br) FROM BorrowRecord br WHERE br.isActive = true AND " +
           "br.borrower.id = :borrowerId AND br.returnDate IS NULL")
    long countActiveBorrowRecordsByBorrower(@Param("borrowerId") UUID borrowerId);
    
    @Query("SELECT br FROM BorrowRecord br WHERE br.isActive = true AND " +
           "br.borrowDate >= :startDate AND br.borrowDate <= :endDate")
    List<BorrowRecord> findBorrowRecordsByDateRange(@Param("startDate") LocalDate startDate, 
                                                   @Param("endDate") LocalDate endDate);
    
    @Query("SELECT br FROM BorrowRecord br WHERE br.isActive = true AND " +
           "br.returnDate IS NULL AND br.dueDate < :today")
    List<BorrowRecord> findOverdueRecords(@Param("today") LocalDate today);
}
