package com.gupao.bd.eos.query.vo;

import lombok.*;

/**
 * 统计结果
 */
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor()
public class Sums {
    @NonNull
    private String field;
    @NonNull
    private long sum;

    private Sums subSum;
}
