package com.vz.mybatis.enhance.common.mapper.hp;

import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author visy.wang
 * @description: SQL助手（生成SQL语句）
 * @date 2023/4/24 17:39
 */
public class SqlHelper {
    private final StringBuilder sb = new StringBuilder();

    public static SqlHelper sql(){
        return new SqlHelper();
    }

    public SqlHelper select(){
        return select("*");
    }

    public SqlHelper select(String columns){
        return select(columns, false);
    }

    public SqlHelper select(String columns, boolean isDistinct){
        if(isDistinct){
            sb.append("SELECT DISTINCT ");
        }else{
            sb.append("SELECT ");
        }
        sb.append(columns);
        return this;
    }

    public SqlHelper count(){
        return count("*");
    }

    public SqlHelper count(String column){
        return count(column, false);
    }

    public SqlHelper count(String column, boolean isDistinct){
        sb.append("SELECT COUNT(");
        if(isDistinct){
            sb.append(" DISTINCT ");
        }
        sb.append(column).append(")");
        return this;
    }

    public SqlHelper delete(){
        sb.append("DELETE");
        return this;
    }

    public SqlHelper insert(String tableName){
        sb.append("INSERT INTO ").append(tableName);
        return this;
    }

    public SqlHelper values(List<String> values){
        sb.append(" VALUES (").append(String.join(",", values)).append(")");
        return this;
    }

    public SqlHelper values(List<String> columns, List<String> values){
        sb.append(" (").append(String.join(",", columns)).append(")")
                .append(" VALUES (").append(String.join(",", values)).append(")");
        return this;
    }

    public SqlHelper update(String tableName){
        sb.append("UPDATE ").append(tableName);
        return this;
    }

    public SqlHelper set(Map<String,String> setValues){
        sb.append(" SET ");
        setValues.forEach((column,value) -> {
            sb.append(column).append("=").append(value).append(",");
        });
        sb.deleteCharAt(sb.length() - 1);
        return this;
    }

    public SqlHelper from(String tableName){
        sb.append(" FROM ").append(tableName);
        return this;
    }

    public SqlHelper where(String conditions){
        if(StringUtils.hasText(conditions)){
            sb.append(" WHERE ").append(conditions);
        }
        return this;
    }

    public SqlHelper orderBy(String orderClause){
        if(StringUtils.hasText(orderClause)){
            sb.append(" ORDER BY ").append(orderClause);
        }
        return this;
    }

    public SqlHelper limit(String LimitClause){
        if(StringUtils.hasText(LimitClause)){
            sb.append(" LIMIT ").append(LimitClause);
        }
        return this;
    }

    public String toStr(){
        return sb.toString();
    }

    public String toStr(Consumer<String> consumer){
        String sql = toStr();
        consumer.accept(sql);
        return sql;
    }
}
