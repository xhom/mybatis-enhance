package com.vz.mybatis.enhance.common.mapper.hp;

import com.vz.mybatis.enhance.common.mapper.core.BaseMapper;
import com.vz.mybatis.enhance.common.mapper.inf.COLUMN_INF;
import com.vz.mybatis.enhance.common.mapper.inf.TABLE_INF;
import org.apache.ibatis.builder.annotation.ProviderContext;
import org.apache.ibatis.session.SqlSession;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * @author visy.wang
 * @description: Mapper助手
 * @date 2023/4/24 16:17
 */
public class MapperHelper {
    /**
     * 表信息缓存
     * Map<Mapper类全名, 对应的表信息>
     */
    private static final Map<String, TABLE_INF> tablesCache = new ConcurrentHashMap<>();

    /**
     * 获取表信息（有缓存）
     * @param context SQLProvider 上下文
     * @return 表信息
     */
    public static TABLE_INF getTable(ProviderContext context){
        String mapperClassName = context.getMapperType().getName();

        //优先从缓存获取表信息
        TABLE_INF table = tablesCache.get(mapperClassName);
        if(Objects.nonNull(table)){
            return table;
        }

        //创建表信息并放入缓存
        Class<?> entityClass = getEntityType(context);
        table = createTable(entityClass);
        tablesCache.put(mapperClassName, table);

        return table;
    }

    /**
     * 创建表信息
     * @param entityClass 表对应的实体类型
     * @return 表信息
     */
    private static TABLE_INF createTable(Class<?> entityClass){
        //获取表名，此处直接将类名转换为下划线形式，也可定义注解，然后从注解中获取
        String tableName = NameHelper.camel2underline(entityClass.getSimpleName());

        TABLE_INF table = new TABLE_INF();
        table.setTableName(tableName);
        table.setEntityClass(entityClass);

        //获取主键字段名列表
        List<String> pkColumns = getPkColumns(tableName);

        Field[] fields = entityClass.getDeclaredFields();
        for (Field field : fields) {
            if(Modifier.isStatic(field.getModifiers())){
                //忽略静态属性
                continue;
            }
            field.setAccessible(true);
            String property = field.getName();

            COLUMN_INF column = new COLUMN_INF();
            column.setField(field);
            column.setProperty(property);
            column.setFieldClass(field.getType());
            column.setColumn(NameHelper.camel2underline(property));
            if(pkColumns.contains(column.getColumn())){
                column.setIsPK(true);//标记为主键字段
                if(Objects.isNull(table.getPkColumn())){
                    //只记录第一个主键字段
                    table.setPkColumn(column);
                }
            }

            table.addColumn(column);
        }

        return table;
    }

    /**
     * 通过Mapper类的泛型参数获取表对应的实体类信息
     * @param context SQLProvider 上下文
     * @return 表对应的实体类信息
     */
    private static Class<?> getEntityType(ProviderContext context) {
        Class<?> mapperType = context.getMapperType();
        Type[] genericInterfaces = mapperType.getGenericInterfaces();//实现的获取接口列表
        return Stream.of(genericInterfaces)
                .filter(ParameterizedType.class::isInstance) //过滤出带有泛型参数的接口
                .map(ParameterizedType.class::cast) //将Type强转为ParameterizedType
                .filter(pType -> BaseMapper.class.equals(pType.getRawType()))//过滤出BaseMapper
                .findFirst() //取第一个
                .map(pType -> pType.getActualTypeArguments()[0]) //取泛型参数中的第一个
                .filter(Class.class::isInstance)//过滤出Class的实例
                .map(Class.class::cast)//将Type转为Class
                .orElseThrow(() -> {
                    //抛出异常
                    return new IllegalArgumentException("未找到BaseMapper的泛型类："+mapperType.getName());
                });
    }

    /**
     * 获取主键列字段名（仅适用于MySQL）
     * @param tableName 表名
     * @return 主键字段名列表（联合主键才会有多个）
     */
    private static List<String> getPkColumns(String tableName){
        List<String> columnList = new ArrayList<>();
        String sql = "SELECT COLUMN_NAME FROM information_schema.`COLUMNS` WHERE TABLE_NAME = '"+tableName+"' AND COLUMN_KEY = 'PRI'";
        SqlSession sqlSession = SqlSessionHelper.getSqlSession();
        try(Connection connection = sqlSession.getConnection();
            Statement statement = connection.createStatement()){
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                columnList.add(resultSet.getString("COLUMN_NAME"));
            }
            return columnList;
        }catch (SQLException e){
            e.printStackTrace();
            columnList.add("id"); //默认"id"为主键
            return columnList;
        }finally {
            SqlSessionHelper.closeSqlSession();
        }
    }
}
