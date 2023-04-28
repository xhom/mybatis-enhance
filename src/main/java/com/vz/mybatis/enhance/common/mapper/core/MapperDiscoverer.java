package com.vz.mybatis.enhance.common.mapper.core;

import com.vz.mybatis.enhance.common.mapper.hp.MapperHelper;
import com.vz.mybatis.enhance.common.mapper.hp.SqlSessionHelper;
import com.vz.mybatis.enhance.common.mapper.inf.COLUMN_INF;
import com.vz.mybatis.enhance.common.mapper.inf.TABLE_INF;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * @author visy.wang
 * @description:
 * @date 2023/4/28 13:43
 */
@Component
public class MapperDiscoverer implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        if(Objects.isNull(applicationContext.getParent())){
            @SuppressWarnings("rawtypes")
            Map<String, BaseMapper> mappers = applicationContext.getBeansOfType(BaseMapper.class);
            //自动将这些标注了注解的bean注册到MQ的事务监听器
            for(BaseMapper<?,?> mapper: mappers.values()){
                Class<?> implMapperClass = (Class<?>) mapper.getClass().getGenericInterfaces()[0];
                TABLE_INF table = MapperHelper.getTable(implMapperClass);
                addOptionsAnnotation(implMapperClass, table);
            }
        }
    }

    private void addOptionsAnnotation(Class<?> mapperClass, TABLE_INF table){
        try{
            COLUMN_INF pkColumn = table.getPkColumn();
            SqlSession sqlSession = SqlSessionHelper.getSqlSession();
            Configuration configuration = sqlSession.getConfiguration();
            Collection<MappedStatement> mappedStatements = configuration.getMappedStatements();
            mappedStatements.forEach(mappedStatement -> {
                String statementId = mappedStatement.getId();
                if(statementId.startsWith(mapperClass.getName()+".insert")){
                    //给Mapper中的insert语句添加配置实现主键的回写
                    modifyMappedStatement(mappedStatement, pkColumn.getColumn(), "record."+pkColumn.getProperty());
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            SqlSessionHelper.closeSqlSession();
        }
    }

    /**
     * 利用反射修改配置
     * 相当于添加了注解： @Options(useGeneratedKeys = true, keyColumn = "keyColumn", keyProperty = "keyProperty")
     * @param statement SQL语句，对应Mapper的一个方法
     * @param keyColumn 主键在数据库的名称
     * @param keyProperty 主键在实体对象的名称
     */
    private static void modifyMappedStatement(MappedStatement statement, String keyColumn, String keyProperty){
        Class<?> clazz = statement.getClass();
        try{
            Field keyColumnsField = clazz.getDeclaredField("keyColumns");
            Field keyPropertiesField = clazz.getDeclaredField("keyProperties");
            Field keyGeneratorField = clazz.getDeclaredField("keyGenerator");
            Field.setAccessible(new AccessibleObject[]{keyColumnsField, keyPropertiesField, keyGeneratorField}, true);

            keyColumnsField.set(statement, new String[]{keyColumn});
            keyPropertiesField.set(statement, new String[]{keyProperty});
            keyGeneratorField.set(statement, Jdbc3KeyGenerator.INSTANCE);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
