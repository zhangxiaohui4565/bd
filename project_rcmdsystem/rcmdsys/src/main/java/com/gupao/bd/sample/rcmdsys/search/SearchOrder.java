/**
 * 
 */
package com.gupao.bd.sample.rcmdsys.search;

/**
 * @author george
 *
 */
public class SearchOrder {

    enum OrderType {
        ASC, DESC
    }

    private MovieField orderField;
    private OrderType orderType;

    public SearchOrder(MovieField orderField, OrderType orderType) {
        this.orderField = orderField;
        this.orderType = orderType;
    }

    public MovieField getOrderField() {
        return orderField;
    }

    public void setOrderField(MovieField orderField) {
        this.orderField = orderField;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

}
