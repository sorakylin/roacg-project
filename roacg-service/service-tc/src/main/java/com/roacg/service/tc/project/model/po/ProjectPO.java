package com.roacg.service.tc.project.model.po;


import com.roacg.core.model.db.support.VirtualDeleteSupportEntity;
import com.roacg.service.tc.project.enums.ProjectPermissionStatusEnum;
import com.roacg.service.tc.project.enums.ProjectStatusEnum;
import com.roacg.service.tc.project.enums.ProjectTypeEnum;
import lombok.Data;

import javax.persistence.*;


/**
 * 团队项目/社区项目
 */
@Data
@Table(name = "tb_tc_project")
@Entity
public class ProjectPO extends VirtualDeleteSupportEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, columnDefinition = "bigint(20)")
    private Long projectId;

    @Column(nullable = false, columnDefinition = "varchar(48)")
    private String projectName;

    //项目简介
    @Column(columnDefinition = "varchar(128)")
    private String projectProfile;

    //从哪里来
    @Column(columnDefinition = "varchar(20)")
    private String fromLanguage;

    //到哪里去
    @Column(columnDefinition = "varchar(20)")
    private String toLanguage;

    //项目的状态
    @Convert(converter = ProjectStatusEnum.Convert.class)
    @Column(nullable = false, columnDefinition = "tinyint(1)")
    private ProjectStatusEnum projectStatus;

    //项目类型
    @Convert(converter = ProjectTypeEnum.Convert.class)
    @Column(nullable = false, columnDefinition = "tinyint(1)")
    private ProjectTypeEnum projectType;

    //项目的许可范围, 标识了该项目可以被谁访问
    @Convert(converter = ProjectTypeEnum.Convert.class)
    @Column(nullable = false, columnDefinition = "tinyint(1)")
    private ProjectPermissionStatusEnum projectPermissionStatus;

    //团队ID 项目->团队 多对一 TODO 不一定要有
    @Column(columnDefinition = "bigint(20)")
    private Long teamId;

    //项目建立人 userId
    @Column(nullable = false, columnDefinition = "bigint(20)")
    private Long founderId;

    //项目审核人 userId      TODO 审核人用来干啥的来着？忘了
    @Column(columnDefinition = "bigint(20)")
    private Long reviewerId;

    //项目等级,保留字段
    @Column(columnDefinition = "tinyint(1)")
    private Integer projectGrade;

}
