package com.entrepreneur.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 项目团队成员表
 */
@Data
@TableName("project_team_member")
public class ProjectTeamMember {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long projectId;
    private String name;
    private String role;
    private String avatar;
    private String bio;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableLogic
    private Integer deleted;
}
