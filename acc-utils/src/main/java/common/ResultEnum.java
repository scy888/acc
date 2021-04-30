package common;

import java.io.Serializable;

/**
 * @author: scyang
 * @program: ssm_super
 * @date: 2019-09-11 20:55:17
 */
public enum ResultEnum implements Serializable {
    /**
     * @Description: 枚举类，构造方法私有
     * @methodName: 
     * @Param: 
     * @return: 
     * @Author: scyang
     * @Date: 2019/9/11 21:35
     */
     
    ADD_SUCCESS(200, "增加成功"),
    ADD_FALSE(201, "增加失败"),
    DELETE_SUCCESS(300,"删除成功"),
    DELETE_FALSE(301,"删除失败"),
    UPDATE_SUCCESS(400,"修改成功"),
    UPDATE_FALSE(401,"修改失败"),
    QUERY_SUCCESS(500,"查询成功"),
    QUERY_FALSE(501,"查询失败");
    
    private Integer statusCode;
    private String message;

   ResultEnum(Integer statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }
}
