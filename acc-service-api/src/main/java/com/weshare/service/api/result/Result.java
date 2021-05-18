package com.weshare.service.api.result;

import com.weshare.service.api.enums.ResultEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.service.api.result
 * @date: 2021-05-18 15:14:54
 * @describe:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> {
    private String code;
    private String msg;
    private Boolean isFlag;
    private T data;

    public Result(Boolean isFlag) {
        this.isFlag = isFlag;
        this.code = isFlag ? ResultEnum.SUCCESS.getCode() : ResultEnum.FAILED.getCode();
        this.msg = isFlag ? ResultEnum.SUCCESS.getMsg() : ResultEnum.FAILED.getMsg();
    }

    public Result(Boolean isFlag, T data) {
        this.isFlag = isFlag;
        this.data = data;
        this.code = isFlag ? ResultEnum.SUCCESS.getCode() : ResultEnum.FAILED.getCode();
        this.msg = isFlag ? ResultEnum.SUCCESS.getMsg() : ResultEnum.FAILED.getMsg();
    }

    public static <T> Result result(Boolean isFlag) {
        Result<T> result = new Result(isFlag);
        return result;
    }

    public static <T> Result result(Boolean isFlag, String msg) {
        Result<T> result = new Result(isFlag);
        result.setMsg(msg);
        return result;
    }

    public static <T> Result result(Boolean isFlag, T data) {
        Result<T> result = new Result(isFlag, data);
        return result;
    }

    public static <T> Result result(Boolean isFlag, T data, String msg) {
        Result<T> result = new Result(isFlag, data);
        result.setMsg(msg);
        return result;
    }

    public static void main(String[] args) {
        System.out.println(Result.result(true));
        System.out.println(Result.result(true, "SUCCESS"));
        System.out.println(Result.result(false));
    }
}
