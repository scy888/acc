package com.weshare.loan.enums;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.loan.enums
 * @date: 2021-05-19 10:59:22
 * @describe:
 */
public enum HashPrefix {

   用户姓名哈希值("a_"),
   证件号码哈希值("b_"),
   手机号码哈希值("c_"),
   车牌号码哈希值("d_");


    private String prefix;

    HashPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }
}
