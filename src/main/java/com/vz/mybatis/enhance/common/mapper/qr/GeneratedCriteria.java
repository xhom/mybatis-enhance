package com.vz.mybatis.enhance.common.mapper.qr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GeneratedCriteria {
    protected List<Criterion> criteria;

    protected GeneratedCriteria() {
        super();
        criteria = new ArrayList<>();
    }

    public boolean isValid() {
        return criteria.size() > 0;
    }

    public List<Criterion> getAllCriteria() {
        return criteria;
    }

    protected void addCriterion(String column, String exp) {
        criteria.add(new Criterion(column, exp));
    }

    protected void addCriterion(String column, String exp, Object value, String property) {
        if (value == null) {
            throw new RuntimeException("Value for " + property + " cannot be null");
        }
        criteria.add(new Criterion(column, exp, value, property));
    }

    protected void addCriterion(String column, String exp, Object value1, Object value2, String property) {
        if (value1 == null || value2 == null) {
            throw new RuntimeException("Between values for " + property + " cannot be null");
        }
        criteria.add(new Criterion(column, exp, value1, value2, property));
    }

    public Criteria isNull(String column) {
        addCriterion(column, " IS NULL ");
        return (Criteria) this;
    }

    public Criteria isNotNull(String column) {
        addCriterion(column, " IS NOT NULL ");
        return (Criteria) this;
    }

    public Criteria eq(String column, Object value, String property) {
        addCriterion(column, " = ", value, property);
        return (Criteria) this;
    }

    public Criteria neq(String column, Object value, String property) {
        addCriterion(column, " <> ", value, property);
        return (Criteria) this;
    }

    public Criteria gt(String column, Object value, String property) {
        addCriterion(column, " > ", value, property);
        return (Criteria) this;
    }

    public Criteria gte(String column, Object value, String property) {
        addCriterion(column, " >= ", value, property);
        return (Criteria) this;
    }

    public Criteria lt(String column, Object value, String property) {
        addCriterion(column, " < ", value, property);
        return (Criteria) this;
    }

    public Criteria lte(String column, Object value, String property) {
        addCriterion(column, " <= ", value, property);
        return (Criteria) this;
    }

    public Criteria like(String column, Object value, String property) {
        addCriterion(column, " LIKE ", "%"+value+"%", property);
        return (Criteria) this;
    }

    public Criteria likeLeft(String column, Object value, String property) {
        addCriterion(column, " LIKE ", value+"%", property);
        return (Criteria) this;
    }

    public Criteria likeRight(String column, Object value, String property) {
        addCriterion(column, " LIKE ", "%"+value, property);
        return (Criteria) this;
    }

    public Criteria notLike(String column, Object value, String property) {
        addCriterion(column, " NOT LIKE ", "%"+value+"%", property);
        return (Criteria) this;
    }

    public Criteria notLikeLeft(String column, Object value, String property) {
        addCriterion(column, " NOT LIKE ", value+"%", property);
        return (Criteria) this;
    }

    public Criteria notLikeRight(String column, Object value, String property) {
        addCriterion(column, " NOT LIKE ", "%"+value, property);
        return (Criteria) this;
    }

    public Criteria in(String column, Collection<?> values, String property) {
        addCriterion(column, " IN ", values, property);
        return (Criteria) this;
    }

    public Criteria notIn(String column, Collection<?> values, String property) {
        addCriterion(column, " NOT IN ", values, property);
        return (Criteria) this;
    }

    public Criteria between(String column, Object value1, Object value2, String property) {
        addCriterion(column, " BETWEEN ", value1, value2, property);
        return (Criteria) this;
    }

    public Criteria notBetween(String column, Object value1, Object value2, String property) {
        addCriterion(column, " NOT BETWEEN ", value1, value2, property);
        return (Criteria) this;
    }
}
