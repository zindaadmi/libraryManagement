package com.geekyAnts.libraryManagement.service;

import com.geekyAnts.libraryManagement.entity.Book;
import com.geekyAnts.libraryManagement.entity.Borrower;
import com.geekyAnts.libraryManagement.entity.FinePolicy;
import com.geekyAnts.libraryManagement.enums.MembershipType;
import com.geekyAnts.libraryManagement.repository.BookRepository;
import com.geekyAnts.libraryManagement.repository.BorrowerRepository;
import com.geekyAnts.libraryManagement.repository.FinePolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class DataInitializationService implements CommandLineRunner {
    
    private final BookRepository bookRepository;
    private final BorrowerRepository borrowerRepository;
    private final FinePolicyRepository finePolicyRepository;
    
    @Override
    public void run(String... args) throws Exception {
        initializeFinePolicies();
        initializeBooks();
        initializeBorrowers();
    }
    
    private void initializeFinePolicies() {
        if (finePolicyRepository.count() == 0) {
            // Fiction category
            FinePolicy fictionPolicy = new FinePolicy();
            fictionPolicy.setCategory("Fiction");
            fictionPolicy.setFinePerDay(BigDecimal.valueOf(0.50));
            fictionPolicy.setIsActive(true);
            finePolicyRepository.save(fictionPolicy);
            
            // Tech category
            FinePolicy techPolicy = new FinePolicy();
            techPolicy.setCategory("Tech");
            techPolicy.setFinePerDay(BigDecimal.valueOf(1.00));
            techPolicy.setIsActive(true);
            finePolicyRepository.save(techPolicy);
            
            // History category
            FinePolicy historyPolicy = new FinePolicy();
            historyPolicy.setCategory("History");
            historyPolicy.setFinePerDay(BigDecimal.valueOf(0.75));
            historyPolicy.setIsActive(true);
            finePolicyRepository.save(historyPolicy);
        }
    }
    
    private void initializeBooks() {
        if (bookRepository.count() == 0) {
            // Fiction books
            createBook("The Great Gatsby", "F. Scott Fitzgerald", "Fiction", 3);
            createBook("To Kill a Mockingbird", "Harper Lee", "Fiction", 2);
            createBook("1984", "George Orwell", "Fiction", 4);
            createBook("Pride and Prejudice", "Jane Austen", "Fiction", 2);
            
            // Tech books
            createBook("Clean Code", "Robert C. Martin", "Tech", 2);
            createBook("Design Patterns", "Gang of Four", "Tech", 3);
            createBook("Spring Boot in Action", "Craig Walls", "Tech", 2);
            createBook("Java: The Complete Reference", "Herbert Schildt", "Tech", 1);
            
            // History books
            createBook("Sapiens", "Yuval Noah Harari", "History", 2);
            createBook("The Guns of August", "Barbara Tuchman", "History", 1);
            createBook("A People's History", "Howard Zinn", "History", 3);
        }
    }
    
    private void createBook(String title, String author, String category, int totalCopies) {
        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setCategory(category);
        book.setTotalCopies(totalCopies);
        book.setAvailableCopies(totalCopies);
        book.setIsAvailable(totalCopies > 0);
        book.setIsDeleted(false);
        bookRepository.save(book);
    }
    
    private void initializeBorrowers() {
        if (borrowerRepository.count() == 0) {
            // Basic members
            createBorrower("John Doe", "john.doe@email.com", MembershipType.BASIC);
            createBorrower("Jane Smith", "jane.smith@email.com", MembershipType.BASIC);
            createBorrower("Bob Johnson", "bob.johnson@email.com", MembershipType.BASIC);
            
            // Premium members
            createBorrower("Alice Brown", "alice.brown@email.com", MembershipType.PREMIUM);
            createBorrower("Charlie Wilson", "charlie.wilson@email.com", MembershipType.PREMIUM);
        }
    }
    
    private void createBorrower(String name, String email, MembershipType membershipType) {
        Borrower borrower = new Borrower();
        borrower.setName(name);
        borrower.setEmail(email);
        borrower.setMembershipType(membershipType);
        borrower.setIsActive(true);
        borrowerRepository.save(borrower);
    }
}
