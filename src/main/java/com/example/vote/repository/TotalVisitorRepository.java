package com.example.vote.repository;

import com.example.vote.entity.TotalVisitor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TotalVisitorRepository extends JpaRepository<TotalVisitor, Long> {
    Optional<TotalVisitor> findByPageName(String pageName);
}