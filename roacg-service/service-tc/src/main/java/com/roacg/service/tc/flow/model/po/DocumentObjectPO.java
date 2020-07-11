package com.roacg.service.tc.flow.model.po;

import com.roacg.core.model.db.BasicCompleteEntity;
import com.roacg.service.tc.common.SnowflakeIdGenerator;
import com.roacg.service.tc.flow.model.enums.ContentTypeEnum;
import com.roacg.service.tc.flow.model.enums.DocumentStateEnum;
import com.roacg.service.tc.flow.model.enums.DocumentTypeEnum;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * 文档逻辑对象表
 */
@Data
@Table(name = "tc_document_object")
@Entity
public class DocumentObjectPO extends BasicCompleteEntity {

    @Id
    @GenericGenerator(name = SnowflakeIdGenerator.NAME, strategy = SnowflakeIdGenerator.CLASS_NAME)
    @GeneratedValue(generator = SnowflakeIdGenerator.NAME)
    @Column(columnDefinition = "bigint", nullable = false)
    private Long documentId;

    //文档类型, 文件夹 or 文件
    @Column(columnDefinition = "tinyint", nullable = false)
    @Convert(converter = DocumentTypeEnum.Convert.class)
    private DocumentTypeEnum documentType;

    //内容类型, 文本? 链接? 附件?
    @Column(columnDefinition = "tinyint", nullable = false)
    @Convert(converter = ContentTypeEnum.Convert.class)
    private ContentTypeEnum contentType;

    //文档状态 未开始/进行中/待审校/已完成
    @Column(columnDefinition = "tinyint", nullable = false)
    @Convert(converter = DocumentStateEnum.Convert.class)
    private DocumentStateEnum documentState;

    //文件数 / 字符数?
    @Column(columnDefinition = "int", nullable = false)
    private Integer size;

    //工作流指针, 指示当前文档处于哪个节点。 每交付一次指针移动一步
    @Column(columnDefinition = "bigint", nullable = false)
    private Integer workflowNodePointer;

    //工作流头节点
    @Column(columnDefinition = "bigint", nullable = false)
    private Long workflowHeadNode;

    //工作流尾节点
    @Column(columnDefinition = "bigint", nullable = false)
    private Long workflowTailNode;

    //最终审核人(用户ID) TODO 不需要审核人, 统一待审页面
    @Column(columnDefinition = "bigint")
    private Long reviewerId;
}
