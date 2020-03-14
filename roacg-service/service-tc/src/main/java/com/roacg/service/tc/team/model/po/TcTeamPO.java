package com.roacg.service.tc.team.model.po;

import com.roacg.core.model.db.BaseEntity;

import javax.persistence.*;

/**
 * 团队表
 * 表示一个翻译协同组的团队基本信息
 */
@Table(name = "tb_tc_team")
@Entity
public class TcTeamPO extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long teamId;

    @Column(nullable = false)
    private String teamName;

    //团队介绍
    @Column(columnDefinition = "varchar(512)")
    private String teamIntroduction;

    //团队等级,根据等级 相应的权限也有所不同
    @Column(columnDefinition = "tinyint(3)", nullable = false)
    private Integer teamGrade;

    //团队当前人数
    @Column(nullable = false)
    private Integer teamSize;

    //团队可创建的项目数量上限
    @Column(nullable = false)
    private Integer projectCap;

    //团队已有的项目数量
    @Column(nullable = false)
    private Integer existingProjectQuantity;

}
