package com.geekyAnts.libraryManagement.repository;

import com.geekyAnts.libraryManagement.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookRepository extends JpaRepository<Book, UUID> {
    
    Page<Book> findByIsDeletedFalse(Pageable pageable);
    
    List<Book> findByCategoryAndIsDeletedFalse(String category);
    
    @Query("SELECT b FROM Book b WHERE b.isDeleted = false AND " +
           "(:category IS NULL OR b.category = :category) AND " +
           "(:available IS NULL OR b.isAvailable = :available)")
    Page<Book> findBooksWithFilters(@Param("category") String category, 
                                   @Param("available") Boolean available, 
                                   Pageable pageable);
    
    @Query("SELECT b FROM Book b WHERE b.isDeleted = false AND " +
           "(:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
           "(:author IS NULL OR LOWER(b.author) LIKE LOWER(CONCAT('%', :author, '%')))")
    Page<Book> searchBooks(@Param("title") String title, 
                          @Param("author") String author, 
                          Pageable pageable);
    
    Optional<Book> findByIdAndIsDeletedFalse(UUID id);
    
    @Query("SELECT b FROM Book b WHERE b.isDeleted = false AND b.title = :title AND b.author = :author")
    Optional<Book> findByTitleAndAuthor(@Param("title") String title, @Param("author") String author);
    
    @Query("SELECT b FROM Book b WHERE b.isDeleted = false AND b.availableCopies > 0")
    List<Book> findAvailableBooks();
    
    @Query("SELECT b.category, COUNT(b) as total, SUM(b.availableCopies) as available " +
           "FROM Book b WHERE b.isDeleted = false GROUP BY b.category")
    List<Object[]> getAvailabilitySummary();
}
