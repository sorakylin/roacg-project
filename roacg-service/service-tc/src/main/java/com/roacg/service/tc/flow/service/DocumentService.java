package com.roacg.service.tc.flow.service;

import com.roacg.service.tc.flow.model.req.CreateDirREQ;
import com.roacg.service.tc.flow.model.req.CreateTextDocumentREQ;
import com.roacg.service.tc.flow.model.vo.DocInfoVO;

import java.util.List;

public interface DocumentService {

    //创建目录
    void createDir(CreateDirREQ createDirREQ);

    /**
     * 创建文本翻译文档
     *
     * @param createDocumentREQ 创建文档
     */
    void createTranslateDocumentWithText(CreateTextDocumentREQ createDocumentREQ);

    /**
     * 查询一个文档节点下的子节点
     *
     * @param nodeId 文档ID or 项目ID; 项目ID为根目录
     * @return 文档列表
     */
    List<DocInfoVO> findChildDocument(Long nodeId);
}
