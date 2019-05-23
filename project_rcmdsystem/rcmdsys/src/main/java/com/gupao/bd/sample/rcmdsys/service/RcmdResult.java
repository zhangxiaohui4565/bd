/**
 * 
 */
package com.gupao.bd.sample.rcmdsys.service;

import java.util.List;

import com.gupao.bd.sample.rcmdsys.controller.MovieVO;

/**
 * @author liubo
 *
 */
public class RcmdResult {

    private List<MovieVO> rcmdItems;
    private String rcmdId;
    private String rcmdAlgorithm;
    
    public List<MovieVO> getRcmdItems() {
        return rcmdItems;
    }
    public void setRcmdItems(List<MovieVO> rcmdItems) {
        this.rcmdItems = rcmdItems;
    }
    public String getRcmdId() {
        return rcmdId;
    }
    public void setRcmdId(String rcmdId) {
        this.rcmdId = rcmdId;
    }
    public String getRcmdAlgorithm() {
        return rcmdAlgorithm;
    }
    public void setRcmdAlgorithm(String rcmdAlgorithm) {
        this.rcmdAlgorithm = rcmdAlgorithm;
    }
}
