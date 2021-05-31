package com.weshare.loan.provider;

import com.weshare.loan.dao.LoanDao;
import com.weshare.loan.entity.*;
import com.weshare.loan.enums.HashPrefix;
import com.weshare.loan.feignClient.RepayFeignClient;
import com.weshare.loan.repo.*;
import com.weshare.service.api.client.LoanClient;
import com.weshare.service.api.client.RepayClient;
import com.weshare.service.api.entity.*;
import com.weshare.service.api.enums.LoanStatusEnum;
import com.weshare.service.api.enums.ProjectEnum;
import com.weshare.service.api.result.Result;
import common.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    @Autowired
    private LinkManRepo linkManRepo;
    @Autowired
    private BackCardRepo backCardRepo;
    @Autowired
    private CriticalDataHashRepo criticalDataHashRepo;
    @Autowired
    private LoanContractRepo loanContractRepo;
    @Autowired
    private LoanTransFlowRepo loanTransFlowRepo;
    @Autowired
    private RepayFeignClient repayFeignClient;
    private String MD5 = "MD5";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result saveListUserBase(List<UserBaseReq> userBaseReqList) {
        //log.info("userBaseReqList:{}", JsonUtil.toJson(userBaseReqList, true));
        //批量保存用户信息,先根据借据号查询是否有,有就更新,没有就新增

        List<UserBase> dbUserBaserList = loanDao.findUserBaseByDueBillNo(userBaseReqList.stream().map(UserBaseReq::getDueBillNo)
                .collect(Collectors.toList()));
        Map<String, UserBase> map = dbUserBaserList.stream().collect(Collectors.toMap(UserBase::getDueBillNo, Function.identity()));
        List<UserBaseReq> insertList = new ArrayList<>();
        List<UserBaseReq> updateList = new ArrayList<>();
        try {
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
            //int a = 5 / 0;
            return Result.result(true, "调用成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("e:", e);
            throw new RuntimeException("e:" + e.getMessage());
            //return Result.result(false, "调用失败");
        }
    }

    @Override
    public Result tesGettUrl(String name, Integer age) {
        String msg = String.format("用户名:%s,年龄:%d", name, age);
        System.out.println(msg);
        return Result.result(true, msg);
    }

    @Override
    public Result tesPostUrl(User user) {
        String name = user.getName();
        Integer age = user.getAge();
        String msg = String.format("用户名:%s,年龄:%d", name, age);
        System.out.println(msg);
        return Result.result(true, msg);
    }

    @Override
    @Transactional
    @Async
    @Deprecated(forRemoval = true)
    public Result saveAllLoanContractAndLoanTransFlow(List<? extends LoanDetailReq> list) {
        log.info("saveAllLoanContractAndLoanTransFlow()方法的异步调用的线程名:{}", Thread.currentThread().getName());
        List<LoanContract> loanContractList = loanContractRepo.findByDueBillNoIn(list.stream().distinct()
                .map(LoanDetailReq::getDueBillNo).collect(Collectors.toList()));
        Map<String, LoanContract> map = loanContractList.stream().collect(Collectors.toMap(LoanContract::getDueBillNo, Function.identity(), (a, b) -> b));
        list = list.stream().filter(e -> e.getLoanStatus().equals(StatusEnum.成功.getCode())).collect(Collectors.toList());
        for (LoanDetailReq req : list) {

            LocalDateTime localDateTime = LocalDateTime.now().withYear(req.getBatchDate().getYear())
                    .withMonth(req.getBatchDate().getMonthValue())
                    .withDayOfMonth(req.getBatchDate().getDayOfMonth());

            LoanContract loanContract = map.get(req.getDueBillNo());

            loanContract = Optional.ofNullable(loanContract).orElseGet(
                    () -> new LoanContract()
                            .setId(SnowFlake.getInstance().nextId() + "")
                            .setCreatedDate(localDateTime)
            );
            loanContract.setDueBillNo(req.getDueBillNo())
                    .setUserId(criticalDataHashRepo.findByDueBillNo(req.getDueBillNo()).getUserId())
                    .setProjectNo(ProjectEnum.YXMS.getProjectNo())
                    .setProductNo(ProjectEnum.YXMS.getProducts().get(0).getProductNo())
                    .setProductName(ProjectEnum.getProductName(loanContract.getProjectNo(), loanContract.getProductNo()))
                    .setContractAmount(req.getLoanAmount())
                    .setInterestRate(new BigDecimal("0.02"))
                    .setTotalTerm(req.getTerm())
                    .setPrincipal(req.getLoanAmount())
                    .setBatchDate(req.getBatchDate())
                    .setRemark("放款成功")
                    .setLastModifiedDate(localDateTime);
            loanContractRepo.save(loanContract);

            Optional.ofNullable(loanTransFlowRepo.findByBatchDateAndDueBillNo(req.getBatchDate(), req.getDueBillNo())).ifPresent(e -> {
                loanTransFlowRepo.deleteByBatchDateAndDueBillNo(req.getBatchDate(), req.getDueBillNo());
            });
            BackCard backCard = backCardRepo.findByDueBillNo(req.getDueBillNo()).stream().findFirst().orElse(null);
            loanTransFlowRepo.save(new LoanTransFlow()
                    .setId(SnowFlake.getInstance().nextId() + "")
                    .setFlowSn(SnowFlake.getInstance().nextId() + "")
                    .setProjectNo(ProjectEnum.YXMS.getProjectNo())
                    .setProductNo(ProjectEnum.YXMS.getProducts().get(0).getProductNo())
                    .setDueBillNo(req.getDueBillNo())
                    .setTransAmount(req.getLoanAmount())
                    .setBankAccountName(backCard.getBackName().name())
                    .setBankAccountNo(backCard.getBackName().getNum())
                    .setTransTime(localDateTime)
                    .setRemark("放款交易流水成功")
                    .setBatchDate(req.getBatchDate())
                    .setCreatedDate(localDateTime)
                    .setLastModifiedDate(localDateTime)
            );
        }

        return Result.result(true);
    }

    @Override
    @Async
    @Transactional
    public Result saveAllLoanContract(List<LoanContractReq> list) {
        log.info("saveAllLoanContract()方法的异步调用的线程名:{}", Thread.currentThread().getName());
        List<LoanContract> loanContractList = loanContractRepo.findByDueBillNoIn(list.stream().distinct()
                .map(LoanContractReq::getDueBillNo).collect(Collectors.toList()));
        Map<String, LoanContract> map = loanContractList.stream().collect(Collectors.toMap(LoanContract::getDueBillNo, Function.identity(), (a, b) -> b));
        for (LoanContractReq req : list) {
            LocalDate batchDate = req.getBatchDate();
            LocalDateTime localDateTime = LocalDateTime.now().withYear(batchDate.getYear()).withMonth(batchDate.getMonthValue()).withDayOfMonth(batchDate.getDayOfMonth());
            LoanContract loanContract = Optional.ofNullable(map.get(req.getDueBillNo())).orElseGet(
                    () -> new LoanContract().setId(SnowFlake.getInstance().nextId() + "")
                            .setCreatedDate(localDateTime)
            );
            BeanUtils.copyProperties(req, loanContract);
            loanContractRepo.save(loanContract
                    .setLastModifiedDate(localDateTime)
                    .setUserId(criticalDataHashRepo.findByDueBillNo(req.getDueBillNo()).getUserId()));
        }
        return Result.result(true);
    }

    @Override
    @Async
    @Transactional
    public Result saveAllLoanTransFlow(List<LoanTransFlowReq> list, String batchDate) {
        log.info("saveAllLoanTransFlow()方法的异步调用的线程名:{}", Thread.currentThread().getName());
        List<String> dueBillNoList = list.stream().map(LoanTransFlowReq::getDueBillNo).collect(Collectors.toList());
        LocalDate batchDate_ = LocalDate.parse(batchDate);
        List<LoanTransFlow> loanTransFlows = loanTransFlowRepo.findByBatchDateAndDueBillNoIn(batchDate_, dueBillNoList);
        Map<String, LoanTransFlow> map = loanTransFlows.stream().collect(Collectors.toMap(e->e.getDueBillNo()+"_"+e.getBatchDate(), Function.identity()));
        for (LoanTransFlowReq req : list) {
            Optional.ofNullable(map.get(req.getDueBillNo()+"_"+req.getBatchDate())).ifPresentOrElse(
                    e -> loanTransFlowRepo.deleteByBatchDateAndDueBillNo(e.getBatchDate(), e.getDueBillNo()),
                    () -> log.info("batchDate:{},借据号:{},不存在...", batchDate_, req.getDueBillNo()));

            loanTransFlowRepo.save(
                    createLoanTransFlow(req)
            );
        }
//        loanTransFlowRepo.saveAll(
//                list.stream().map(e -> {
//                    LoanTransFlow loanTransFlow = new LoanTransFlow();
//                    BeanUtils.copyProperties(e, loanTransFlow);
//                    LocalDate batchDate__ = e.getBatchDate();
//                    LocalDateTime localDateTime = LocalDateTime.now().withYear(batchDate__.getYear()).withMonth(batchDate__.getMonthValue()).withDayOfMonth(batchDate__.getDayOfMonth());
//                    BackCard backCard = backCardRepo.findByDueBillNo(e.getDueBillNo()).get(0);
//                    return loanTransFlow.setId(SnowFlake.getInstance().nextId() + "")
//                            .setBankAccountName(backCard.getBackName().name())
//                            .setBankAccountNo(backCard.getBackName().getNum())
//                            .setCreatedDate(localDateTime)
//                            .setLastModifiedDate(localDateTime);
//                }).collect(Collectors.toList())
//        );
        return Result.result(true);
    }

    @Override
    public Result<List<LoanContractReq>> findLoanContractByDueBillNoIn(List<String> list) {
        List<LoanContract> loanContractList = loanContractRepo.findByDueBillNoIn(list);
        List<LoanContractReq> loanContractReqList = ReflectUtils.getBeanUtils(loanContractList, LoanContractReq.class);
        Result result = Result.result(true, loanContractReqList);
        return result;

    }

    @Override
    public Result UpdateRepaySummaryCurrentTerm(String projectNo, String batchDate) {
        List<LoanContract> loanContractList = loanContractRepo.findByProjectNo(projectNo);
        for (LoanContract loanContract : loanContractList) {
            String dueBillNo = loanContract.getDueBillNo();
            LocalDate firstTermDueDate = loanContract.getFirstTermDueDate();
            LocalDate lastTermDueDate = loanContract.getLastTermDueDate();
            Integer totalTerm = loanContract.getTotalTerm();
            repayFeignClient.UpdateRepaySummaryCurrentTerm(new RepayClient.UpdateRepaySummaryCurrentTerm()
                    .setCurrentTerm(StringUtils.getCurrentTerm(firstTermDueDate, lastTermDueDate, LocalDate.parse(batchDate), totalTerm))
                    .setBatchDate(batchDate).setDueBillNo(dueBillNo));
        }
        return Result.result(true);
    }

    @Override
    public Result UpdateLoanContractStatus(List<UpdateLoanContractStatus> list) {

        List<LoanContract> loanContractList = loanContractRepo.findByDueBillNoIn(list.stream().map(UpdateLoanContractStatus::getDueBillNo)
                .collect(Collectors.toList()));
        for (UpdateLoanContractStatus status : list) {
            for (LoanContract contract : loanContractList) {
                if (status.getDueBillNo().equals(contract.getDueBillNo())) {
                    loanContractRepo.save(contract
                            .setBatchDate(LocalDate.parse(status.getBatchDate()))
                            .setLoanStatusEnum(LoanStatusEnum.REFUND)
                            .setLastModifiedDate(DateUtils.getLocalDateTime(LocalDate.parse(status.getBatchDate())))
                            .setRemark("已退票")
                    );
                    break;
                }
            }
        }
        return Result.result(true);
    }

    private LoanTransFlow createLoanTransFlow(LoanTransFlowReq req) {
        LoanTransFlow loanTransFlow = new LoanTransFlow();
        BeanUtils.copyProperties(req, loanTransFlow);
        LocalDate batchDate__ = req.getBatchDate();
        LocalDateTime localDateTime = LocalDateTime.now().withYear(batchDate__.getYear()).withMonth(batchDate__.getMonthValue()).withDayOfMonth(batchDate__.getDayOfMonth());
        BackCard backCard = backCardRepo.findByDueBillNo(req.getDueBillNo()).get(0);
        loanTransFlow.setId(SnowFlake.getInstance().nextId() + "")
                .setBankAccountName(backCard.getBackName().name())
                .setBankAccountNo(backCard.getBackName().getNum())
                .setCreatedDate(localDateTime)
                .setLastModifiedDate(localDateTime);
        return loanTransFlow;
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

    @Getter
    public enum StatusEnum {

        成功("01"),
        失败("02");
        private String code;

        StatusEnum(String code) {
            this.code = code;
        }
    }
}
