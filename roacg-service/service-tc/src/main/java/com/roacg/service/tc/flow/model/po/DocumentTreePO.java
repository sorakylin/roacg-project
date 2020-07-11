package com.roacg.service.tc.flow.model.po;

import com.roacg.service.tc.project.model.po.ProjectPO;
import lombok.Data;

import javax.persistence.*;

/**
 * 闭包表
 */
@Data
@Table(name = "tc_document_tree")
@Entity
public class DocumentTreePO {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "bigint", nullable = false)
    private Long id;

    /**
     * 祖先节点, 如果指向根节点 则为 projectId {@link ProjectPO#getProjectId()}
     */
    @Column(columnDefinition = "bigint", nullable = false)
    private Long ancestor;

    //后代节点
    @Column(columnDefinition = "bigint", nullable = false)
    private Long descendant;

    //相隔层级，>=1
    @Column(columnDefinition = "tinyint", nullable = false)
    private Integer distance;
}
