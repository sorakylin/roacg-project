package com.roacg.service.tc.flow.service;

import com.roacg.service.tc.flow.model.dto.ContributorDTO;
import com.roacg.service.tc.flow.model.po.DocumentWorkflowPO;

import java.util.List;

public interface DocumentWorkflowService {

    List<DocumentWorkflowPO> generateDocumentWorkflow(Long documentId, List<ContributorDTO> contributors);
}
