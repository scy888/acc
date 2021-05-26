package com.weshare.service.api.enums;

import lombok.Data;
import lombok.Getter;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.service.api.enums
 * @date: 2021-05-26 18:40:19
 * @describe:
 */
@Getter
public enum SettleTypeEnum {

    NORMAL_SETTLE("正常结清"),
    OVERDUE_SETTLE("逾期结清"),
    PRE_SETTLE("提前结清"),
    RETURN_SETTLE("退票结清");

    private String desc;

    SettleTypeEnum(String desc) {
        this.desc = desc;
    }
}
