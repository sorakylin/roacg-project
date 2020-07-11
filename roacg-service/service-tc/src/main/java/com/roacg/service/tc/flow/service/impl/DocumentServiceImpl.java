package com.roacg.service.tc.flow.service.impl;

import com.roacg.core.utils.bean.BeanMapper;
import com.roacg.service.tc.flow.model.enums.ContentTypeEnum;
import com.roacg.service.tc.flow.model.enums.DocumentStateEnum;
import com.roacg.service.tc.flow.model.enums.DocumentTypeEnum;
import com.roacg.service.tc.flow.model.po.DocumentObjectPO;
import com.roacg.service.tc.flow.model.po.DocumentVersionPO;
import com.roacg.service.tc.flow.model.po.DocumentWorkflowPO;
import com.roacg.service.tc.flow.model.req.CreateDirREQ;
import com.roacg.service.tc.flow.model.req.CreateTextDocumentREQ;
import com.roacg.service.tc.flow.model.vo.DocInfoVO;
import com.roacg.service.tc.flow.repository.DocumentObjectRepository;
import com.roacg.service.tc.flow.repository.DocumentVersionRepository;
import com.roacg.service.tc.flow.service.DocumentService;
import com.roacg.service.tc.flow.service.DocumentTreeService;
import com.roacg.service.tc.flow.service.DocumentWorkflowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DocumentServiceImpl implements DocumentService {

    @Resource
    private DocumentObjectRepository documentRepository;
    @Resource
    private DocumentVersionRepository versionRepository;

    @Resource
    private DocumentWorkflowService workflowService;
    @Resource
    private DocumentTreeService treeService;

    @Override
    @Transactional
    public void createDir(CreateDirREQ req) {
        DocumentObjectPO entity = new DocumentObjectPO();

        entity.setDocumentName(req.getDirName());
        entity.setDocumentType(DocumentTypeEnum.DIR);
        entity.setDocumentState(DocumentStateEnum.COMPLETED);
        entity.setContentType(ContentTypeEnum.OTHER);
        entity.setSize(0);
        entity.setWorkflowHeadNode(0L);
        entity.setWorkflowTailNode(0L);
        entity.setWorkflowNodePointer(0L);

        documentRepository.saveAndFlush(entity);
        treeService.createNode(req.getParentId(), entity.getDocumentId());
    }

    @Override
    @Transactional
    public void createTranslateDocumentWithText(CreateTextDocumentREQ req) {
        DocumentObjectPO entity = new DocumentObjectPO();

        entity.setDocumentName(req.getDocumentName());
        entity.setDocumentType(DocumentTypeEnum.FILE);
        entity.setContentType(ContentTypeEnum.TEXT);
        entity.setDocumentState(DocumentStateEnum.NOT_STARTED);
        entity.setSize(req.getOriginTextContent().length());
        entity.setAcceptorId(req.getAcceptorId());

        //下面仨先初始化
        entity.setWorkflowHeadNode(0L);
        entity.setWorkflowTailNode(0L);
        entity.setWorkflowNodePointer(0L);

        documentRepository.saveAndFlush(entity);

        //生成工作流
        Long documentId = entity.getDocumentId();
        List<DocumentWorkflowPO> workflow = workflowService.generateDocumentWorkflow(documentId, req.getContributors());
        entity.setWorkflowHeadNode(workflow.get(0).getId());
        entity.setWorkflowTailNode(workflow.get(workflow.size() - 1).getId());
        entity.setWorkflowNodePointer(workflow.get(0).getId());
        documentRepository.save(entity);

        //初版原文保存
        DocumentVersionPO firstEdition = new DocumentVersionPO();
        firstEdition.setDocumentId(documentId);
        firstEdition.setContent(req.getOriginTextContent());
        firstEdition.setDocumentVersion(0);

        versionRepository.save(firstEdition);
    }

    @Override
    public List<DocInfoVO> findChildDocument(Long nodeId) {
        Set<Long> childNodeId = treeService.findChildNodeId(nodeId);
        if (childNodeId.isEmpty()) return Collections.emptyList();

        return documentRepository.findAllById(childNodeId)
                .stream()
                .map(document -> BeanMapper.map(document, DocInfoVO.class))
                .collect(Collectors.toList());
    }
}
