package com.weshare.service.api.enums;

import lombok.Getter;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.service.api.enums
 * @date: 2021-06-28 10:05:48
 * @describe:
 */

@Getter
public enum DataCheckType {

    校验借款合同金额等于还款计划应还本金之和("借款合同金额=sum(还款计划本金)"),
    校验还款主信息剩余本金等于还款计划剩余本金之和("剩余本金=sum(还款计划的剩余本金之和)"),
    检验还款计划的实还金额等于还款流水或实还之和("sum(还款计划已还金额)=sum(还款流水之和或实还之和)"),
    校验还款流水表流水号等于实还表流流水号("还款流水表flowSn=实还表flowSn"),
    校验用户还款主信息期次等于还款计划总期次("还款计划期次的校验"),
    校验还款主信息表的借据状态和还款计划表的期次状态一致性("借据状态和期次状态一致性"),
    校验还款计划是否跳期("校验还款计划是否跳期");


    private String desc;

    DataCheckType(String desc) {
        this.desc = desc;
    }

}
