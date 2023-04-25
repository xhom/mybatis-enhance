package com.vz.mybatis.enhance.common.mapper;

import com.vz.mybatis.enhance.common.mapper.hp.MapperHelper;
import com.vz.mybatis.enhance.common.mapper.hp.SqlHelper;
import com.vz.mybatis.enhance.common.mapper.inf.ColumnINF;
import com.vz.mybatis.enhance.common.mapper.inf.TableINF;
import com.vz.mybatis.enhance.common.mapper.qr.BaseExample;
import com.vz.mybatis.enhance.common.mapper.qr.Criterion;
import com.vz.mybatis.enhance.common.mapper.qr.Querier;
import org.apache.ibatis.builder.annotation.ProviderContext;
import org.apache.ibatis.jdbc.SQL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author visy.wang
 * @description:
 * @date 2023/4/24 13:18
 */
public class BaseSqlProvider {
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final Logger logger = LoggerFactory.getLogger(BaseSqlProvider.class);

    public String selectByPrimaryKey(Map<String,Object> params, ProviderContext context){
        TableINF table = MapperHelper.getTable(context);
        ColumnINF primaryKey = table.getPrimaryKey();
        String sql =  SqlHelper.sql()
                .select(table.selectColumnsAsProperties())
                .from(table.getTableName())
                .where(primaryKey.getColumn() + " = #{id}")
                .toStr();
        printLog(context, "selectByPrimaryKey", sql, params);
        return sql;
    }

    public String selectByExample(Map<String,Object> params, ProviderContext context){
        Querier<?> querier = (Querier<?>)params.get("querier");
        TableINF table = MapperHelper.getTable(context);
        BaseExample example = querier.getExample();
        String sql = SqlHelper.sql()
                .select(table.selectColumnsAsProperties(), example.getDistinct())
                .from(table.getTableName())
                .where(getCondition(example, params))
                .orderBy(example.getOrderByClause())
                .limit(example.getLimitClause())
                .toStr();
        printLog(context, "selectByExample", sql, params);
        return sql;
    }

    public String countByExample(Map<String,Object> params, ProviderContext context){
        Querier<?> querier = (Querier<?>)params.get("querier");
        TableINF table = MapperHelper.getTable(context);
        BaseExample example = querier.getExample();
        String sql = SqlHelper.sql()
                .count(table.getPrimaryKey().getColumn(), example.getDistinct())
                .from(table.getTableName())
                .where(getCondition(example, params))
                .toStr();
        printLog(context, "countByExample", sql, params);
        return sql;
    }

    public String deleteByPrimaryKey(Map<String,Object> params, ProviderContext context){
        TableINF table = MapperHelper.getTable(context);
        ColumnINF primaryKey = table.getPrimaryKey();
        String sql =  SqlHelper.sql()
                .delete()
                .from(table.getTableName())
                .where(primaryKey.getColumn() + " = #{id}")
                .toStr();
        printLog(context, "deleteByPrimaryKey", sql, params);
        return sql;
    }

    public String deleteByExample(Map<String,Object> params, ProviderContext context){
        Querier<?> querier = (Querier<?>)params.get("querier");
        TableINF table = MapperHelper.getTable(context);
        BaseExample example = querier.getExample();
        String sql = SqlHelper.sql()
                .delete()
                .from(table.getTableName())
                .where(getCondition(example, params))
                .toStr();
        printLog(context, "deleteByExample", sql, params);
        return sql;
    }

    public String insert(Object entity, ProviderContext context){
        TableINF table = MapperHelper.getTable(context);
        List<String> columns = new ArrayList<>(), values = new ArrayList<>();
        table.getColumns().forEach(item -> {
            if(item.getIsPrimaryKey()){
                //跳过主键，主键由数据库自增自动产生
                return;
            }
            columns.add(item.getColumn());
            try{
                Object o = item.getField().get(entity);
                values.add(getSqlValue(o));
            }catch (Exception e){
                values.add(getSqlValue(null));
                e.printStackTrace();
            }
        });

        String sql = SqlHelper.sql()
                .insert(table.getTableName())
                .values(columns, values)
                .toStr();
        printLog(context, "insert", sql, entity);
        return sql;
    }

    public String insertSelective(Object entity, ProviderContext context){
        TableINF table = MapperHelper.getTable(context);
        List<String> columns = new ArrayList<>(), values = new ArrayList<>();
        table.getColumns().forEach(item -> {
            if(item.getIsPrimaryKey()){
                //跳过主键，主键由数据库自增自动产生
                return;
            }
            try{
                Object o = item.getField().get(entity);
                if(Objects.nonNull(o)){
                    //只写入非NUll的字段
                    columns.add(item.getColumn());
                    values.add(getSqlValue(o));
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        });

        String sql = SqlHelper.sql()
                .insert(table.getTableName())
                .values(columns, values)
                .toStr();
        printLog(context, "insertSelective", sql, entity);
        return sql;
    }

    public String updateByPrimaryKey(Object entity, ProviderContext context){
        TableINF table = MapperHelper.getTable(context);
        Map<String,String> setValues = new HashMap<>();
        StringBuilder condition = new StringBuilder();
        table.getColumns().forEach(item -> {
            try{
                Object o = item.getField().get(entity);
                if(item.getIsPrimaryKey()){
                    condition.append(item.getColumn()).append("=").append(getSqlValue(o));
                }else{
                    setValues.put(item.getColumn(), getSqlValue(o));
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        });

        String sql = SqlHelper.sql()
                .update(table.getTableName())
                .set(setValues)
                .where(condition.toString())
                .toStr();
        printLog(context, "updateByPrimaryKey", sql, entity);
        return sql;
    }

    private static String getCondition(BaseExample example, Map<String,Object> params){
        params.clear();
        StringBuilder condition = new StringBuilder();
        example.getCriteriaList().forEach(criteria -> {
            for (Criterion cri : criteria.getAllCriteria()) {
                if(condition.length() > 0){
                    condition.append(" AND ");
                }

                String property = cri.getProperty();
                Object value = cri.getValue();

                if(cri.isNoValue()){
                    condition.append(cri.getCondition());
                }else if(cri.isSingleValue()){
                    params.put(property, value);
                    condition.append(cri.getCondition()).append("#{").append(property).append("}");
                }else if(cri.isListValue()){
                    params.put(property, value);
                    String inWhere = "";
                    if(value instanceof Collection){
                        Collection<?> collection = (Collection<?>) value;
                        if(!CollectionUtils.isEmpty(collection)){
                            StringBuilder inList = new StringBuilder();
                            for (int i=0; i<collection.size(); i++) {
                                inList.append("#{").append(property).append("[").append(i++).append("]},");
                            }
                            inWhere = inList.deleteCharAt(inList.length()-1).toString();
                        }
                    }
                    condition.append(cri.getCondition()).append("(").append(inWhere).append(")");
                }else if(cri.isBetweenValue()){
                    String property1 = property+"1", property2 = property+"2";
                    Object secondValue = cri.getSecondValue();
                    params.put(property1, value);
                    params.put(property2, secondValue);
                    condition
                            .append(cri.getCondition())
                            .append("#{").append(property1).append("}")
                            .append(" AND ")
                            .append("#{").append(property2).append("}");
                }else{
                    condition.append(cri.getCondition()).append("NULL");
                }
            }
        });
        return condition.toString();
    }

    private static String getSqlValue(Object value){
        if(Objects.isNull(value)){
            return "NULL";
        }
        if(value instanceof Number){
            return value.toString();
        }else if(value instanceof Date){
            Date date = (Date) value;
            return "'"+dateFormat(date)+"'";
        }else if(value instanceof Collection){
            String inWhere = "";
            Collection<?> collection = (Collection<?>)value;
            if(!CollectionUtils.isEmpty(collection)){
                StringBuilder inList = new StringBuilder();
                for (Object item : collection) {
                    if(item instanceof Number){
                        inList.append(item).append(",");
                    }else{
                        inList.append("'").append(item).append("',");
                    }
                }
                inWhere = inList.deleteCharAt(inList.length()-1).toString();
            }
            return inWhere;
        }else{
            return "'"+value+"'";
        }
    }

    private static void printLog(ProviderContext context, String method, String sql, Object params){
        String mapperClassName = context.getMapperType().getName();
        String separator = "-----------------------------------------------------------";
        logger.info("\n{}\nMethod: {}.{}\nSql: {} \nParams: {}\n{}", separator, mapperClassName, method, sql, params, separator);
    }

    private static String dateFormat(Date date){
        return format.format(date);
    }
}
