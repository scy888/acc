package common;

import java.io.Serializable;

/**
 * @author: scyang
 * @date: 2019-09-03 21:29:54
 */
public class ResponeData<T> implements Serializable {
    private boolean flag;
    private Integer code;
    private String msg;
    private T data;

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public ResponeData() {
    }

    public ResponeData(boolean flag, Integer code, String msg, T data) {
        this.flag = flag;
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public ResponeData(boolean flag, Integer code, String msg) {
        this.flag = flag;
        this.code = code;
        this.msg = msg;
    }
}
