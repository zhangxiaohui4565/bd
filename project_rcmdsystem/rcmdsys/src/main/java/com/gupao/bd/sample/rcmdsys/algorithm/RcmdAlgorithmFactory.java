/**
 * 
 */
package com.gupao.bd.sample.rcmdsys.algorithm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author george
 *
 */
@Component
public class RcmdAlgorithmFactory {

    @Autowired
    private TopNAlgorithm topNAlgorithm;
    @Autowired
    private CwbtiabAlgorithm cwbtiabAlgorithm;
    @Autowired
    private ItemBasedCFAlgorithm itemBasedCFAlgorithm;
    
    enum AlgorithmType {
        TOP_N, CWBTIAB, ITEM_BASED_CF
    }
    
    public RcmdAlgorithm getAlgorithmByName(String name){
        AlgorithmType algorithmType = AlgorithmType.valueOf(name);
        if(algorithmType == null){
            return null;
        }
        switch (algorithmType) {
        case TOP_N:
            return topNAlgorithm;
        case CWBTIAB:
            return cwbtiabAlgorithm;
        case ITEM_BASED_CF:
            return itemBasedCFAlgorithm;
        default:
            return null;
        }
    }
    
    public static boolean isValidAlgorithmName(String name){
        AlgorithmType algorithmType = AlgorithmType.valueOf(name);
        if(algorithmType == null){
            return false;
        }
        return true;
    }
}
