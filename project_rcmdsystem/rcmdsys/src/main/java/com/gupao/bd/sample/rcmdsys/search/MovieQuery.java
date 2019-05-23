/**
 * 
 */
package com.gupao.bd.sample.rcmdsys.search;

import java.util.List;

/**
 * @author george
 *
 */
public class MovieQuery {

    private int start;
    private int fetchSize;
    private List<SearchCriteria> filterCriterias;
    private List<SearchCriteria> searchCriterias;
    private List<SearchOrder> searchOrders;

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getFetchSize() {
        return fetchSize;
    }

    public void setFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
    }

    public List<SearchCriteria> getFilterCriterias() {
        return filterCriterias;
    }

    public void setFilterCriterias(List<SearchCriteria> filterCriterias) {
        this.filterCriterias = filterCriterias;
    }

    public List<SearchCriteria> getSearchCriterias() {
        return searchCriterias;
    }

    public void setSearchCriterias(List<SearchCriteria> searchCriterias) {
        this.searchCriterias = searchCriterias;
    }

    public List<SearchOrder> getSearchOrders() {
        return searchOrders;
    }

    public void setSearchOrders(List<SearchOrder> searchOrders) {
        this.searchOrders = searchOrders;
    }

}
