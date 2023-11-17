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
    void shouldGetSysUserMapperAndFindAll() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        SysUserMapper sysUserMapper = sqlSession.getMapper(SysUserMapper.class);
        List<SysUser> sysUserList = sysUserMapper.findAll();
        System.out.println(sysUserList);
    }

    @Test
    void shouldSelectOneSysUserWithId() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        Long id = 2030009L;
        SysUser sysUser = sqlSession.selectOne("com.lin.mybatis.mapper.SysUserMapper.queryUserInfoById", id);
        Assertions.assertEquals(id, sysUser.getId());
        System.out.println(sysUser);
        sqlSession.close();
    }

    @Test
    void shouldGetMapperAndSelectOneSysUserWithId() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        Long id = 2030011L;
        SysUserMapper sysUserMapper = sqlSession.getMapper(SysUserMapper.class);
        SysUser sysUser = new SysUser();
        sysUser.setId(id);
        List<SysUser> sysUsers = sysUserMapper.queryUserInfoById(sysUser);
        Assertions.assertEquals(1, sysUsers.size());
        System.out.println(sysUsers);
        sqlSession.close();
    }

    @Test
    void shouldInsertSysUser() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        SysUser sysUser = new SysUser();
        sysUser.setId(1L);
        sysUser.setTenantId("xxx");
        sysUser.setName("MM");
        int count = sqlSession.insert("com.lin.mybatis.mapper.SysUserMapper.insertSysUser", sysUser);
        Assertions.assertEquals(1, count);
        sqlSession.commit();
    }

    @Test
    void shouldGetMapperAndInsertSysUser() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        SysUserMapper mapper = sqlSession.getMapper(SysUserMapper.class);
        SysUser sysUser = new SysUser();
        sysUser.setId(1L);
        sysUser.setTenantId("xxx");
        sysUser.setName("MM");
        int count = mapper.insertSysUser(sysUser);
        Assertions.assertEquals(1, count);
        sqlSession.commit();
    }

    @Test
    void shouldUpdateUserName() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        SysUser sysUser = new SysUser();
        sysUser.setId(1L);
        sysUser.setName("MMXX");
        int count = sqlSession.update("com.lin.mybatis.mapper.SysUserMapper.updateUserName", sysUser);
        Assertions.assertEquals(1, count);
        sqlSession.commit();
    }

    @Test
    void shouldGetMapperAndUpdateUserName() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        SysUserMapper mapper = sqlSession.getMapper(SysUserMapper.class);
        SysUser sysUser = new SysUser();
        sysUser.setId(1L);
        sysUser.setName("MMXX");
        int count = mapper.updateUserName("MMXX", 1L);
        Assertions.assertEquals(1, count);
        sqlSession.commit();
    }

    @Test
    void shouldDeleteUserById() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        SysUser sysUser = new SysUser();
        sysUser.setId(2030009L);
        int count = sqlSession.update("com.lin.mybatis.mapper.SysUserMapper.deleteUserById", sysUser);
        Assertions.assertEquals(1, count);
        sqlSession.commit();
    }

}
