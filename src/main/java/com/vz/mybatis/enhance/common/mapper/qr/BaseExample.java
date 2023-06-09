package com.vz.mybatis.enhance.common.mapper.qr;

import java.util.*;

/**
 * @author visy.wang
 * @description: 基础查询构建类
 * @date 2023/4/24 13:01
 */
public class BaseExample {
    /**
     * 是否在SELECT时去重，默认false
     */
    protected boolean distinct;
    /**
     * 分页子句（不含LIMIT关键字）
     */
    protected String limitClause;
    /**
     * 排序字段集合 Map<字段名，排序方向ASC或DESC>
     */
    protected Map<String,String> orderByMap = new LinkedHashMap<>();

    protected List<Criteria> criteriaList;

    public BaseExample() {
        criteriaList = new ArrayList<>();
    }

    public void addOrderBy(String column, boolean isAsc) {
        orderByMap.put(column, isAsc?"ASC":"DESC");
    }

    public String getOrderByClause() {
        if(Objects.isNull(orderByMap) || orderByMap.isEmpty()){
            return "";
        }
        StringBuilder orderByClause = new StringBuilder();
        orderByMap.forEach((column, direction) -> {
            orderByClause.append(column).append(" ").append(direction).append(",");
        });
        return orderByClause.deleteCharAt(orderByClause.length()-1).toString();
    }

    public String getLimitClause() {
        return Objects.isNull(limitClause) ? "" : limitClause;
    }

    public void setLimitClause(String limitClause) {
        this.limitClause = limitClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean getDistinct() {
        return distinct;
    }

    public List<Criteria> getCriteriaList() {
        return criteriaList;
    }

    public void or(Criteria criteria) {
        criteriaList.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        criteriaList.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (criteriaList.size() == 0) {
            criteriaList.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        return new Criteria();
    }

    public void clear() {
        criteriaList.clear();
        distinct = false;
        orderByMap.clear();
        limitClause = null;
    }

}
