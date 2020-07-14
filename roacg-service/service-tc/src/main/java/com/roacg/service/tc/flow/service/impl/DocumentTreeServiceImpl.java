package com.roacg.service.tc.flow.service.impl;

import com.roacg.core.model.exception.ParameterValidationException;
import com.roacg.service.tc.flow.model.po.DocumentTreePO;
import com.roacg.service.tc.flow.repository.DocumentTreeRepository;
import com.roacg.service.tc.flow.service.DocumentTreeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DocumentTreeServiceImpl implements DocumentTreeService {

    @Resource
    private DocumentTreeRepository documentTreeRepository;

    @Override
    @Transactional
    public void createNode(Long parentId, Long nodeId) {
        if (Objects.isNull(parentId)) {
            throw new ParameterValidationException("parentId 不能为空");
        }
        if (Objects.isNull(nodeId)) {
            throw new ParameterValidationException("nodeId 不能为空");
        }

        //关系已存在，跳过
        if (findChildNodeId(parentId).contains(nodeId)) return;

        DocumentTreePO parentNode = new DocumentTreePO();
        parentNode.setDistance(1);
        parentNode.setAncestor(parentId);
        parentNode.setDescendant(nodeId);

        //从根节点到父节点的链表, 不包含父节点
        List<DocumentTreePO> relationNodeList = documentTreeRepository.findByDescendant(parentId);
        if (relationNodeList.isEmpty()) {
            documentTreeRepository.save(parentNode);
            return;
        }

        List<DocumentTreePO> saveList = relationNodeList.stream().map(node -> {
            DocumentTreePO relationNode = new DocumentTreePO();
            relationNode.setDescendant(nodeId);
            relationNode.setAncestor(node.getAncestor());
            relationNode.setDistance(node.getDistance() + 1);
            return relationNode;
        }).collect(Collectors.toList());

        saveList.add(parentNode);
        List<DocumentTreePO> result = documentTreeRepository.saveAll(saveList);
    }

    @Override
    @Transactional
    public void deleteNode(Long nodeId) {
        documentTreeRepository.deleteSubTree(nodeId);
        documentTreeRepository.deleteById(nodeId);
    }

    @Override
    public Set<Long> findChildNodeId(Long nodeId) {
        return documentTreeRepository.findChildNodeId(nodeId);
    }

    @Override
    public Queue<Long> findNodeAllParentId(Long nodeId) {
        Queue<Long> result = documentTreeRepository.findByDescendant(nodeId).stream()
                .sorted(Comparator.comparing(DocumentTreePO::getDistance).reversed())
                .map(DocumentTreePO::getAncestor)
                .collect(Collectors.toCollection(LinkedList::new));

        return result;
    }
}
