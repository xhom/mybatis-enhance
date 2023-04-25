package com.vz.mybatis.enhance.common.mapper.qr;

import com.vz.mybatis.enhance.common.func.FFunction;
import com.vz.mybatis.enhance.common.mapper.hp.MethodRefHelper;
import com.vz.mybatis.enhance.common.mapper.hp.NameHelper;
import com.vz.mybatis.enhance.common.mapper.inf.ColumnINF;

import java.util.Collection;

/**
 * @author visy.wang
 * @description: 查询器
 * @date 2023/4/25 9:54
 */
public class Querier<T> {
    private final BaseExample example = new BaseExample();
    private final Criteria criteria = example.createCriteria();

    public static <T> Querier<T> query(){
        return new Querier<>();
    }

    public Querier<T> isNull(FFunction<T,?> function) {
        ColumnINF columnInfo = getColumnInfo(function);
        criteria.isNull(columnInfo.getColumn());
        return this;
    }

    public Querier<T> isNotNull(FFunction<T,?> function) {
        ColumnINF columnInfo = getColumnInfo(function);
        criteria.isNotNull(columnInfo.getColumn());
        return this;
    }

    public <V> Querier<T> eq(FFunction<T,V> function, V value) {
        ColumnINF columnInfo = getColumnInfo(function);
        System.out.println(columnInfo);
        criteria.eq(columnInfo.getColumn(), value, columnInfo.getProperty());
        return this;
    }

    public <V> Querier<T> neq(FFunction<T,V> function, V value) {
        ColumnINF columnInfo = getColumnInfo(function);
        criteria.neq(columnInfo.getColumn(), value, columnInfo.getProperty());
        return this;
    }

    public <V> Querier<T> gt(FFunction<T,V> function, V value) {
        ColumnINF columnInfo = getColumnInfo(function);
        criteria.gt(columnInfo.getColumn(), value, columnInfo.getProperty());
        return this;
    }

    public <V> Querier<T> gte(FFunction<T,V> function, V value) {
        ColumnINF columnInfo = getColumnInfo(function);
        criteria.gte(columnInfo.getColumn(), value, columnInfo.getProperty());
        return this;
    }

    public <V> Querier<T> lt(FFunction<T,V> function, V value) {
        ColumnINF columnInfo = getColumnInfo(function);
        criteria.lt(columnInfo.getColumn(), value, columnInfo.getProperty());
        return this;
    }

    public <V> Querier<T> lte(FFunction<T,V> function, V value) {
        ColumnINF columnInfo = getColumnInfo(function);
        criteria.lte(columnInfo.getColumn(), value, columnInfo.getProperty());
        return this;
    }

    public <V> Querier<T> like(FFunction<T,V> function, V value) {
        ColumnINF columnInfo = getColumnInfo(function);
        criteria.like(columnInfo.getColumn(), value, columnInfo.getProperty());
        return this;
    }

    public <V> Querier<T> likeLeft(FFunction<T,V> function, V value) {
        ColumnINF columnInfo = getColumnInfo(function);
        criteria.likeLeft(columnInfo.getColumn(), value, columnInfo.getProperty());
        return this;
    }

    public <V> Querier<T> likeRight(FFunction<T,V> function, V value) {
        ColumnINF columnInfo = getColumnInfo(function);
        criteria.likeRight(columnInfo.getColumn(), value, columnInfo.getProperty());
        return this;
    }

    public <V> Querier<T> in(FFunction<T,V> function, Collection<V> values) {
        ColumnINF columnInfo = getColumnInfo(function);
        criteria.in(columnInfo.getColumn(), values, columnInfo.getProperty());
        return this;
    }

    public <V> Querier<T> notIn(FFunction<T,V> function, Collection<V> values) {
        ColumnINF columnInfo = getColumnInfo(function);
        criteria.notIn(columnInfo.getColumn(), values, columnInfo.getProperty());
        return this;
    }

    public <V> Querier<T> between(FFunction<T,V> function, V value1, V value2) {
        ColumnINF columnInfo = getColumnInfo(function);
        criteria.between(columnInfo.getColumn(), value1, value2, columnInfo.getProperty());
        return this;
    }

    public <V> Querier<T> notBetween(FFunction<T,V> function, V value1, V value2) {
        ColumnINF columnInfo = getColumnInfo(function);
        criteria.notBetween(columnInfo.getColumn(), value1, value2, columnInfo.getProperty());
        return this;
    }

    private ColumnINF getColumnInfo(FFunction<T,?> function){
        String property = MethodRefHelper.getFieldName(function);
        ColumnINF column = new ColumnINF();
        column.setProperty(property);
        column.setColumn(NameHelper.camel2underline(property));
        return column;
    }

    public Querier<T> distinct(){
        example.setDistinct(true);
        return this;
    }

    public Querier<T> asc(FFunction<T,?> function){
        ColumnINF columnInfo = getColumnInfo(function);
        example.addOrderBy(columnInfo.getColumn(), true);
        return this;
    }

    public Querier<T> desc(FFunction<T,?> function){
        ColumnINF columnInfo = getColumnInfo(function);
        example.addOrderBy(columnInfo.getColumn(), false);
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
        return this;
    }

    public BaseExample getExample(){
        return example;
    }
}
