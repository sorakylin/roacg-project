package com.roacg.service.tc.project.model.po;

import lombok.Data;

import javax.persistence.*;

/**
 * 项目用户表
 * 即项目 和 用户的关联表, 表示了该项目有哪些人参与
 */
@Data
@Entity
@Table(name = "tb_project_user")
public class ProjectUserPO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, columnDefinition = "bigint(20)")
    private Long id;

    @Column(nullable = false, columnDefinition = "bigint(20)")
    private Long projectId;

    @Column(nullable = false, columnDefinition = "bigint(20)")
    private Long userId;


    public static ProjectUserPO newInstance(Long projectId, Long userId) {

        ProjectUserPO fragment = new ProjectUserPO();
        fragment.setProjectId(projectId);
        fragment.setUserId(userId);
        return fragment;
    }
}