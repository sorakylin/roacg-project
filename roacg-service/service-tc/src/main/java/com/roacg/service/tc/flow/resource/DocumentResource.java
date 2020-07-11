package com.roacg.service.tc.flow.resource;

import com.roacg.core.model.auth.enmus.PermissionType;
import com.roacg.core.model.resource.RoApiResponse;
import com.roacg.core.web.security.annotation.ExposeResource;
import com.roacg.service.tc.flow.model.req.CreateDirREQ;
import com.roacg.service.tc.flow.model.req.CreateTextDocumentREQ;
import com.roacg.service.tc.flow.model.vo.DocInfoVO;
import com.roacg.service.tc.flow.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 文档相关的资源
 */
@RestController
@RequestMapping("/document")
@ExposeResource
public class DocumentResource {

    @Autowired
    private DocumentService documentService;

    /**
     * 创建文件夹
     */
    @PostMapping("/dir")
    @ExposeResource(type = PermissionType.LOGIN)
    public RoApiResponse createDir(@RequestBody CreateDirREQ req) {
        documentService.createDir(req);
        return RoApiResponse.ok();
    }

    /**
     * 创建翻译文档
     */
    @PostMapping("/file/text")
    @ExposeResource(type = PermissionType.LOGIN)
    public RoApiResponse createTextDocument(@RequestBody CreateTextDocumentREQ req) {
        documentService.createTranslateDocumentWithText(req);
        return RoApiResponse.ok();
    }

    /**
     * 查询一个文档节点下的子节点
     *
     * @param nodeId 文档ID or 项目ID; 项目ID为根目录
     * @return 文档列表
     */
    @GetMapping("/child-node/{nodeId}")
    public RoApiResponse findChildNode(@PathVariable Long nodeId) {
        List<DocInfoVO> result = documentService.findChildDocument(nodeId);
        return RoApiResponse.ok(result);
    }
}
