package com.geekyAnts.libraryManagement.dto;

import com.geekyAnts.libraryManagement.entity.Borrower;
import com.geekyAnts.libraryManagement.enums.MembershipType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowerDTO {
    private UUID id;
    private String name;
    private String email;
    private MembershipType membershipType;
    private Integer maxBorrowLimit;
    private Boolean isActive;
    
    public static BorrowerDTO fromEntity(Borrower borrower) {
        return new BorrowerDTO(
            borrower.getId(),
            borrower.getName(),
            borrower.getEmail(),
            borrower.getMembershipType(),
            borrower.getMaxBorrowLimit(),
            borrower.getIsActive()
        );
    }
}
