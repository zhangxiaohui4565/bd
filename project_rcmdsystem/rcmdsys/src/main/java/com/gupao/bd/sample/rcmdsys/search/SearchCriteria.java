/**
 * 
 */
package com.gupao.bd.sample.rcmdsys.search;

/**
 * @author george
 *
 */
public class SearchCriteria {

    public enum Operator {
        IN, 
        EQUAL,
        NOT_EQUAL,
        CONTAIN
    }

    private MovieField field;
    private String value;
    private Operator operator;

    public SearchCriteria(MovieField field, String value, Operator operator) {
        this.field = field;
        this.value = value;
        this.operator = operator;
    }

    public MovieField getField() {
        return field;
    }

    public void setField(MovieField field) {
        this.field = field;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    @Override
    public String toString() {
        return "SearchCriteria [field=" + field + ", value=" + value + ", operator=" + operator + "]";
    }
}
