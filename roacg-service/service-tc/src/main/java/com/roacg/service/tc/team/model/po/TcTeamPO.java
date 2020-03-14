package com.roacg.service.tc.team.model.po;

import com.roacg.core.model.db.BaseEntity;

import javax.persistence.*;

@Table(name = "tb_tc_team")
@Entity
public class TcTeamPO extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long teamId;

    @Column
    private String teamName;

    //组长ID
    @Column(columnDefinition = "bigint(20)")
    private Long teamLeaderId;

    //团队介绍
    @Column(columnDefinition = "varchar(512)")
    private String teamIntroduction;

    //团队等级,根据等级 相应的权限也有所不同
    @Column(columnDefinition = "tinyint(3)")
    private Integer teamGrade;

    //团队当前人数
    @Column
    private Integer teamSize;

    //团队可创建的项目数量上限
    @Column
    private Integer projectCap;

    //团队已有的项目数量
    @Column
    private Integer existingProjectQuantity;

}
