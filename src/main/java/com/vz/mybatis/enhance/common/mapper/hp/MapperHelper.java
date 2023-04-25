package com.vz.mybatis.enhance.common.mapper.hp;

import com.vz.mybatis.enhance.common.mapper.BaseMapper;
import com.vz.mybatis.enhance.common.mapper.inf.ColumnINF;
import com.vz.mybatis.enhance.common.mapper.inf.TableINF;
import org.apache.ibatis.builder.annotation.ProviderContext;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * @author visy.wang
 * @description:
 * @date 2023/4/24 16:17
 */
public class MapperHelper {
    private static final Map<Class<?>, TableINF> tablesCache = new ConcurrentHashMap<>();

    public static TableINF getTable(ProviderContext context){
        Class<?> mapperType = context.getMapperType();

        TableINF table = tablesCache.get(mapperType);
        if(Objects.nonNull(table)){
            return table;
        }

        Class<?> entityClass = getEntityType(context);
        table = createTable(entityClass);
        tablesCache.put(mapperType, table);

        return table;
    }

    private static TableINF createTable(Class<?> entityClass){
        //可优先从注解获取，此处直接将类名转换为下划线形式
        String tableName = NameHelper.camel2underline(entityClass.getSimpleName());

        TableINF table = new TableINF();
        table.setTableName(tableName);
        table.setEntityClass(entityClass);

        Field[] fields = entityClass.getDeclaredFields();
        for (Field field : fields) {
            if(Modifier.isStatic(field.getModifiers())){
                //忽略静态属性
                continue;
            }
            field.setAccessible(true);
            String property = field.getName();

            ColumnINF column = new ColumnINF();
            column.setProperty(property);
            column.setColumn(NameHelper.camel2underline(property));
            column.setField(field);
            column.setFieldClass(field.getType());
            if("id".equals(property)){
                //可根据注解判断，此处直接看名称是否叫“id”
                column.setIsPrimaryKey(true);
                table.setPrimaryKey(column);
            }

            table.addColumn(column);
        }

        return table;
    }

    private static Class<?> getEntityType(ProviderContext context) {
        Class<?> mapperType = context.getMapperType();
        Type[] genericInterfaces = mapperType.getGenericInterfaces();//实现的获取接口列表
        return Stream.of(genericInterfaces)
                .filter(ParameterizedType.class::isInstance) //过滤出带有泛型参数的接口
                .map(ParameterizedType.class::cast) //将Type强转为ParameterizedType
                .filter(pType -> BaseMapper.class.equals(pType.getRawType()))//过滤出BaseMapper
                .findFirst() //取第一个
                .map(pType -> pType.getActualTypeArguments()[0]) //取反省参数中的第一个
                .filter(Class.class::isInstance)//过滤出Class的实例
                .map(Class.class::cast)//将Type转为Class
                .orElseThrow(() -> {
                    //抛出异常
                    return new IllegalStateException("未找到BaseMapper的泛型类："+mapperType.getName());
                });
    }
}
