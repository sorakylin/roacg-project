package com.roacg.service.tc.project.model.po;

import lombok.Data;

import javax.persistence.*;

/**
 * 项目用户表
 * 即项目 和 用户的关联表, 表示了该项目有哪些人参与
 */
@Data
@Entity
@Table(name = "tb_tc_project_user")
public class ProjectUserPO {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, columnDefinition = "bigint(20)")
    private Long id;

    @Column(nullable = false, columnDefinition = "bigint(20)")
    private Long projectId;

    @Column(nullable = false, columnDefinition = "bigint(20)")
    private Long userId;
}