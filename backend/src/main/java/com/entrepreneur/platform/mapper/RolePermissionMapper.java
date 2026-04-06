package com.entrepreneur.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.entrepreneur.platform.entity.RolePermission;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 角色权限关联表Mapper
 */
public interface RolePermissionMapper extends BaseMapper<RolePermission> {
    
    /**
     * 根据角色代码查询权限代码列表
     */
    @Select("SELECT permission_code FROM role_permission WHERE role_code = #{roleCode}")
    List<String> selectPermissionCodesByRoleCode(String roleCode);
    
    /**
     * 根据权限代码查询角色代码列表
     */
    @Select("SELECT role_code FROM role_permission WHERE permission_code = #{permissionCode}")
    List<String> selectRoleCodesByPermissionCode(String permissionCode);
}