package com.vz.mybatis.enhance.common.mapper.inf;

import lombok.Data;

import java.lang.reflect.Field;

/**
 * @author visy.wang
 * @description: 列信息
 * @date 2023/4/24 14:57
 */
@Data
public class COLUMN_INF {
    /**
     * 列类型
     */
    private Class<?> fieldClass;
    /**
     * 是否主键
     */
    private Boolean isPK = false;
    /**
     * 列名
     */
    private String column;
    /**
     * 对象属性名
     */
    private String property;
    /**
     * 反射属性对象
     */
    private Field field;

}
