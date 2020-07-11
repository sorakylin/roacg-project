package com.roacg.service.tc.flow.service;

import java.util.Set;

public interface DocumentTreeService {


    void createNode(Long parentId, Long nodeId);

    void deleteNode(Long nodeId);

    Set<Long> findChildNodeId(Long nodeId);
}
