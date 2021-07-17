package com.weshare.adapter.migration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.weshare.adapter.entity.InterfaceLog;
import com.weshare.adapter.entity.LoanDetail;
import com.weshare.adapter.entity.MsgLog;
import com.weshare.adapter.entity.RepaymentPlan;
import com.weshare.adapter.repo.LoanDetailRepo;
import com.weshare.adapter.repo.MsgLogRepo;
import com.weshare.adapter.repo.RepaymentPlanRepo;
import common.ChangeEnumUtils;
import common.JsonUtil;
import common.SnowFlake;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.adapter.migration
 * @date: 2021-07-16 16:59:29
 * @describe:
 */
@Slf4j
@Component
public class DataMigrationFactory {

    @Autowired
    private Migration migrationLoanDetail;
    @Autowired
    private Migration migrationRepayPlan;

    @Autowired
    private MsgLogRepo msgLogRepo;
    @Autowired
    private HttpServletRequest request;

    private static final Map<String, Migration> map = new HashMap<>();
    private static MsgLogRepo staticmMgLogRepo;
    private static HttpServletRequest staticRequest;

    @PostConstruct
    public void init() {
        System.out.println("初始化了....");
        map.put(migrationLoanDetail.getClassName(), migrationLoanDetail);
        map.put(migrationRepayPlan.getClassName(), migrationRepayPlan);
        //map.put("repayPlan", new MigrationRepayPlan());
        staticmMgLogRepo = msgLogRepo;
        staticRequest = request;
    }

    public static Migration getDataMigration(String serviceId) {
        Migration migration = map.get(serviceId);
        return migration;
    }

    @Service("migrationLoanDetail")
    @Transactional
    public static class MigrationLoanDetail extends Migration {
        @Autowired
        private LoanDetailRepo loanDetailRepo;

        @Override
        public String dataMigration(String msgJson, String batchDate, String dataLogId) {
            log.info(InterfaceLog.ServiceEnum.LOAN_DETAIL.name() + " 数据落库开始...");
            String projectNo = JsonUtil.toJsonNode(msgJson, "project_no");
            String productNo = JsonUtil.toJsonNode(msgJson, "product_no");
            String createDate = JsonUtil.toJsonNode(msgJson, "create_date");
            String content = JsonUtil.toJsonNode(msgJson, "content");
            List<InterfaceLog.OriginalReqMsg.LoanDetail> loanDetails = JsonUtil.fromJson(content, new TypeReference<List<InterfaceLog.OriginalReqMsg.LoanDetail>>() {
            });
            MsgLog msgLog = staticmMgLogRepo.findByOriginalDataLogId(dataLogId);
            if (Objects.isNull(msgLog)) {
                InterfaceLog.OriginalReqMsg.LoanDetail loanDetail = loanDetails.get(0);
                //保存日子信息
                msgLog = getMsgLog(batchDate, dataLogId, projectNo, productNo, content, loanDetail.getDueBillNo(),MsgLog.MsgTypeEnum.LOAN_DETAIL);
                staticmMgLogRepo.save(msgLog);
                //保存放款库数据
                loanDetails.forEach(e -> {
                    loanDetailRepo.save(
                            new LoanDetail()
                                    .setId(SnowFlake.getInstance().nextId() + "")
                                    .setDueBillNo(e.getDueBillNo())
                                    .setLoanStatus(ChangeEnumUtils.changeEnum(ChangeEnumUtils.YXMS.getProjectNo(), "loanStatus", e.getLoanStatus(), LoanDetail.LoanStatusEnum.class))
                                    .setAccountNum(e.getAccountNum())
                                    .setLoanAmount(e.getLoanAmount())
                                    .setLoanDate(e.getLoanDate())
                                    .setTerm(e.getTerm())
                                    .setSerialNum(e.getSerialNum())
                                    .setBatchDate(LocalDate.parse(batchDate))
                                    .setCreatedDate(LocalDateTime.parse(createDate, DateTimeFormatter.ofPattern("yyyyMMddHHmmss")))
                                    .setLastModifiedDate(LocalDateTime.now())
                    );
                });
            } else {
                staticmMgLogRepo.save(msgLog);
            }
            return "success";
        }
    }

    @Service("migrationRepayPlan")
    @Transactional
    public static class MigrationRepayPlan extends Migration {
        @Autowired
        private RepaymentPlanRepo RepaymentPlanRepo;

        @Override
        public String dataMigration(String msgJson, String batchDate, String dataLogId) {
            log.info(InterfaceLog.ServiceEnum.REPAY_PLAN.name() + " 数据落库开始...");
            String projectNo = JsonUtil.toJsonNode(msgJson, "project_no");
            String productNo = JsonUtil.toJsonNode(msgJson, "product_no");
            String createDate = JsonUtil.toJsonNode(msgJson, "create_date");
            String content = JsonUtil.toJsonNode(msgJson, "content");
            List<InterfaceLog.OriginalReqMsg.RepayPlan> repayPlans = JsonUtil.fromJson(content, new TypeReference<List<InterfaceLog.OriginalReqMsg.RepayPlan>>() {
            });
            MsgLog msgLog = staticmMgLogRepo.findByOriginalDataLogId(dataLogId);
            if (Objects.isNull(msgLog)) {
                InterfaceLog.OriginalReqMsg.RepayPlan repayPlan = repayPlans.get(0);
                //保存日子信息
                msgLog = getMsgLog(batchDate, dataLogId, projectNo, productNo, content, repayPlan.getDueBillNo(),MsgLog.MsgTypeEnum.REPAYMENT_PLAN);
                staticmMgLogRepo.save(msgLog);
                //保存放款库数据
                repayPlans.forEach(e -> {
                    for (InterfaceLog.OriginalReqMsg.RepayPlan.DueBillNoList dueBillNoList : e.getDueBillNoList()) {
                        RepaymentPlanRepo.save(
                                new RepaymentPlan()
                                        .setId(SnowFlake.getInstance().nextId() + "")
                                        .setDueBillNo(e.getDueBillNo())
                                        .setRepaymentDate(dueBillNoList.getRepaymentDate())
                                        .setTerm(dueBillNoList.getTerm())
                                        .setShouldMonthMoney(dueBillNoList.getShouldMonthMoney())
                                        .setShouldCapitalMoney(dueBillNoList.getShouldCapitalMoney())
                                        .setShouldInterestMoney(dueBillNoList.getShouldInterestMoney())
                                        .setBatchDate(LocalDate.parse(batchDate))
                                        .setCreatedDate(LocalDateTime.parse(createDate, DateTimeFormatter.ofPattern("yyyyMMddHHmmss")))
                                        .setLastModifiedDate(LocalDateTime.now())
                        );
                    }
                });
            } else {
                staticmMgLogRepo.save(msgLog);
            }
            return "success";
        }
    }

    private static MsgLog getMsgLog(String batchDate, String dataLogId, String projectNo, String productNo, String content, String dueBillNo, MsgLog.MsgTypeEnum msgTypeEnum) {
        MsgLog msgLog = new MsgLog()
                .setApplyNo(dueBillNo)
                .setProjectNo(projectNo)
                .setProductNo(productNo)//MsgLog.MsgTypeEnum.LOAN_DETAIL
                .setMsgType(msgTypeEnum)
                .setOriginalDataLogId(dataLogId)
                .setReqData(content)
                //.setRequestRiskTime()
                .setUrl(staticRequest.getRequestURI())
                .setBatchDate(LocalDate.parse(batchDate))
                .setCreatedDate(LocalDateTime.now())
                .setLastModifiedDate(LocalDateTime.now());
        return msgLog;
    }
}
