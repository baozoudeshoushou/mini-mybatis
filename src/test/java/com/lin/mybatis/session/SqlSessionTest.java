package com.lin.mybatis.session;

import com.lin.mybatis.entity.SysUser;
import com.lin.mybatis.io.Resources;
import com.lin.mybatis.mapper.SysUserMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.util.List;

/**
 * @author linjiayi5
 * @date 2023/11/14
 */
public class SqlSessionTest {

    private static SqlSessionFactory sqlSessionFactory;

    @BeforeAll
    static void setUp() throws Exception {
        // create an SqlSessionFactory
        try (Reader reader = Resources.getResourceAsReader("mybatis-config.xml")) {
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        }
    }

    @Test
    void shouldOpenAndClose() {
        SqlSession session = sqlSessionFactory.openSession(TransactionIsolationLevel.SERIALIZABLE);
        session.close();
    }

    @Test
    void shouldSelectAllSysUser() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        List<SysUser> list = sqlSession.selectList("com.lin.mybatis.mapper.SysUserMapper.findAll");
        System.out.println(list);
        sqlSession.close();
    }

    @Test
    void shouldSelectOneSysUserWithId() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        SysUser sysUser = sqlSession.selectOne("com.lin.mybatis.mapper.SysUserMapper.queryUserInfoById", 3L);
        Assertions.assertEquals(3L, sysUser.getId());
        System.out.println(sysUser);
        sqlSession.close();
    }

    @Test
    void shouldGetSysUserMapperAndFindAll() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        SysUserMapper sysUserMapper = sqlSession.getMapper(SysUserMapper.class);
        List<SysUser> sysUserList = sysUserMapper.findAll();
        System.out.println(sysUserList);
    }

}
