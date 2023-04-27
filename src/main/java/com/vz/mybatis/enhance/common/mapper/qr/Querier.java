package com.vz.mybatis.enhance.common.mapper.qr;

import com.vz.mybatis.enhance.common.func.FFunction;
import com.vz.mybatis.enhance.common.mapper.hp.MethodRefHelper;
import com.vz.mybatis.enhance.common.mapper.hp.NameHelper;

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
        criteria.isNull(toColumn(property));
        return this;
    }

    public Querier<T> isNotNull(FFunction<T,?> function) {
        return isNotNull(getProperty(function));
    }

    public Querier<T> isNotNull(String property) {
        criteria.isNotNull(toColumn(property));
        return this;
    }

    public Querier<T> eq(FFunction<T,?> function, Object value) {
        return eq(getProperty(function), value);
    }

    public Querier<T> eq(String property, Object value) {
        criteria.eq(toColumn(property), value, property);
        return this;
    }

    public Querier<T> neq(FFunction<T,?> function, Object value) {
        return neq(getProperty(function), value);
    }

    public Querier<T> neq(String property, Object value) {
        criteria.neq(toColumn(property), value, property);
        return this;
    }

    public Querier<T> gt(FFunction<T,?> function, Object value) {
        return gt(getProperty(function), value);
    }

    public Querier<T> gt(String property, Object value) {
        criteria.gt(toColumn(property), value, property);
        return this;
    }

    public Querier<T> gte(FFunction<T,?> function, Object value) {
        return gte(getProperty(function), value);
    }

    public Querier<T> gte(String property, Object value) {
        criteria.gte(toColumn(property), value, property);
        return this;
    }

    public Querier<T> lt(FFunction<T,?> function, Object value) {
        return lt(getProperty(function), value);
    }

    public Querier<T> lt(String property, Object value) {
        criteria.lt(toColumn(property), value, property);
        return this;
    }

    public Querier<T> lte(FFunction<T,?> function, Object value) {
        return lte(getProperty(function), value);
    }

    public Querier<T> lte(String property, Object value) {
        criteria.lte(toColumn(property), value, property);
        return this;
    }

    public Querier<T> like(FFunction<T,?> function, Object value) {
        return like(getProperty(function), value);
    }

    public Querier<T> like(String property, Object value) {
        criteria.like(toColumn(property), value, property);
        return this;
    }

    public Querier<T> likeLeft(FFunction<T,?> function, Object value) {
        return likeLeft(getProperty(function), value);
    }

    public Querier<T> likeLeft(String property, Object value) {
        criteria.likeLeft(toColumn(property), value, property);
        return this;
    }

    public Querier<T> likeRight(FFunction<T,?> function, Object value) {
        return likeRight(getProperty(function), value);
    }

    public Querier<T> likeRight(String property, Object value) {
        criteria.likeRight(toColumn(property), value, property);
        return this;
    }

    public Querier<T> notLike(FFunction<T,?> function, Object value) {
        return notLike(getProperty(function), value);
    }

    public Querier<T> notLike(String property, Object value) {
        criteria.notLike(toColumn(property), value, property);
        return this;
    }

    public Querier<T> notLikeLeft(FFunction<T,?> function, Object value) {
        return notLikeLeft(getProperty(function), value);
    }

    public Querier<T> notLikeLeft(String property, Object value) {
        criteria.notLikeLeft(toColumn(property), value, property);
        return this;
    }

    public Querier<T> notLikeRight(FFunction<T,?> function, Object value) {
        return notLikeRight(getProperty(function), value);
    }

    public Querier<T> notLikeRight(String property, Object value) {
        criteria.notLikeRight(toColumn(property), value, property);
        return this;
    }

    public Querier<T> in(FFunction<T,?> function, Collection<?> values) {
        return in(getProperty(function), values);
    }

    public Querier<T> in(String property, Collection<?> values) {
        criteria.in(toColumn(property), values, property);
        return this;
    }

    public Querier<T> notIn(FFunction<T,?> function, Collection<?> values) {
        return notIn(getProperty(function), values);
    }

    public Querier<T> notIn(String property, Collection<?> values) {
        criteria.notIn(toColumn(property), values, property);
        return this;
    }

    public Querier<T> between(FFunction<T,?> function, Object value1, Object value2) {
        return between(getProperty(function), value1, value2);
    }

    public Querier<T> between(String property, Object value1, Object value2) {
        criteria.between(toColumn(property), value1, value2, property);
        return this;
    }

    public Querier<T> notBetween(FFunction<T,?> function, Object value1, Object value2) {
        return notBetween(getProperty(function), value1, value2);
    }

    public Querier<T> notBetween(String property, Object value1, Object value2) {
        criteria.notBetween(toColumn(property), value1, value2, property);
        return this;
    }

    public Querier<T> asc(FFunction<T,?> function){
        return asc(getProperty(function));
    }

    public Querier<T> asc(String property){
        example.addOrderBy(toColumn(property), true);
        return this;
    }

    public Querier<T> desc(FFunction<T,?> function){
        return desc(getProperty(function));
    }

    public Querier<T> desc(String property){
        example.addOrderBy(toColumn(property), false);
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

    private String toColumn(String property){
        return NameHelper.camel2underline(property);
    }
}
