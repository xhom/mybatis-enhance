package com.vz.mybatis.enhance.common.mapper.qr;

import lombok.Data;
import java.util.Collection;

/**
 * 查询条件，单个字段的查询信息
 */
@Data
public class Criterion {
    /**
     * 列名（数据库字段）
     */
    private String column;
    /**
     * 属性名（Java实体）
     */
    private String property;
    /**
     * 查询值
     */
    private Object value;
    /**
     * 查询值2（BETWEEN才有）
     */
    private Object secondValue;
    /**
     * 查询条件，比如：”id > “
     */
    private String condition;
    /**
     * 查询条件中没有值
     */
    private boolean noValue;
    /**
     * 查询条件中有一个值
     */
    private boolean singleValue;
    /**
     * 查询条件中有两个值
     */
    private boolean betweenValue;
    /**
     * 查询条件中有一个值，但是的集合
     */
    private boolean listValue;


    protected Criterion(String column, String exp) {
        super();
        this.column = column;
        this.condition = column + exp;
        this.noValue = true;
    }

    protected Criterion(String column, String exp, Object value, String property) {
        super();
        this.column = column;
        this.property = property;
        this.condition = column + exp;
        this.value = value;
        if (value instanceof Collection) {
            this.listValue = true;
        } else {
            this.singleValue = true;
        }
    }

    protected Criterion(String column, String exp, Object value, Object secondValue, String property) {
        super();
        this.column = column;
        this.property = property;
        this.condition = column + exp;
        this.value = value;
        this.secondValue = secondValue;
        this.betweenValue = true;
    }
}