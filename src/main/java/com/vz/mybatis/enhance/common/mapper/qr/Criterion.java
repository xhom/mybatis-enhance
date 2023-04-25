package com.vz.mybatis.enhance.common.mapper.qr;

import lombok.Data;
import java.util.Collection;

@Data
public class Criterion {
    private String column;

    private String property;

    private Object value;

    private Object secondValue;

    private String condition;

    private boolean noValue;

    private boolean singleValue;

    private boolean betweenValue;

    private boolean listValue;


    protected Criterion(String column, String express) {
        super();
        this.column = column;
        this.condition = column + express;
        this.noValue = true;
    }

    protected Criterion(String column, String express, Object value, String property) {
        super();
        this.column = column;
        this.property = property;
        this.condition = column + express;
        this.value = value;
        if (value instanceof Collection) {
            this.listValue = true;
        } else {
            this.singleValue = true;
        }
    }

    protected Criterion(String column, String express, Object value, Object secondValue, String property) {
        super();
        this.column = column;
        this.property = property;
        this.condition = column + express;
        this.value = value;
        this.secondValue = secondValue;
        this.betweenValue = true;
    }
}