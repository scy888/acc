package com.weshare.service.api.enums;

import lombok.Getter;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.service.api.enums
 * @date: 2021-05-26 18:36:15
 * @describe:
 */
@Getter
public enum AssetStatusEnum {

    NORMAL("正常还款"),
    OVERDUE("逾期"),
    SETTLED("已结清");

    private String desc;

    AssetStatusEnum(String desc) {
        this.desc = desc;
    }
}
