package com.vz.mybatis.enhance.common.mapper.hp;

import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.SqlSessionUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author visy.wang
 * @description: SqlSession助手
 * @date 2023/4/28 10:39
 */
@Component
public class SqlSessionHelper implements ApplicationContextAware {
    private static ApplicationContext context;
    private static SqlSession sqlSession;
    private static SqlSessionTemplate template;

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        context = ctx;
    }

    public static SqlSession getSqlSession(){
        if(sqlSession == null){
            template = context.getBean(SqlSessionTemplate.class);
            sqlSession = SqlSessionUtils.getSqlSession(template.getSqlSessionFactory(),
                    template.getExecutorType(), template.getPersistenceExceptionTranslator());
        }
        return sqlSession;
    }

    public static void closeSqlSession(){
        SqlSessionUtils.closeSqlSession(sqlSession, template.getSqlSessionFactory());
    }
}
