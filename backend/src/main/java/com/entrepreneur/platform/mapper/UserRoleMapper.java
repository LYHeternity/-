package com.entrepreneur.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.entrepreneur.platform.entity.UserRole;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户角色关联表Mapper
 */
public interface UserRoleMapper extends BaseMapper<UserRole> {
    
    /**
     * 根据用户ID查询角色代码列表
     */
    @Select("SELECT role_code FROM user_role WHERE user_id = #{userId}")
    List<String> selectRoleCodesByUserId(Long userId);
    
    /**
     * 根据角色代码查询用户ID列表
     */
    @Select("SELECT user_id FROM user_role WHERE role_code = #{roleCode}")
    List<Long> selectUserIdsByRoleCode(String roleCode);
}