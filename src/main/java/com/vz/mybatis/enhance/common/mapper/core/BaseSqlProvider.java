package com.vz.mybatis.enhance.common.mapper.core;

import com.vz.mybatis.enhance.common.mapper.hp.MapperHelper;
import com.vz.mybatis.enhance.common.mapper.hp.SqlHelper;
import com.vz.mybatis.enhance.common.mapper.inf.COLUMN_INF;
import com.vz.mybatis.enhance.common.mapper.inf.TABLE_INF;
import com.vz.mybatis.enhance.common.mapper.qr.BaseExample;
import com.vz.mybatis.enhance.common.mapper.qr.Criterion;
import com.vz.mybatis.enhance.common.mapper.qr.Querier;
import org.apache.ibatis.builder.annotation.ProviderContext;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author visy.wang
 * @description:
 * @date 2023/4/24 13:18
 */
public class BaseSqlProvider {

    public String selectById(Map<String,Object> params, ProviderContext context){
        params.remove("param1");
        TABLE_INF table = MapperHelper.getTable(context);
        COLUMN_INF pkColumn = table.getPkColumn();
        return SqlHelper.sql()
                .select(table.selectColumnsAsProperties())
                .from(table.getTableName())
                .where(pkColumn.getColumn() + " = #{id}")
                .toStrWithLog(context, params);
    }

    public String selectByIds(Map<String,Object> params, ProviderContext context){
        params.remove("param1");
        Object idList = params.get("idList");
        TABLE_INF table = MapperHelper.getTable(context);
        COLUMN_INF pkColumn = table.getPkColumn();
        return SqlHelper.sql()
                .select(table.selectColumnsAsProperties())
                .from(table.getTableName())
                .where(pkColumn.getColumn() + " IN (" + getInWhere("idList", idList) + ")")
                .toStrWithLog(context, params);
    }

    public String selectOne(Map<String,Object> params, ProviderContext context){
        Querier<?> querier = (Querier<?>)params.get("querier");
        params.clear();
        TABLE_INF table = MapperHelper.getTable(context);
        BaseExample example = querier.getExample();
        return SqlHelper.sql()
                .select(table.selectColumnsAsProperties(), example.getDistinct())
                .from(table.getTableName())
                .where(getCondition(example, params))
                .orderBy(example.getOrderByClause())
                .limit("1") //只取查询结果中第一条记录
                .toStrWithLog(context, params);
    }

    public String selectList(Map<String,Object> params, ProviderContext context){
        Querier<?> querier = (Querier<?>)params.get("querier");
        params.clear();
        TABLE_INF table = MapperHelper.getTable(context);
        BaseExample example = querier.getExample();
        return SqlHelper.sql()
                .select(table.selectColumnsAsProperties(), example.getDistinct())
                .from(table.getTableName())
                .where(getCondition(example, params))
                .orderBy(example.getOrderByClause())
                .limit(example.getLimitClause())
                .toStrWithLog(context, params);
    }

    public String selectAll(ProviderContext context){
        TABLE_INF table = MapperHelper.getTable(context);
        return SqlHelper.sql()
                .select(table.selectColumnsAsProperties())
                .from(table.getTableName())
                .toStrWithLog(context, null);
    }

    public String count(Map<String,Object> params, ProviderContext context){
        Querier<?> querier = (Querier<?>)params.get("querier");
        params.clear();
        TABLE_INF table = MapperHelper.getTable(context);
        BaseExample example = querier.getExample();
        return SqlHelper.sql()
                .count(table.getPkColumn().getColumn(), example.getDistinct())
                .from(table.getTableName())
                .where(getCondition(example, params))
                .toStrWithLog(context, params);
    }

    public String countAll(ProviderContext context){
        TABLE_INF table = MapperHelper.getTable(context);
        return SqlHelper.sql()
                .count(table.getPkColumn().getColumn())
                .from(table.getTableName())
                .toStrWithLog(context, null);
    }

    public String deleteById(Map<String,Object> params, ProviderContext context){
        params.remove("param1");
        TABLE_INF table = MapperHelper.getTable(context);
        COLUMN_INF pkColumn = table.getPkColumn();
        return SqlHelper.sql()
                .delete()
                .from(table.getTableName())
                .where(pkColumn.getColumn() + " = #{id}")
                .toStrWithLog(context, params);
    }

    public String deleteByIds(Map<String,Object> params, ProviderContext context){
        params.remove("param1");
        Object idList = params.get("idList");
        TABLE_INF table = MapperHelper.getTable(context);
        COLUMN_INF pkColumn = table.getPkColumn();
        return SqlHelper.sql()
                .delete()
                .from(table.getTableName())
                .where(pkColumn.getColumn() + " IN (" + getInWhere("idList", idList) + ")")
                .toStrWithLog(context, params);
    }

    public String delete(Map<String,Object> params, ProviderContext context){
        Querier<?> querier = (Querier<?>)params.get("querier");
        params.clear();
        TABLE_INF table = MapperHelper.getTable(context);
        BaseExample example = querier.getExample();
        return SqlHelper.sql()
                .delete()
                .from(table.getTableName())
                .where(getCondition(example, params))
                .toStrWithLog(context, params);
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
                .toStrWithLog(context, entity);
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
                .toStrWithLog(context, entity);
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

        return SqlHelper.sql()
                .update(table.getTableName())
                .set(setValues)
                .where(condition.toString())
                .toStrWithLog(context, params);
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

        return SqlHelper.sql()
                .update(table.getTableName())
                .set(setValues)
                .where(condition.toString())
                .toStrWithLog(context, params);
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

        return SqlHelper.sql()
                .update(table.getTableName())
                .set(setValues)
                .where(getCondition(example, params))
                .toStrWithLog(context, params);
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

        return SqlHelper.sql()
                .update(table.getTableName())
                .set(setValues)
                .where(getCondition(example, params))
                .toStrWithLog(context, params);
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
                    inList.append("#{").append(property).append("[").append(i).append("]},");
                }
                inWhere = inList.deleteCharAt(inList.length()-1).toString();
            }
        }
        return inWhere;
    }
}