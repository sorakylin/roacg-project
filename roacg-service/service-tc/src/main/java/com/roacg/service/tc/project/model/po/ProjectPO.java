package com.roacg.service.tc.project.model.po;


import com.roacg.core.model.db.support.VirtualDeleteSupportEntity;
import lombok.Data;

import javax.persistence.*;

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

    //团队ID  项目->团队 多对一
    @Column(nullable = false, columnDefinition = "bigint(20)")
    private Long teamId;

    //项目建立人 userId
    @Column(nullable = false, columnDefinition = "bigint(20)")
    private Long founderId;

    //项目审核人 userId
    @Column(nullable = false, columnDefinition = "bigint(20)")
    private Long reviewerId;

    //项目等级,保留字段
    @Column(columnDefinition = "tinyint(1)", nullable = false)
    private Integer projectGrade;

    //当前项目的任务最大数量限制
    @Column(nullable = false)
    private Integer taskCap;

}
