package com.geekyAnts.libraryManagement.entity;

import com.geekyAnts.libraryManagement.enums.MembershipType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "borrowers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Borrower {
    
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;
    
    @NotBlank(message = "Name is required")
    @Column(nullable = false)
    private String name;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Column(nullable = false, unique = true)
    private String email;
    
    @NotNull(message = "Membership type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MembershipType membershipType;
    
    @Column(nullable = false)
    private Integer maxBorrowLimit;
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    @OneToMany(mappedBy = "borrower", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BorrowRecord> borrowRecords = new ArrayList<>();
    
    @PrePersist
    @PreUpdate
    public void setMaxBorrowLimit() {
        if (this.maxBorrowLimit == null) {
            this.maxBorrowLimit = this.membershipType.getMaxBorrowLimit();
        }
    }
}
