package com.weshare.service.api.vo;

import lombok.Value;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.service.api.vo
 * @date: 2021-06-03 13:56:34
 * @describe:
 */
@Value
public class Tuple2<T, R> {

    private T first;
    private R second;

    public static <T, R> Tuple2<T, R> of(T t, R r) {
        return new Tuple2<>(t, r);
    }
}
