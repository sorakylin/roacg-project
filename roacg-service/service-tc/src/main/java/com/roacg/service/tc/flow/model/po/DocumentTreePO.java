package com.roacg.service.tc.flow.model.po;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 闭包表
 */
@Data
public class DocumentTreePO {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "bigint", nullable = false)
    private Long id;
    @Column(columnDefinition = "bigint", nullable = false)
    private Long ancestor;//祖先节点
    @Column(columnDefinition = "bigint", nullable = false)
    private Long descendant;//后代节点
    @Column(columnDefinition = "tinyint", nullable = false)
    private Integer distance;//相隔层级，>=1
}
