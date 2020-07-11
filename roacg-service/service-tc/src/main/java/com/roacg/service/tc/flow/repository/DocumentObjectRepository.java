package com.roacg.service.tc.flow.repository;

import com.roacg.service.tc.flow.model.po.DocumentObjectPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentObjectRepository extends JpaRepository<DocumentObjectPO, Long> {
    
}
