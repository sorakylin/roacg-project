package com.roacg.service.tc.flow.repository;

import com.roacg.service.tc.flow.model.po.DocumentTreePO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface DocumentTreeRepository extends JpaRepository<DocumentTreePO, Long> {

    List<DocumentTreePO> findByDescendant(Long descendant);

    @Modifying
    @Query("DELETE FROM DocumentTreePO WHERE ancestor=:nodeId")
    int deleteSubTree(@Param("nodeId") Long nodeId);

    @Query("SELECT descendant FROM DocumentTreePO WHERE ancestor=:nodeId AND distance=1")
    Set<Long> findChildNodeId(@Param("nodeId") Long nodeId);
}
