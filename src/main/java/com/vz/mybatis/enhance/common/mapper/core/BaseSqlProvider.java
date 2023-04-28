package com.vz.mybatis.enhance.common.mapper.core;

import com.vz.mybatis.enhance.common.mapper.hp.MapperHelper;
import com.vz.mybatis.enhance.common.mapper.hp.SqlHelper;
import com.vz.mybatis.enhance.common.mapper.inf.COLUMN_INF;
import com.vz.mybatis.enhance.common.mapper.inf.TABLE_INF;
import com.vz.mybatis.enhance.common.mapper.qr.BaseExample;
import com.vz.mybatis.enhance.common.mapper.qr.Criterion;
import com.vz.mybatis.enhance.common.mapper.qr.Querier;
import org.apache.ibatis.builder.annotation.ProviderContext;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author visy.wang
 * @description: 基础SQL生成
 * @date 2023/4/24 13:18
 */
public class BaseSqlProvider {
    private static final Logger logger = LoggerFactory.getLogger(BaseSqlProvider.class);

    public String selectById(Map<String,Object> params, ProviderContext context){
        TABLE_INF table = MapperHelper.getTable(context);
        COLUMN_INF pkColumn = table.getPkColumn();
        return SqlHelper.sql()
                .select(table.allColumns())
                .from(table.getTableName())
                .where(pkColumn.getColumn() + " = #{id}")
                .toStr(sql -> log(context, sql, washing(params)));
    }

    public String selectByIds(Map<String,Object> params, ProviderContext context){
        Object idList = params.get("idList");
        TABLE_INF table = MapperHelper.getTable(context);
        COLUMN_INF pkColumn = table.getPkColumn();
        return SqlHelper.sql()
                .select(table.allColumns())
                .from(table.getTableName())
                .where(getInCondition(pkColumn.getColumn(), "idList", idList))
                .toStr(sql -> log(context, sql, washing(params)));
    }

    public String selectOne(Map<String,Object> params, ProviderContext context){
        Querier<?> querier = (Querier<?>)params.get("querier");
        TABLE_INF table = MapperHelper.getTable(context);
        BaseExample example = querier.getExample();
        return SqlHelper.sql()
                .select(table.allColumns(), example.getDistinct())
                .from(table.getTableName())
                .where(getConditions(example, params))
                .orderBy(example.getOrderByClause())
                .limit("1") //只取查询结果中第一条记录
                .toStr(sql -> log(context, sql, washing(params)));
    }

    public String selectList(Map<String,Object> params, ProviderContext context){
        Querier<?> querier = (Querier<?>)params.get("querier");
        TABLE_INF table = MapperHelper.getTable(context);
        BaseExample example = querier.getExample();
        return SqlHelper.sql()
                .select(table.allColumns(), example.getDistinct())
                .from(table.getTableName())
                .where(getConditions(example, params))
                .orderBy(example.getOrderByClause())
                .limit(example.getLimitClause())
                .toStr(sql -> log(context, sql, washing(params)));
    }

    public String selectAll(ProviderContext context){
        TABLE_INF table = MapperHelper.getTable(context);
        return SqlHelper.sql()
                .select(table.allColumns())
                .from(table.getTableName())
                .toStr(sql -> log(context, sql, null));
    }

    public String count(Map<String,Object> params, ProviderContext context){
        Querier<?> querier = (Querier<?>)params.get("querier");
        TABLE_INF table = MapperHelper.getTable(context);
        BaseExample example = querier.getExample();
        return SqlHelper.sql()
                .count(table.getPkColumn().getColumn(), example.getDistinct())
                .from(table.getTableName())
                .where(getConditions(example, params))
                .toStr(sql -> log(context, sql, washing(params)));
    }

    public String countAll(ProviderContext context){
        TABLE_INF table = MapperHelper.getTable(context);
        return SqlHelper.sql()
                .count(table.getPkColumn().getColumn())
                .from(table.getTableName())
                .toStr(sql -> log(context, sql, null));
    }

    public String deleteById(Map<String,Object> params, ProviderContext context){
        TABLE_INF table = MapperHelper.getTable(context);
        COLUMN_INF pkColumn = table.getPkColumn();
        return SqlHelper.sql()
                .delete()
                .from(table.getTableName())
                .where(pkColumn.getColumn() + " = #{id}")
                .toStr(sql -> log(context, sql, washing(params)));
    }

    public String deleteByIds(Map<String,Object> params, ProviderContext context){
        Object idList = params.get("idList");
        TABLE_INF table = MapperHelper.getTable(context);
        COLUMN_INF pkColumn = table.getPkColumn();
        return SqlHelper.sql()
                .delete()
                .from(table.getTableName())
                .where(getInCondition(pkColumn.getColumn(), "idList", idList))
                .toStr(sql -> log(context, sql, washing(params)));
    }

    public String delete(Map<String,Object> params, ProviderContext context){
        Querier<?> querier = (Querier<?>)params.get("querier");
        TABLE_INF table = MapperHelper.getTable(context);
        BaseExample example = querier.getExample();
        return SqlHelper.sql()
                .delete()
                .from(table.getTableName())
                .where(getConditions(example, params))
                .toStr(sql -> log(context, sql, washing(params)));
    }

    public String insert(Map<String,Object> params, ProviderContext context){
        Object entity = params.get("record");
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

        return SqlHelper.sql()
                .insert(table.getTableName())
                .values(columns, values)
                .toStr(sql -> log(context, sql, washing(params)));
    }

    public String insertSelective(Map<String,Object> params, ProviderContext context){
        Object entity = params.get("record");
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

        return SqlHelper.sql()
                .insert(table.getTableName())
                .values(columns, values)
                .toStr(sql -> log(context, sql, washing(params)));
    }

    public String updateById(Map<String,Object> params, ProviderContext context){
        Object entity = params.get("record");
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
                        throw new IllegalArgumentException("The property '"+property+"' can not be null !");
                    }
                    condition.append(column).append("=").append("#{").append(property).append("}");
                }else{
                    setValues.put(column, "#{"+property+"}");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        });

        return SqlHelper.sql()
                .update(table.getTableName())
                .set(setValues)
                .where(condition.toString())
                .toStr(sql -> log(context, sql, washing(params)));
    }

    public String updateByIdSelective(Map<String,Object> params, ProviderContext context){
        Object entity = params.get("record");
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
                        throw new IllegalArgumentException("The property '"+property+"' can not be null !");
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

        return SqlHelper.sql()
                .update(table.getTableName())
                .set(setValues)
                .where(condition.toString())
                .toStr(sql -> log(context, sql, washing(params)));
    }

    public String update(Map<String,Object> params, ProviderContext context){
        Object entity = params.get("record");
        Querier<?> querier = (Querier<?>)params.get("querier");
        BaseExample example = querier.getExample();
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

        return SqlHelper.sql()
                .update(table.getTableName())
                .set(setValues)
                .where(getConditions(example, params))
                .toStr(sql -> log(context, sql, washing(params)));
    }

    public String updateSelective(Map<String,Object> params, ProviderContext context){
        Object entity = params.get("record");
        Querier<?> querier = (Querier<?>)params.get("querier");
        BaseExample example = querier.getExample();
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

        return SqlHelper.sql()
                .update(table.getTableName())
                .set(setValues)
                .where(getConditions(example, params))
                .toStr(sql -> log(context, sql, washing(params)));
    }

    private static String getConditions(BaseExample example, Map<String,Object> params){
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
                    String inSequence = getInSequence(property, value);
                    condition.append(cri.getCondition()).append("(").append(inSequence).append(")");
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

    private static String getInCondition(String column, String property, Object coll){
        return column + " IN (" + getInSequence(property, coll) + ")";
    }

    private static String getInSequence(String property, Object coll){
        if(coll instanceof Collection){
            Collection<?> collection = (Collection<?>) coll;
            if(!CollectionUtils.isEmpty(collection)){
                StringBuilder inSequence = new StringBuilder();
                for (int i=0; i<collection.size(); i++) {
                    inSequence.append("#{").append(property).append("[").append(i).append("]},");
                }
                return inSequence.deleteCharAt(inSequence.length()-1).toString();
            }
        }
        return "";
    }

    private static Map<String,Object> washing(Map<String,Object> params){
        Arrays.asList("param1", "querier", "record").forEach(params::remove);
        return params;
    }

    private static void log(ProviderContext context, String sql, Object params){
        String mapperMethodName = context.getMapperType().getName()+"."+context.getMapperMethod().getName();
        logger.info("\nMethod: {}\nSql: {}\nParams: {}", mapperMethodName, sql, params==null?"{ }":params);
    }
}
