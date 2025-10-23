package com.geekyAnts.libraryManagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "borrow_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowRecord {
    
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;
    
    @NotNull(message = "Book is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
    
    @NotNull(message = "Borrower is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "borrower_id", nullable = false)
    private Borrower borrower;
    
    @NotNull(message = "Borrow date is required")
    @Column(nullable = false)
    private LocalDate borrowDate;
    
    @NotNull(message = "Due date is required")
    @Column(nullable = false)
    private LocalDate dueDate;
    
    private LocalDate returnDate;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal fineAmount = BigDecimal.ZERO;
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @PrePersist
    public void setBorrowDate() {
        if (this.borrowDate == null) {
            this.borrowDate = LocalDate.now();
        }
        if (this.dueDate == null) {
            this.dueDate = this.borrowDate.plusDays(14);
        }
    }
    
    public boolean isOverdue() {
        return this.returnDate == null && LocalDate.now().isAfter(this.dueDate);
    }
    
    public long getDaysOverdue() {
        if (this.returnDate != null) {
            return Math.max(0, this.returnDate.toEpochDay() - this.dueDate.toEpochDay());
        }
        return Math.max(0, LocalDate.now().toEpochDay() - this.dueDate.toEpochDay());
    }
}
