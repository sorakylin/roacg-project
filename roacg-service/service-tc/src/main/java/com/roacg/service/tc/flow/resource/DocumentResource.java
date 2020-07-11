package com.roacg.service.tc.flow.resource;

import com.roacg.core.model.resource.RoApiResponse;
import com.roacg.core.web.security.annotation.ExposeResource;
import com.roacg.service.tc.flow.model.req.CreateDirREQ;
import com.roacg.service.tc.flow.model.req.CreateTextDocumentREQ;
import com.roacg.service.tc.flow.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 文档相关的资源
 */
@RestController
@RequestMapping("/document")
@ExposeResource
public class DocumentResource {

    @Autowired
    private DocumentService documentService;


    @PostMapping("/dir")
    public RoApiResponse createDir(@RequestBody CreateDirREQ req) {
        documentService.createDir(req);
        return RoApiResponse.ok();
    }

    @PostMapping("/file/text")
    public RoApiResponse createTextDocument(@RequestBody CreateTextDocumentREQ req) {
        documentService.createTranslateDocumentWithText(req);
        return RoApiResponse.ok();
    }

}
