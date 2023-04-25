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
public class TableINF {
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
    private ColumnINF primaryKey;
    /**
     * 所有列集合
     */
    private List<ColumnINF> columns = new ArrayList<>();

    public void addColumn(ColumnINF column){
        columns.add(column);
    }

    public String selectColumnsAsProperties(){
        StringBuilder selectSQL = new StringBuilder();
        columns.forEach(column -> {
            selectSQL.append(column.getColumn()).append(" AS ").append(column.getProperty()).append(",");
        });
        return selectSQL.deleteCharAt(selectSQL.length()-1).toString();
    }
}
