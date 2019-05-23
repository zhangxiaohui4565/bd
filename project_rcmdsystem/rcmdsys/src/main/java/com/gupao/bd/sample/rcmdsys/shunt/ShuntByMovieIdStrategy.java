/**
 * 
 */
package com.gupao.bd.sample.rcmdsys.shunt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.gupao.bd.sample.rcmdsys.algorithm.RcmdAlgorithmFactory;
import com.gupao.bd.sample.rcmdsys.controller.RcmdContext;
import com.gupao.bd.sample.rcmdsys.util.JsonHelper;

/**
 * @author george
 * 根据电影id的最后两位分流
 */
public class ShuntByMovieIdStrategy extends BaseStrategy {

    private Map<IdRange, String> setting;
    
    /**
     * @param initParamInJson
     */
    public ShuntByMovieIdStrategy(String initParamInJson) {
        super(initParamInJson);
        if(StringUtils.isEmpty(initParamInJson)){
            throw new RuntimeException("分流策略配置参数不能为空");
        }
        try{
            setting = new HashMap<>();
            List<Map<String, String>> confObjs = JsonHelper.fromJson(initParamInJson, List.class);
            for(Map<String, String> confObj : confObjs){
                String idRangeStr = confObj.get("idRange");
                String[] upAndDown = idRangeStr.split(":");
                IdRange range = new IdRange(Integer.valueOf(upAndDown[0]), Integer.valueOf(upAndDown[1]));
                String algorithmName = confObj.get("algorithmName");
                if(!RcmdAlgorithmFactory.isValidAlgorithmName(algorithmName)){
                    throw new RuntimeException("不正确的算法名称：" + algorithmName);
                }
                setting.put(range, algorithmName);
            }
        } catch(Exception e){
            throw new RuntimeException("分流策略配置不正确：" + initParamInJson + ". 异常信息：" + e.getMessage());
        }
    }

    @Override
    public String getRcmdAlgorithm(RcmdContext context) {
        Long movieId = context.getCurrentMovieId();
        if(movieId != null){
            int lastTwoDigit = Long.valueOf(movieId % 100).intValue();
            for(IdRange range : setting.keySet()){
                if(range.isInScope(lastTwoDigit)){
                    return setting.get(range);
                }
            }
        }
        return null;
    }

}

class IdRange{
    private int up;
    private int down;
    
    public IdRange(int down, int up) {
        this.up = up;
        this.down = down;
        if(down>up){
            throw new RuntimeException("下区间不能超过上区间");
        }
    }
    
    public boolean isInScope(int value){
        return (value>=down&&value<=up);
    }
    public int getUp() {
        return up;
    }
    public void setUp(int up) {
        this.up = up;
    }
    public int getDown() {
        return down;
    }
    public void setDown(int down) {
        this.down = down;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + down;
        result = prime * result + up;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        IdRange other = (IdRange) obj;
        if (down != other.down)
            return false;
        if (up != other.up)
            return false;
        return true;
    }
    
}