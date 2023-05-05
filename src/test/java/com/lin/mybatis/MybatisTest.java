package com.lin.mybatis;

import com.lin.mybatis.entity.SysUser;
import com.lin.mybatis.io.Resources;
import com.lin.mybatis.session.SqlSession;
import com.lin.mybatis.session.SqlSessionFactory;
import com.lin.mybatis.session.SqlSessionFactoryBuilder;
import com.lin.mybatis.util.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.util.List;

/**
 * @Author linjiayi5
 * @Date 2023/4/27 15:52:16
 */
public class MybatisTest {

    private static SqlSessionFactory sqlSessionFactory;

    @BeforeAll
    static void setUp() throws Exception {
        // create an SqlSessionFactory
        try (Reader reader = Resources
                .getResourceAsReader("mybatis-config.xml")) {
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        }
    }

    @Test
    void test() {
        Assert.notNull(sqlSessionFactory, "");
        SqlSession sqlSession = sqlSessionFactory.openSession();
        List<SysUser> list = sqlSession.selectList("com.lin.mybatis.mapper.UserMapper.queryUserInfoById", 3);
        System.out.println(list);
    }

}
