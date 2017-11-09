package com.ly.zmn48644.tutorial;


import com.ly.zmn48644.tutorial.mapper.AdminMapper;
import com.ly.zmn48644.tutorial.model.Admin;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class App {
    public static void main(String[] args) throws IOException {
        // 第一步 设置配置文件,完成各种初始化工作.
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);

        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        // 第二步 获取 sqlSession
        SqlSession sqlSession = sqlSessionFactory.openSession();

        // 第三步 从sqlSession获取想要的mapper对象,调用想要执行的方法
        AdminMapper mapper = sqlSession.getMapper(AdminMapper.class);
        List<Admin> admins = mapper.selectAdmin();

        //System.out.println(admin.getEmail());

    }

}
