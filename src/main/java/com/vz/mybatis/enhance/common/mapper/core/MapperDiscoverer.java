package com.vz.mybatis.enhance.common.mapper.core;

import com.vz.mybatis.enhance.common.mapper.hp.MapperHelper;
import com.vz.mybatis.enhance.common.mapper.inf.COLUMN_INF;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author visy.wang
 * @description: Mapper发现器
 * @date 2023/4/28 13:43
 */
@Component
public class MapperDiscoverer implements ApplicationListener<ContextRefreshedEvent> {
    private static final Logger logger = LoggerFactory.getLogger(MapperDiscoverer.class);
    private static Field keyColumnsField, keyPropertiesField, keyGeneratorField;

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        if(Objects.nonNull(applicationContext.getParent())){
            return;
        }
        @SuppressWarnings("rawtypes")
        Map<String, BaseMapper> mappers = applicationContext.getBeansOfType(BaseMapper.class);
        Map<String, List<MappedStatement>> insertMappedStatements = getInsertMappedStatements();
        mappers.forEach((name, proxyMapper) -> {
            Class<?> mapperType = proxyMapper.getClass().getInterfaces()[0];
            //获取对应Mapper的Insert语句列表
            List<MappedStatement> mappedStatements = insertMappedStatements.get(mapperType.getName());
            if(CollectionUtils.isEmpty(mappedStatements)){
                return;
            }
            COLUMN_INF pkColumn = MapperHelper.getTable(mapperType).getPkColumn();
            mappedStatements.forEach(statement -> {
                //修改Insert语句的配置，实现主键的回写
                String keyProperty = BaseSqlProvider.ENTITY_NAME+"."+pkColumn.getProperty();
                modifyMappedStatement(statement, pkColumn.getColumn(), keyProperty);
            });
        });
    }

    /**
     * 获取MyBatis中所有已注册的Insert语句的配置
     * @return <Mapper类全路径，Insert语句列表>
     */
    private Map<String,List<MappedStatement>> getInsertMappedStatements(){
        Configuration configuration = sqlSessionTemplate.getConfiguration();
        Collection<MappedStatement> mappedStatements = configuration.getMappedStatements();
        return mappedStatements.stream().filter(statement -> {
            return SqlCommandType.INSERT.equals(statement.getSqlCommandType());
        }).collect(Collectors.groupingBy(statement -> {
            String statementId = statement.getId();
            return statementId.substring(0, statementId.lastIndexOf("."));
        }));
    }

    /**
     * 利用反射修改配置
     * 相当于添加了注解： @Options(useGeneratedKeys = true, keyColumn = "keyColumn", keyProperty = "keyProperty")
     * @param statement SQL语句，对应Mapper的一个方法
     * @param keyColumn 主键在数据库的名称
     * @param keyProperty 主键在实体对象的名称
     */
    private static void modifyMappedStatement(MappedStatement statement, String keyColumn, String keyProperty){
        try{
            if(Objects.isNull(keyColumnsField)){
                Class<?> statementClass = statement.getClass();
                keyColumnsField = statementClass.getDeclaredField("keyColumns");
                keyPropertiesField = statementClass.getDeclaredField("keyProperties");
                keyGeneratorField = statementClass.getDeclaredField("keyGenerator");
                AccessibleObject[] accessibleObjects = {keyColumnsField, keyPropertiesField, keyGeneratorField};
                Field.setAccessible(accessibleObjects, true);
            }
            keyColumnsField.set(statement, new String[]{keyColumn});
            keyPropertiesField.set(statement, new String[]{keyProperty});
            keyGeneratorField.set(statement, Jdbc3KeyGenerator.INSTANCE);
            logger.info("{} mapped statement modify success, keyColumn={}, keyProperty={}", statement.getId(), keyColumn, keyProperty);
        }catch (Exception e){
            logger.info("{} mapped statement modify failure: {}", statement.getId(), e.getMessage());
        }
    }
}
