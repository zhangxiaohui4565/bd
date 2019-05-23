package com.gupao.bd.sample.rcmdsys.controller;

/**
 * @author george
 *
 */
public enum RcmdType {

	VISITOR_HOMEPAGE,
	SINGLE_MOVIE_PAGE,
	USER_HOMEPAGE;
	
	public static RcmdType geRcmdType(String typeStr){
	    for(RcmdType type :RcmdType.values()){
	        if(type.name().equals(typeStr)){
	            return type;
	        }
	    }
	    return null;
	}
}
