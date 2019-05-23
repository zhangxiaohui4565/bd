package com.gupao.bd.eos.query.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 统计结果返回
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StatsResult {
    private List<Sums> summaries;
}
