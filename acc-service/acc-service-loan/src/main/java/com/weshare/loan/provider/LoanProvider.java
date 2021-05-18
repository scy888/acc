package com.weshare.loan.provider;

import com.alibaba.fastjson.JSON;
import com.weshare.loan.dao.LoanDao;
import com.weshare.loan.entity.UserBase;
import com.weshare.service.api.client.LoanClient;
import com.weshare.service.api.entity.UserBaseReq;
import com.weshare.service.api.result.Result;
import common.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.loan.provider
 * @date: 2021-05-18 16:11:46
 * @describe:
 */
@RestController
@Slf4j
public class LoanProvider implements LoanClient {

    @Autowired
    private LoanDao loanDao;

    @Override
    public Result saveListUserBase(List<UserBaseReq> userBaseReqList) throws Exception {
        //log.info("userBaseReqList:{}", JsonUtil.toJson(userBaseReqList, true));
        //批量保存用户信息,先根据借据号查询是否有,有就更新,没有就新增

        List<UserBase> dbUserBaserList = loanDao.findUserBaseByDueBillNo(userBaseReqList.stream().map(UserBaseReq::getDueBillNo)
                .collect(Collectors.toList()));
        Map<String, UserBase> map = dbUserBaserList.stream().collect(Collectors.toMap(UserBase::getDueBillNo, Function.identity()));
        List<UserBaseReq> insertList = new ArrayList<>();
        List<UserBaseReq> updateList = new ArrayList<>();
        try {
            //int a = 5 / 0;
            for (UserBaseReq userBaseReq : userBaseReqList) {
                UserBase dbUserBase = map.get(userBaseReq.getDueBillNo());
                if (dbUserBase == null) {
                    insertList.add(userBaseReq);
                } else {
                    updateList.add(userBaseReq);
                }
            }
            if (!insertList.isEmpty()) {
                log.info("走批量新增逻辑...");
                loanDao.addUserBaseList(insertList);
            }
            if (!updateList.isEmpty()) {
                log.info("走批量更新逻辑...");
                loanDao.updateUserBaseList(updateList);
                return Result.result(true,"调用成功");
            }
        } catch (Exception e) {
            //e.printStackTrace();
            log.error("e:",e);
            return Result.result(false,"调用失败");
        }
        return null;
    }
}
