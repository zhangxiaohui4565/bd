/**
 * 
 */
package com.gupao.bd.sample.rcmdsys.search;

/**
 * @author george
 *
 */
public enum MovieField {

    ID("id"),
    TAGS("tags"),
    NAME("name"),
    IMAGE_URL("image_url"),
    YEAR("year"),
    SCORE("score");
    
    private String esFieldName;
    
    private MovieField(String esFiledName){
        this.esFieldName = esFiledName;
    }

    public String getEsFieldName() {
        return esFieldName;
    }
    
}
