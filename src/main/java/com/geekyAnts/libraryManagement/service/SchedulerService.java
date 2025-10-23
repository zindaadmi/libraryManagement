package com.geekyAnts.libraryManagement.service;

import com.geekyAnts.libraryManagement.entity.BorrowRecord;
import com.geekyAnts.libraryManagement.repository.BorrowRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchedulerService {

    private final BorrowRecordRepository borrowRecordRepository;

    /**
     * Runs daily at midnight to flag overdue records
     */
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void flagOverdueRecords() {
        log.info("Starting daily overdue records check...");
        
        try {
            LocalDate today = LocalDate.now();
            List<BorrowRecord> overdueRecords = borrowRecordRepository.findOverdueRecords(today);
            
            if (overdueRecords.isEmpty()) {
                log.info("No overdue records found");
                return;
            }
            
            log.info("Found {} overdue records", overdueRecords.size());
            
            for (BorrowRecord record : overdueRecords) {
                log.info("Flagging overdue record: Book ID {}, Borrower ID {}, Due Date: {}", 
                    record.getBook().getId(), record.getBorrower().getId(), record.getDueDate());
            }
            
            log.info("Successfully processed {} overdue records", overdueRecords.size());
            
        } catch (Exception e) {
            log.error("Error processing overdue records: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Manual method to check overdue records (for testing)
     */
    @Transactional
    public List<BorrowRecord> getOverdueRecords() {
        LocalDate today = LocalDate.now();
        return borrowRecordRepository.findOverdueRecords(today);
    }
}
