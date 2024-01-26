package com.lin.mybatis.mapper;

import com.lin.mybatis.annotations.Param;
import com.lin.mybatis.annotations.ResultMap;
import com.lin.mybatis.annotations.Select;
import com.lin.mybatis.entity.SysUser;
import com.lin.mybatis.entity.TheSysUser;

import java.util.List;

/**
 * @Author linjiayi5
 * @Date 2023/5/6 14:19:39
 */
public interface SysUserMapper {

    List<SysUser> findAll();

    List<SysUser> queryUserInfoById(SysUser user);

    int insertSysUser(SysUser user);

    int updateUserName(@Param("name") String name, @Param("id") Long id);

    int deleteUserById(Long id);

    @Select("Select * FROM sys_user WHERE id = #{id}")
    SysUser selectUserById(Long id);

    @Select("Select * FROM sys_user WHERE id = #{id}")
    @ResultMap({"theSysUserMap"})
    TheSysUser selectTheUserById(Long id);

}
