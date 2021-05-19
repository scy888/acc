package com.weshare.loan.provider;

import com.weshare.loan.dao.LoanDao;
import com.weshare.loan.entity.BackCard;
import com.weshare.loan.entity.CriticalDataHash;
import com.weshare.loan.entity.LinkMan;
import com.weshare.loan.entity.UserBase;
import com.weshare.loan.enums.HashPrefix;
import com.weshare.loan.repo.BackCardRepo;
import com.weshare.loan.repo.CriticalDataHashRepo;
import com.weshare.loan.repo.LinkManRepo;
import com.weshare.service.api.client.LoanClient;
import com.weshare.service.api.entity.UserBaseReq;
import com.weshare.service.api.result.Result;
import common.Md5Utils;
import common.SnowFlake;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    @Autowired
    private LinkManRepo linkManRepo;
    @Autowired
    private BackCardRepo backCardRepo;
    @Autowired
    private CriticalDataHashRepo criticalDataHashRepo;
    private String MD5 = "MD5";

    @Override
    @Transactional(rollbackFor = Exception.class)
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
                //保存hash表
                criticalDataHashRepo.saveAll(
                        getCollect(insertList)
                );
                loanDao.addUserBaseList(insertList.stream().map(e -> {
                    CriticalDataHash criticalDataHash = criticalDataHashRepo.findByDueBillNo(e.getDueBillNo());
                    return e.setUserId(criticalDataHash.getUserId())
                            .setUserName(criticalDataHash.getUserNameHash())
                            .setIdCardNum(criticalDataHash.getIdCardNumHash())
                            .setIphone(criticalDataHash.getIphoneHash())
                            .setCarNum(criticalDataHash.getCarNumHash());
                }).collect(Collectors.toList()));
                //新增联系人信息,银行卡信息
                for (UserBaseReq userBaseReq : insertList) {
                    List<UserBaseReq.LinkManReq> linkManReqList = userBaseReq.getLinkManList();
                    linkManRepo.saveAll(
                            linkManReqList.stream().map(e -> {
                                LinkMan linkMan = new LinkMan();
                                BeanUtils.copyProperties(e, linkMan);
                                linkMan.setId(SnowFlake.getInstance().nextId() + "");
                                return linkMan;
                            }).collect(Collectors.toList())
                    );
                    List<UserBaseReq.BackCardReq> backCardReqList = userBaseReq.getBackCardList();
                    backCardRepo.saveAll(
                            backCardReqList.stream().map(e -> {
                                BackCard backCard = new BackCard();
                                BeanUtils.copyProperties(e, backCard);
                                backCard.setId(SnowFlake.getInstance().nextId() + "");
                                return backCard;
                            }).collect(Collectors.toList())
                    );
                }
            }
            if (!updateList.isEmpty()) {
                log.info("走批量更新逻辑...");
                //更新hash表
                criticalDataHashRepo.saveAll(
                        getCollect(updateList)
                );
                loanDao.updateUserBaseList(updateList.stream().map(e -> {
                    CriticalDataHash criticalDataHash = criticalDataHashRepo.findByDueBillNo(e.getDueBillNo());
                    return e.setUserId(criticalDataHash.getUserId())
                            .setUserName(criticalDataHash.getUserNameHash())
                            .setIdCardNum(criticalDataHash.getIdCardNumHash())
                            .setIphone(criticalDataHash.getIphoneHash())
                            .setCarNum(criticalDataHash.getCarNumHash());
                }).collect(Collectors.toList()));
                //更新联系人信息,银行卡信息
                for (UserBaseReq userBaseReq : updateList) {
                    List<UserBaseReq.LinkManReq> linkManReqList = userBaseReq.getLinkManList();
                    linkManRepo.saveAll(
                            linkManReqList.stream().map(e -> {
                                LinkMan linkMan = linkManRepo.findByIdCardNum(e.getIdCardNum());
                                BeanUtils.copyProperties(e, linkMan);
                                return linkMan;
                            }).collect(Collectors.toList())
                    );
                    List<UserBaseReq.BackCardReq> backCardReqList = userBaseReq.getBackCardList();
                    backCardRepo.saveAll(
                            backCardReqList.stream().map(e -> {
                                BackCard backCard = backCardRepo.findByBackNum(e.getBackNum());
                                BeanUtils.copyProperties(e, backCard);
                                return backCard;
                            }).collect(Collectors.toList())
                    );
                }
            }
            return Result.result(true, "调用成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("e:", e);
            return Result.result(false, "调用失败");
        }
    }

    @Override
    public Result tesGettUrl(String name, Integer age) {
        String msg = String.format("用户名:%s,年龄:%d", name, age);
        System.out.println(msg);
        return Result.result(true,msg);
    }

    private List<CriticalDataHash> getCollect(List<UserBaseReq> userBaseReqList) {
        return userBaseReqList.stream().map(e -> new CriticalDataHash()
                .setUserId(Md5Utils.algorithmEncode(e.getUserId(), MD5))
                .setUserName(e.getUserName())
                .setUserNameHash(HashPrefix.用户姓名哈希值.getPrefix() + Md5Utils.algorithmEncode(e.getUserName(), MD5))
                .setIdCardNum(e.getIdCardNum())
                .setIdCardNumHash(HashPrefix.证件号码哈希值.getPrefix() + Md5Utils.algorithmEncode(e.getIdCardNum(), MD5))
                .setIphone(e.getIphone())
                .setIphoneHash(HashPrefix.手机号码哈希值.getPrefix() + Md5Utils.algorithmEncode(e.getIphone(), MD5))
                .setCarNum(e.getCarNum())
                .setCarNumHash(HashPrefix.车牌号码哈希值.getPrefix() + Md5Utils.algorithmEncode(e.getCarNum(), MD5))
                .setDueBillNo(e.getDueBillNo())
                .setProjectNo(e.getProjectNo())
                .setCreateDate(LocalDate.now())
                .setLastModifyDate(LocalDateTime.now()))
                .collect(Collectors.toList());
    }
}
