package com.weshare.service.api.vo;

import lombok.Data;
import lombok.Value;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.service.api.vo
 * @date: 2021-06-02 15:50:30
 * @describe:
 */
@Value
public class Tuple3<T, R, U> {

    private T first;
    private R second;
    private U third;

    public static <T, R, U> Tuple3<T, R, U> of(T t, R r, U u) {
        return new Tuple3<>(t, r, u);
    }
}
