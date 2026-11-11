package com.zayna.user.repository;

import com.zayna.user.entity.OrganizerRequest;
import com.zayna.user.enums.RequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizerRequestRepository extends JpaRepository<OrganizerRequest, Long> {

    Page<OrganizerRequest> findByStatus(RequestStatus status, Pageable pageable);

    boolean existsByUserIdAndStatus(Long userId, RequestStatus status);

    Optional<OrganizerRequest> findFirstByUserIdOrderByCreatedAtDesc(Long userId);
}