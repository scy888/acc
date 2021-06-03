package com.weshare.service.api.vo;

import lombok.Value;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.service.api.vo
 * @date: 2021-06-02 15:50:30
 * @describe:
 */
@Value
public class Tuple4<T, R, U,V> {

    private T first;
    private R second;
    private U third;
    private V fourth;

    public static <T, R, U,V> Tuple4<T, R, U,V> of(T t, R r, U u,V v) {
        return new Tuple4<>(t, r, u, v);
    }
}
