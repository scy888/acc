package com.weshare.service.api.enums;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.service.api.enums
 * @date: 2021-05-18 15:09:14
 * @describe:
 */
public enum ResultEnum {

    SUCCESS("10000","成功"),
    FAILED("00000","失败");

    private String code;
    private String msg;

    ResultEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
