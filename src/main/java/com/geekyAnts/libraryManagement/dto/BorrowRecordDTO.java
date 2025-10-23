package com.geekyAnts.libraryManagement.dto;

import com.geekyAnts.libraryManagement.entity.BorrowRecord;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowRecordDTO {
    private UUID id;
    private UUID bookId;
    private String bookTitle;
    private String bookAuthor;
    private UUID borrowerId;
    private String borrowerName;
    private String borrowerEmail;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private BigDecimal fineAmount;
    private Boolean isActive;
    private Boolean isOverdue;
    private Long daysOverdue;
    
    public static BorrowRecordDTO fromEntity(BorrowRecord record) {
        BorrowRecordDTO dto = new BorrowRecordDTO();
        dto.setId(record.getId());
        dto.setBookId(record.getBook().getId());
        dto.setBookTitle(record.getBook().getTitle());
        dto.setBookAuthor(record.getBook().getAuthor());
        dto.setBorrowerId(record.getBorrower().getId());
        dto.setBorrowerName(record.getBorrower().getName());
        dto.setBorrowerEmail(record.getBorrower().getEmail());
        dto.setBorrowDate(record.getBorrowDate());
        dto.setDueDate(record.getDueDate());
        dto.setReturnDate(record.getReturnDate());
        dto.setFineAmount(record.getFineAmount());
        dto.setIsActive(record.getIsActive());
        dto.setIsOverdue(record.isOverdue());
        dto.setDaysOverdue(record.getDaysOverdue());
        return dto;
    }
}
