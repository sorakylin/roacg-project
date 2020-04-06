package com.roacg.service.tc.team.model.po;

import com.roacg.service.tc.team.enums.UserTeamRoleEnum;
import lombok.Data;

import javax.persistence.*;

/**
 * 团队用户表
 * 即团队 和 用户的关联表, 其中带有一个用户在此组中的角色信息
 * <p>
 * TODO 这张表可以用 MyISAM
 */
@Data
@Entity
@Table(name = "tb_tc_team_user")
public class TeamUserPO {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, columnDefinition = "bigint(20)")
    private Long id;

    /**
     * 小组ID
     *
     * @see TeamPO#getTeamId()
     */
    @Column(nullable = false, columnDefinition = "bigint(20)")
    private Long teamId;

    @Column(nullable = false, columnDefinition = "bigint(20)")
    private Long userId;

    @Convert(converter = UserTeamRoleEnum.Convert.class)
    @Column(nullable = false, columnDefinition = "tinyint(1)")
    private UserTeamRoleEnum userTeamRole;
}
