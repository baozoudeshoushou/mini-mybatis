package com.lin.mybatis.mapper;

import com.lin.mybatis.entity.SysUser;

import java.util.List;

/**
 * @Author linjiayi5
 * @Date 2023/5/6 14:19:39
 */
public interface SysUserMapper {

    List<SysUser> findAll();

}
