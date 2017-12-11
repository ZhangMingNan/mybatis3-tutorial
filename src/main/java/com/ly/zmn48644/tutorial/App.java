package com.ly.zmn48644.tutorial;


import com.ly.zmn48644.tutorial.mapper.BlogMapper;
import com.ly.zmn48644.tutorial.model.Blog;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class App {
    
    private static void testBlogCount() throws IOException {
        // 第一步 设置配置文件,完成各种初始化工作.
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        // 第二步 获取 sqlSession
        SqlSession sqlSession = sqlSessionFactory.openSession();
        BlogMapper mapper = sqlSession.getMapper(BlogMapper.class);
        int count = mapper.blogCount();
        System.out.println(count);
        //关闭会话
        sqlSession.close();
    }

    private static void testBlog() throws IOException {
        // 第一步 设置配置文件,完成各种初始化工作.
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        // 第二步 获取 sqlSession
        SqlSession sqlSession = sqlSessionFactory.openSession();
        BlogMapper mapper = sqlSession.getMapper(BlogMapper.class);
        Blog blog = mapper.selectBlogDetails(1);
        System.out.println(blog.toString());
        //关闭会话
        sqlSession.close();
    }
}
