package com.geekyAnts.libraryManagement.repository;

import com.geekyAnts.libraryManagement.entity.FinePolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FinePolicyRepository extends JpaRepository<FinePolicy, UUID> {
    
    List<FinePolicy> findByIsActiveTrue();
    
    Optional<FinePolicy> findByIdAndIsActiveTrue(UUID id);
    
    Optional<FinePolicy> findByCategoryAndIsActiveTrue(String category);
    
    boolean existsByCategoryAndIsActiveTrue(String category);
}
