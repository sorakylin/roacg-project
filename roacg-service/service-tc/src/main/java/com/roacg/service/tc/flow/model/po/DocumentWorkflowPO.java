package com.roacg.service.tc.flow.model.po;

import com.roacg.core.model.db.BaseEntity;
import lombok.Data;

import javax.persistence.*;

/**
 * 文档工作流
 * 一行数据为一个节点, 一个工作流即一个双向链表
 */
@Data
@Table(name = "tc_document_workflow")
@Entity
public class DocumentWorkflowPO extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "bigint", nullable = false)
    private Long id;

    //工作者(用户ID)
    @Column(columnDefinition = "bigint", nullable = false)
    private Long workerId;

    //文档ID
    @Column(columnDefinition = "bigint", nullable = false)
    private Long documentId;

    //文档版本ID, 和文档版本为一对一关系，交付状态为未开始时为空。
    @Column(columnDefinition = "bigint")
    private Long documentVersionId;

    //工作者留下的备注
    @Column(columnDefinition = "varchar(500)")
    private String remark;

    //交付标签 未开始/未交付/已交付
    @Column(columnDefinition = "tinyint", nullable = false)
    private Integer deliveryTag;

    //上一个节点, 为头节点时为空
    @Column(columnDefinition = "bigint")
    private Long prevNode;

    //下一个节点, 尾节点时为空
    @Column(columnDefinition = "bigint")
    private Long nextNode;
}
