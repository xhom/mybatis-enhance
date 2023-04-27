package com.vz.mybatis.enhance.common.mapper.qr;

import com.vz.mybatis.enhance.common.func.FFunction;
import com.vz.mybatis.enhance.common.mapper.hp.MethodRefHelper;
import com.vz.mybatis.enhance.common.mapper.hp.NameHelper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
        return isNull(getProperty(function));
    }

    public Querier<T> isNull(String property) {
        Column column = getColumn(property);
        criteria.isNull(column.getColumn());
        return this;
    }

    public Querier<T> isNotNull(FFunction<T,?> function) {
        return isNotNull(getProperty(function));
    }

    public Querier<T> isNotNull(String property) {
        Column column = getColumn(property);
        criteria.isNotNull(column.getColumn());
        return this;
    }

    public Querier<T> eq(FFunction<T,?> function, Object value) {
        return eq(getProperty(function), value);
    }

    public Querier<T> eq(String property, Object value) {
        Column column = getColumn(property);
        criteria.eq(column.getColumn(), value, column.getProperty());
        return this;
    }

    public Querier<T> neq(FFunction<T,?> function, Object value) {
        return neq(getProperty(function), value);
    }

    public Querier<T> neq(String property, Object value) {
        Column column = getColumn(property);
        criteria.neq(column.getColumn(), value, column.getProperty());
        return this;
    }

    public Querier<T> gt(FFunction<T,?> function, Object value) {
        return gt(getProperty(function), value);
    }

    public Querier<T> gt(String property, Object value) {
        Column column = getColumn(property);
        criteria.gt(column.getColumn(), value, column.getProperty());
        return this;
    }

    public Querier<T> gte(FFunction<T,?> function, Object value) {
        return gte(getProperty(function), value);
    }

    public Querier<T> gte(String property, Object value) {
        Column column = getColumn(property);
        criteria.gte(column.getColumn(), value, column.getProperty());
        return this;
    }

    public Querier<T> lt(FFunction<T,?> function, Object value) {
        return lt(getProperty(function), value);
    }

    public Querier<T> lt(String property, Object value) {
        Column column = getColumn(property);
        criteria.lt(column.getColumn(), value, column.getProperty());
        return this;
    }

    public Querier<T> lte(FFunction<T,?> function, Object value) {
        return lte(getProperty(function), value);
    }

    public Querier<T> lte(String property, Object value) {
        Column column = getColumn(property);
        criteria.lte(column.getColumn(), value, column.getProperty());
        return this;
    }

    public Querier<T> like(FFunction<T,?> function, Object value) {
        return like(getProperty(function), value);
    }

    public Querier<T> like(String property, Object value) {
        Column column = getColumn(property);
        criteria.like(column.getColumn(), value, column.getProperty());
        return this;
    }

    public Querier<T> likeLeft(FFunction<T,?> function, Object value) {
        return likeLeft(getProperty(function), value);
    }

    public Querier<T> likeLeft(String property, Object value) {
        Column column = getColumn(property);
        criteria.likeLeft(column.getColumn(), value, column.getProperty());
        return this;
    }

    public Querier<T> likeRight(FFunction<T,?> function, Object value) {
        return likeRight(getProperty(function), value);
    }

    public Querier<T> likeRight(String property, Object value) {
        Column column = getColumn(property);
        criteria.likeRight(column.getColumn(), value, column.getProperty());
        return this;
    }

    public Querier<T> notLike(FFunction<T,?> function, Object value) {
        return notLike(getProperty(function), value);
    }

    public Querier<T> notLike(String property, Object value) {
        Column column = getColumn(property);
        criteria.notLike(column.getColumn(), value, column.getProperty());
        return this;
    }

    public Querier<T> notLikeLeft(FFunction<T,?> function, Object value) {
        return notLikeLeft(getProperty(function), value);
    }

    public Querier<T> notLikeLeft(String property, Object value) {
        Column column = getColumn(property);
        criteria.notLikeLeft(column.getColumn(), value, column.getProperty());
        return this;
    }

    public Querier<T> notLikeRight(FFunction<T,?> function, Object value) {
        return notLikeRight(getProperty(function), value);
    }

    public Querier<T> notLikeRight(String property, Object value) {
        Column column = getColumn(property);
        criteria.notLikeRight(column.getColumn(), value, column.getProperty());
        return this;
    }

    public Querier<T> in(FFunction<T,?> function, Collection<?> values) {
        return in(getProperty(function), values);
    }

    public Querier<T> in(String property, Collection<?> values) {
        Column column = getColumn(property);
        criteria.in(column.getColumn(), values, column.getProperty());
        return this;
    }

    public Querier<T> notIn(FFunction<T,?> function, Collection<?> values) {
        return notIn(getProperty(function), values);
    }

    public Querier<T> notIn(String property, Collection<?> values) {
        Column column = getColumn(property);
        criteria.notIn(column.getColumn(), values, column.getProperty());
        return this;
    }

    public Querier<T> between(FFunction<T,?> function, Object value1, Object value2) {
        return between(getProperty(function), value1, value2);
    }

    public Querier<T> between(String property, Object value1, Object value2) {
        Column column = getColumn(property);
        criteria.between(column.getColumn(), value1, value2, column.getProperty());
        return this;
    }

    public Querier<T> notBetween(FFunction<T,?> function, Object value1, Object value2) {
        return notBetween(getProperty(function), value1, value2);
    }

    public Querier<T> notBetween(String property, Object value1, Object value2) {
        Column column = getColumn(property);
        criteria.notBetween(column.getColumn(), value1, value2, column.getProperty());
        return this;
    }

    public Querier<T> asc(FFunction<T,?> function){
        return asc(getProperty(function));
    }

    public Querier<T> asc(String property){
        Column column = getColumn(property);
        example.addOrderBy(column.getColumn(), true);
        return this;
    }

    public Querier<T> desc(FFunction<T,?> function){
        return desc(getProperty(function));
    }

    public Querier<T> desc(String property){
        Column column = getColumn(property);
        example.addOrderBy(column.getColumn(), false);
        return this;
    }

    public Querier<T> distinct(){
        example.setDistinct(true);
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

    private String getProperty(FFunction<T,?> function){
        return MethodRefHelper.getFieldName(function);
    }

    private Column getColumn(String property){
        return Column.me(NameHelper.camel2underline(property), property);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class Column {
        private String column;
        private String property;
        public static Column me(String column, String property){
            return new Column(column, property);
        }
    }
}
