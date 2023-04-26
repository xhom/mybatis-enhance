package com.vz.mybatis.enhance.common.mapper.qr;

import com.vz.mybatis.enhance.common.func.FFunction;
import com.vz.mybatis.enhance.common.mapper.hp.MethodRefHelper;
import com.vz.mybatis.enhance.common.mapper.hp.NameHelper;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Collection;

/**
 * @author visy.wang
 * @description: 查询器
 * @date 2023/4/25 9:54
 */
public class Querier<T> {
    private final BaseExample example = new BaseExample();
    private Criteria criteria = example.createCriteria();

    public static <T> Querier<T> query(){
        return new Querier<>();
    }

    public Querier<T> isNull(FFunction<T,?> function) {
        Column column = getColumn(function);
        criteria.isNull(column.getColumn());
        return this;
    }

    public Querier<T> isNotNull(FFunction<T,?> function) {
        Column column = getColumn(function);
        criteria.isNotNull(column.getColumn());
        return this;
    }

    public <V> Querier<T> eq(FFunction<T,V> function, V value) {
        Column column = getColumn(function);
        criteria.eq(column.getColumn(), value, column.getProperty());
        return this;
    }

    public <V> Querier<T> neq(FFunction<T,V> function, V value) {
        Column column = getColumn(function);
        criteria.neq(column.getColumn(), value, column.getProperty());
        return this;
    }

    public <V> Querier<T> gt(FFunction<T,V> function, V value) {
        Column column = getColumn(function);
        criteria.gt(column.getColumn(), value, column.getProperty());
        return this;
    }

    public <V> Querier<T> gte(FFunction<T,V> function, V value) {
        Column column = getColumn(function);
        criteria.gte(column.getColumn(), value, column.getProperty());
        return this;
    }

    public <V> Querier<T> lt(FFunction<T,V> function, V value) {
        Column column = getColumn(function);
        criteria.lt(column.getColumn(), value, column.getProperty());
        return this;
    }

    public <V> Querier<T> lte(FFunction<T,V> function, V value) {
        Column column = getColumn(function);
        criteria.lte(column.getColumn(), value, column.getProperty());
        return this;
    }

    public <V> Querier<T> like(FFunction<T,V> function, V value) {
        Column column = getColumn(function);
        criteria.like(column.getColumn(), value, column.getProperty());
        return this;
    }

    public <V> Querier<T> likeLeft(FFunction<T,V> function, V value) {
        Column column = getColumn(function);
        criteria.likeLeft(column.getColumn(), value, column.getProperty());
        return this;
    }

    public <V> Querier<T> likeRight(FFunction<T,V> function, V value) {
        Column column = getColumn(function);
        criteria.likeRight(column.getColumn(), value, column.getProperty());
        return this;
    }

    public <V> Querier<T> in(FFunction<T,V> function, Collection<V> values) {
        Column column = getColumn(function);
        criteria.in(column.getColumn(), values, column.getProperty());
        return this;
    }

    public <V> Querier<T> notIn(FFunction<T,V> function, Collection<V> values) {
        Column column = getColumn(function);
        criteria.notIn(column.getColumn(), values, column.getProperty());
        return this;
    }

    public <V> Querier<T> between(FFunction<T,V> function, V value1, V value2) {
        Column column = getColumn(function);
        criteria.between(column.getColumn(), value1, value2, column.getProperty());
        return this;
    }

    public <V> Querier<T> notBetween(FFunction<T,V> function, V value1, V value2) {
        Column column = getColumn(function);
        criteria.notBetween(column.getColumn(), value1, value2, column.getProperty());
        return this;
    }

    public Querier<T> distinct(){
        example.setDistinct(true);
        return this;
    }

    public Querier<T> asc(FFunction<T,?> function){
        Column column = getColumn(function);
        example.addOrderBy(column.getColumn(), true);
        return this;
    }

    public Querier<T> desc(FFunction<T,?> function){
        Column column = getColumn(function);
        example.addOrderBy(column.getColumn(), false);
        return this;
    }

    public Querier<T> limit(int pageSize){
        return limit(1, pageSize);
    }

    public Querier<T> limit(int pageNo, int pageSize){
        pageNo = Math.max(pageNo, 1);
        pageSize = Math.max(pageSize, 0);
        int offset = (pageNo-1) * pageSize;
        example.setLimitClause(offset + "," + pageSize);
        return this;
    }

    public Querier<T> clear(){
        example.clear();
        criteria = example.createCriteria();
        return this;
    }

    public BaseExample getExample(){
        return example;
    }

    private Column getColumn(FFunction<T,?> function){
        String property = MethodRefHelper.getFieldName(function);
        return Column.me(NameHelper.camel2underline(property), property);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class Column {
        private String column;
        private String property;
        public static Column me(String c, String p){
            return new Column(c, p);
        }
    }
}
