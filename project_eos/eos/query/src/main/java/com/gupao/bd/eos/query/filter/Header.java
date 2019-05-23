package com.gupao.bd.eos.query.filter;

import lombok.*;

/**
 * HTTP Header
 */
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class Header {
    private final String name;
    private final String value;
}
