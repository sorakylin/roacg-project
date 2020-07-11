package com.roacg.service.tc.flow.repository;

import com.roacg.service.tc.flow.model.po.DocumentWorkflowPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentWorkflowRepository extends JpaRepository<DocumentWorkflowPO, Long> {
}
