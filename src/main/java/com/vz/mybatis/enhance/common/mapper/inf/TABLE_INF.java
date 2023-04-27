package com.vz.mybatis.enhance.common.mapper.inf;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author visy.wang
 * @description: 表信息
 * @date 2023/4/24 14:08
 */
@Data
public class TABLE_INF {
    /**
     * 关联Java类Class
     */
    private Class<?> entityClass;
    /**
     * 表名
     */
    private String tableName;
    /**
     * 主键列
     */
    private COLUMN_INF pkColumn;
    /**
     * 所有列集合
     */
    private List<COLUMN_INF> columns = new ArrayList<>();

    public void addColumn(COLUMN_INF column){
        columns.add(column);
    }

    public String allColumns(){
        return allColumns(true);
    }

    public String allColumns(boolean isAlias){
        StringBuilder selectColumns = new StringBuilder();
        columns.forEach(column -> {
            selectColumns.append(column.getColumn());
            if(isAlias){
                selectColumns.append(" AS ").append(column.getProperty());
            }
            selectColumns.append(",");
        });
        return selectColumns.deleteCharAt(selectColumns.length()-1).toString();
    }
}
