package com.roacg.service.tc.flow.model.po;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 版本控制记录表
 */
@Data
public class DocumentVersionListPO {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "bigint", nullable = false)
    private Long id;

    //对应的文档ID
    @Column(columnDefinition = "bigint", nullable = false)
    private Long documentId;

    //文档版本, 0=初版; 只会一直向后递增
    @Column(columnDefinition = "int", nullable = false)
    private Integer documentVersion;

    //文档内容
    @Column(columnDefinition = "varchar(20000)", length = 20000)
    private String content;

    //可能指向一个附件表 or 其他的内容表等
    @Column(columnDefinition = "bigint")
    private Long contentFkId;

}
