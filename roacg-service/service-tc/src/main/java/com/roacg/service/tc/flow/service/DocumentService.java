package com.roacg.service.tc.flow.service;

import com.roacg.service.tc.flow.model.req.CreateDirREQ;
import com.roacg.service.tc.flow.model.req.CreateTextDocumentREQ;

public interface DocumentService {

    //创建目录
    void createDir(CreateDirREQ createDirREQ);

    /**
     * 创建文本翻译文档
     *
     * @param createDocumentREQ 创建文档
     */
    void createTranslateDocumentWithText(CreateTextDocumentREQ createDocumentREQ);
}
