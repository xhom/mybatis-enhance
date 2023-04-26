package com.vz.mybatis.enhance.common.mapper;

import com.vz.mybatis.enhance.common.mapper.hp.MapperHelper;
import com.vz.mybatis.enhance.common.mapper.hp.SqlHelper;
import com.vz.mybatis.enhance.common.mapper.inf.COLUMN_INF;
import com.vz.mybatis.enhance.common.mapper.inf.TABLE_INF;
import com.vz.mybatis.enhance.common.mapper.qr.BaseExample;
import com.vz.mybatis.enhance.common.mapper.qr.Criterion;
import com.vz.mybatis.enhance.common.mapper.qr.Querier;
import org.apache.ibatis.builder.annotation.ProviderContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author visy.wang
 * @description:
 * @date 2023/4/24 13:18
 */
public class BaseSqlProvider {
    private static final Logger logger = LoggerFactory.getLogger(BaseSqlProvider.class);

    public String selectById(Map<String,Object> params, ProviderContext context){
        params.remove("param1");
        TABLE_INF table = MapperHelper.getTable(context);
        COLUMN_INF pkColumn = table.getPkColumn();
        String sql =  SqlHelper.sql()
                .select(table.selectColumnsAsProperties())
                .from(table.getTableName())
                .where(pkColumn.getColumn() + " = #{id}")
                .toStr();
        printLog(context, "selectById", sql, params);
        return sql;
    }

    public String selectByIds(Map<String,Object> params, ProviderContext context){
        params.remove("param1");
        Object idList = params.get("idList");
        TABLE_INF table = MapperHelper.getTable(context);
        COLUMN_INF pkColumn = table.getPkColumn();
        String sql =  SqlHelper.sql()
                .select(table.selectColumnsAsProperties())
                .from(table.getTableName())
                .where(pkColumn.getColumn() + " IN (" + getInWhere("idList", idList) + ")")
                .toStr();
        printLog(context, "selectByIds", sql, params);
        return sql;
    }

    public String selectList(Map<String,Object> params, ProviderContext context){
        Querier<?> querier = (Querier<?>)params.get("querier");
        params.clear();
        TABLE_INF table = MapperHelper.getTable(context);
        BaseExample example = querier.getExample();
        String sql = SqlHelper.sql()
                .select(table.selectColumnsAsProperties(), example.getDistinct())
                .from(table.getTableName())
                .where(getCondition(example, params))
                .orderBy(example.getOrderByClause())
                .limit(example.getLimitClause())
                .toStr();
        printLog(context, "select", sql, params);
        return sql;
    }

    public String selectAll(ProviderContext context){
        TABLE_INF table = MapperHelper.getTable(context);
        String sql = SqlHelper.sql()
                .select(table.selectColumnsAsProperties())
                .from(table.getTableName())
                .toStr();
        printLog(context, "selectAll", sql, Collections.emptyMap());
        return sql;
    }

    public String count(Map<String,Object> params, ProviderContext context){
        Querier<?> querier = (Querier<?>)params.get("querier");
        params.clear();
        TABLE_INF table = MapperHelper.getTable(context);
        BaseExample example = querier.getExample();
        String sql = SqlHelper.sql()
                .count(table.getPkColumn().getColumn(), example.getDistinct())
                .from(table.getTableName())
                .where(getCondition(example, params))
                .toStr();
        printLog(context, "count", sql, params);
        return sql;
    }

    public String countAll(ProviderContext context){
        TABLE_INF table = MapperHelper.getTable(context);
        String sql = SqlHelper.sql()
                .count(table.getPkColumn().getColumn())
                .from(table.getTableName())
                .toStr();
        printLog(context, "countAll", sql, Collections.emptyMap());
        return sql;
    }

    public String deleteById(Map<String,Object> params, ProviderContext context){
        params.remove("param1");
        TABLE_INF table = MapperHelper.getTable(context);
        COLUMN_INF pkColumn = table.getPkColumn();
        String sql =  SqlHelper.sql()
                .delete()
                .from(table.getTableName())
                .where(pkColumn.getColumn() + " = #{id}")
                .toStr();
        printLog(context, "deleteById", sql, params);
        return sql;
    }

    public String deleteByIds(Map<String,Object> params, ProviderContext context){
        params.remove("param1");
        Object idList = params.get("idList");
        TABLE_INF table = MapperHelper.getTable(context);
        COLUMN_INF pkColumn = table.getPkColumn();
        String sql =  SqlHelper.sql()
                .delete()
                .from(table.getTableName())
                .where(pkColumn.getColumn() + " IN (" + getInWhere("idList", idList) + ")")
                .toStr();
        printLog(context, "deleteByIds", sql, params);
        return sql;
    }

    public String delete(Map<String,Object> params, ProviderContext context){
        Querier<?> querier = (Querier<?>)params.get("querier");
        params.clear();
        TABLE_INF table = MapperHelper.getTable(context);
        BaseExample example = querier.getExample();
        String sql = SqlHelper.sql()
                .delete()
                .from(table.getTableName())
                .where(getCondition(example, params))
                .toStr();
        printLog(context, "delete", sql, params);
        return sql;
    }

    public String insert(Map<String,Object> params, ProviderContext context){
        Object entity = params.get("record");
        params.clear();
        TABLE_INF table = MapperHelper.getTable(context);
        List<String> columns = new ArrayList<>(), values = new ArrayList<>();
        table.getColumns().forEach(item -> {
            if(item.getIsPK()){
                //跳过主键，主键由数据库自增自动产生
                return;
            }
            columns.add(item.getColumn());
            String property = item.getProperty();
            values.add("#{"+property+"}");
            try{
                params.put(property, item.getField().get(entity));
            }catch (Exception e){
                params.put(property, null);
                e.printStackTrace();
            }
        });

        String sql = SqlHelper.sql()
                .insert(table.getTableName())
                .values(columns, values)
                .toStr();
        printLog(context, "insert", sql, params);
        return sql;
    }

    public String insertSelective(Map<String,Object> params, ProviderContext context){
        Object entity = params.get("record");
        params.clear();
        TABLE_INF table = MapperHelper.getTable(context);
        List<String> columns = new ArrayList<>(), values = new ArrayList<>();
        table.getColumns().forEach(item -> {
            if(item.getIsPK()){
                //跳过主键，主键由数据库自增自动产生
                return;
            }
            try{
                Object value = item.getField().get(entity);
                if(Objects.nonNull(value)){
                    columns.add(item.getColumn());
                    String property = item.getProperty();
                    values.add("#{"+property+"}");
                    params.put(property, value);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        });

        String sql = SqlHelper.sql()
                .insert(table.getTableName())
                .values(columns, values)
                .toStr();
        printLog(context, "insertSelective", sql, params);
        return sql;
    }

    public String updateById(Map<String,Object> params, ProviderContext context){
        Object entity = params.get("record");
        params.clear();
        TABLE_INF table = MapperHelper.getTable(context);
        Map<String,String> setValues = new HashMap<>();
        StringBuilder condition = new StringBuilder();
        table.getColumns().forEach(item -> {
            try{
                String column = item.getColumn(), property = item.getProperty();
                Object value = item.getField().get(entity);
                params.put(property, value);
                if(item.getIsPK()){
                    if(Objects.isNull(value)){
                        //主键值不能为空
                        throw new IllegalArgumentException(property+" Can not be null !");
                    }
                    condition.append(column).append("=").append("#{").append(property).append("}");
                }else{
                    setValues.put(column, "#{"+property+"}");
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
        printLog(context, "updateById", sql, params);
        return sql;
    }

    public String updateByIdSelective(Map<String,Object> params, ProviderContext context){
        Object entity = params.get("record");
        params.clear();
        TABLE_INF table = MapperHelper.getTable(context);
        Map<String,String> setValues = new HashMap<>();
        StringBuilder condition = new StringBuilder();
        table.getColumns().forEach(item -> {
            try{
                String column = item.getColumn(), property = item.getProperty();
                Object value = item.getField().get(entity);
                if(item.getIsPK()){
                    if(Objects.isNull(value)){
                        //主键值不能为空
                        throw new IllegalArgumentException(property+" Can not be null !");
                    }
                    params.put(property, value);
                    condition.append(column).append("=").append("#{").append(property).append("}");
                }else if(Objects.nonNull(value)){
                    params.put(property, value);
                    setValues.put(column, "#{"+property+"}");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        });

        String sql = SqlHelper.sql().update(table.getTableName()).set(setValues).where(condition.toString()).toStr();
        printLog(context, "updateByIdSelective", sql, params);
        return sql;
    }

    public String update(Map<String,Object> params, ProviderContext context){
        Object entity = params.get("record");
        Querier<?> querier = (Querier<?>)params.get("querier");
        BaseExample example = querier.getExample();
        params.clear();
        TABLE_INF table = MapperHelper.getTable(context);
        Map<String,String> setValues = new HashMap<>();
        table.getColumns().forEach(item -> {
            if(item.getIsPK()){
                //主键不能修改
                return;
            }
            try{
                String property = item.getProperty()+"Alias"; //避免和条件中的属性名冲突
                params.put(property, item.getField().get(entity));
                setValues.put(item.getColumn(), "#{"+property+"}");
            }catch (Exception e){
                e.printStackTrace();
            }
        });

        String sql = SqlHelper.sql().update(table.getTableName()).set(setValues).where(getCondition(example, params)).toStr();
        printLog(context, "update", sql, params);
        return sql;
    }

    public String updateSelective(Map<String,Object> params, ProviderContext context){
        Object entity = params.get("record");
        Querier<?> querier = (Querier<?>)params.get("querier");
        BaseExample example = querier.getExample();
        params.clear();
        TABLE_INF table = MapperHelper.getTable(context);
        Map<String,String> setValues = new HashMap<>();
        table.getColumns().forEach(item -> {
            if(item.getIsPK()){
                //主键不能修改
                return;
            }
            try{
                Object value = item.getField().get(entity);
                if(Objects.nonNull(value)){
                    String property = item.getProperty()+"Alias"; //避免和条件中的属性名冲突
                    params.put(property, value);
                    setValues.put(item.getColumn(), "#{"+property+"}");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        });

        String sql = SqlHelper.sql().update(table.getTableName()).set(setValues).where(getCondition(example, params)).toStr();
        printLog(context, "update", sql, params);
        return sql;
    }

    private static String getCondition(BaseExample example, Map<String,Object> params){
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
                    String inWhere = getInWhere(property, value);
                    condition.append(cri.getCondition()).append("(").append(inWhere).append(")");
                }else if(cri.isBetweenValue()){
                    String property1 = property+"1", property2 = property+"2";
                    Object secondValue = cri.getSecondValue();
                    params.put(property1, value);
                    params.put(property2, secondValue);
                    condition.append(cri.getCondition())
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

    private static String getInWhere(String property, Object value){
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
        return inWhere;
    }

    private static void printLog(ProviderContext context, String method, String sql, Object params){
        String mapperClassName = context.getMapperType().getName();
        String separator = "-----------------------------------------------------------";
        logger.info("\n{}\nMethod: {}.{}\nSql: {} \nParams: {}\n{}", separator, mapperClassName, method, sql, params, separator);
    }

}
