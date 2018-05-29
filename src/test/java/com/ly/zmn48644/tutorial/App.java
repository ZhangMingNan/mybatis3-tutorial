package com.ly.zmn48644.tutorial;


import com.ly.zmn48644.tutorial.mapper.AuthorMapper;
import com.ly.zmn48644.tutorial.mapper.BlogMapper;
import com.ly.zmn48644.tutorial.mapper.CommentMapper;
import com.ly.zmn48644.tutorial.model.Author;
import com.ly.zmn48644.tutorial.model.Blog;
import com.ly.zmn48644.tutorial.model.Comment;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class App {
    SqlSession sqlSession;

    @Before
    public void setup() throws IOException {
        // 第一步 设置配置文件,完成各种初始化工作.
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        // 第二步 获取 sqlSession
        sqlSession = sqlSessionFactory.openSession();
    }

    @Test
    public void testAuthor() throws IOException {
        AuthorMapper mapper = sqlSession.getMapper(AuthorMapper.class);
        Author author = mapper.selectAuthorById(1);
        System.out.println(author);
        //关闭会话
        sqlSession.close();
    }

    @Test
    public void testComment() throws IOException {

        CommentMapper commentMapper = sqlSession.getMapper(CommentMapper.class);
        Comment comment = new Comment();
        comment.setId(1);
        comment.setPostId(1);
        List<Comment> comments = commentMapper.selectComment(comment);
        for (Comment comm : comments) {
            System.out.println(comm.toString());
        }
    }

    @Test
    public void testBlogCount() throws IOException {
        // 第一步 设置配置文件,完成各种初始化工作!
        BlogMapper mapper = sqlSession.getMapper(BlogMapper.class);
        int count = mapper.blogCount();
        System.out.println(count);
        //关闭会话
        sqlSession.close();
    }

    @Test
    public void testBlog() throws IOException {
        // 第一步 设置配置文件,完成各种初始化工作.

        BlogMapper mapper = sqlSession.getMapper(BlogMapper.class);
        Blog blog = mapper.selectBlogDetails(1);
        System.out.println(blog.toString());
        //关闭会话
        sqlSession.close();
    }
}
