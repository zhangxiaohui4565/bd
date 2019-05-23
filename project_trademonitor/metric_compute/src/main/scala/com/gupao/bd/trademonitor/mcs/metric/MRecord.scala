package com.gupao.bd.trademonitor.mcs.metric

import com.alibaba.fastjson.JSONObject

/**
  * 计算后的统计指标
  */
case class MRecord(mtype: MType, key: String, value: AnyVal, dt: String) {

  val recordOfJson: JSONObject = new JSONObject()

  // 指标类型：全站累计成交额（site)、各城市累计注册人数(city)、各产品分时成交额(prd)、新老客交易用户数(userType)
  recordOfJson.put("metric", mtype.name)

  // 时间点
  recordOfJson.put("dt", dt)

  // 指标名称,如产品名称或城市名称
  recordOfJson.put("key", key)

  // 指标值,如注册人数
  recordOfJson.put("value", value)

  override def toString: String = recordOfJson.toJSONString

}
