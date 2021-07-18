package com.weshare.adapter.migration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.weshare.adapter.entity.*;
import com.weshare.adapter.repo.LoanDetailRepo;
import com.weshare.adapter.repo.MsgLogRepo;
import com.weshare.adapter.repo.RefundTicketRepo;
import com.weshare.adapter.repo.RepaymentPlanRepo;
import com.weshare.service.api.enums.ProjectEnum;
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
import java.util.*;
import java.util.stream.Collectors;

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
    private Migration migrationRefundTicket;

    @Autowired
    private MsgLogRepo msgLogRepo;
    @Autowired
    private HttpServletRequest request;

    private static final Map<String, Migration> map = new HashMap<>();
    private static MsgLogRepo staticMsgLogRepo;
    private static HttpServletRequest staticRequest;

    @PostConstruct
    public void init() {
        System.out.println("初始化了....");
        map.put(migrationLoanDetail.getClassName(), migrationLoanDetail);
        map.put(migrationRepayPlan.getClassName(), migrationRepayPlan);
        map.put(migrationRefundTicket.getClassName(), migrationRefundTicket);
        //map.put("repayPlan", new MigrationRepayPlan());
        staticMsgLogRepo = msgLogRepo;
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
            InterfaceLog.OriginalReqMsg.LoanDetail loanDetail = loanDetails.get(0);
            MsgLog msgLog = staticMsgLogRepo.findByOriginalDataLogId(dataLogId);
            Optional.ofNullable(msgLog).ifPresent(e -> {
                staticMsgLogRepo.deleteMsgLogByOriginalDataLogId(e.getOriginalDataLogId());
            });
            //保存日志信息
            msgLog = getMsgLog(batchDate, dataLogId, projectNo, productNo, content, loanDetail.getDueBillNo(), MsgLog.MsgTypeEnum.LOAN_DETAIL);
            staticMsgLogRepo.save(msgLog);
            //保存放款库数据
            loanDetailRepo.deleteByDueBillNoList(loanDetails.stream().map(InterfaceLog.OriginalReqMsg.LoanDetail::getDueBillNo).collect(Collectors.toList()));
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
            MsgLog msgLog = staticMsgLogRepo.findByOriginalDataLogId(dataLogId);
            Optional.ofNullable(msgLog).ifPresent(e -> {
                staticMsgLogRepo.deleteMsgLogByOriginalDataLogId(e.getOriginalDataLogId());
            });
            InterfaceLog.OriginalReqMsg.RepayPlan repayPlan = repayPlans.get(0);
            //保存日志信息
            msgLog = getMsgLog(batchDate, dataLogId, projectNo, productNo, content, repayPlan.getDueBillNo(), MsgLog.MsgTypeEnum.REPAY_PLAN);
            staticMsgLogRepo.save(msgLog);
            RepaymentPlanRepo.deleteByDueBillNoList(repayPlans.stream().map(InterfaceLog.OriginalReqMsg.RepayPlan::getDueBillNo).collect(Collectors.toList()));
            //保存还款计划库数据
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
            return "success";
        }
    }

    @Service("migrationRefundTicket")
    @Transactional
    public static class MigrationRefundTicket extends Migration {
        @Autowired
        private RefundTicketRepo refundTicketRepo;

        @Override
        public String dataMigration(String msgJson, String batchDate, String dataLogId) {
            log.info(InterfaceLog.ServiceEnum.REFUND_TICKET.name() + " 数据落库开始...");
            String projectNo = JsonUtil.toJsonNode(msgJson, "project_no");
            String productNo = JsonUtil.toJsonNode(msgJson, "product_no");
            String createDate = JsonUtil.toJsonNode(msgJson, "create_date");
            String content = JsonUtil.toJsonNode(msgJson, "content");

            List<InterfaceLog.OriginalReqMsg.RefundTicket> refundTickets = JsonUtil.fromJson(content, new TypeReference<List<InterfaceLog.OriginalReqMsg.RefundTicket>>() {
            });
            MsgLog msgLog = staticMsgLogRepo.findByOriginalDataLogId(dataLogId);
            Optional.ofNullable(msgLog).ifPresent(e -> {
                staticMsgLogRepo.deleteMsgLogByOriginalDataLogId(e.getOriginalDataLogId());
            });
            InterfaceLog.OriginalReqMsg.RefundTicket refundTicket = refundTickets.get(0);
            //保存日志信息
            msgLog = getMsgLog(batchDate, dataLogId, projectNo, productNo, content, refundTicket.getDueBillNo(), MsgLog.MsgTypeEnum.REFUND_TICKET);
            staticMsgLogRepo.save(msgLog);
            refundTicketRepo.deleteByDueBillNoList(refundTickets.stream().map(InterfaceLog.OriginalReqMsg.RefundTicket::getDueBillNo).collect(Collectors.toList()));
            //保存退票数据
            refundTickets.forEach(e -> {
                refundTicketRepo.save(
                        new RefundTicket()
                                .setId(SnowFlake.getInstance().nextId() + "")
                                .setRefundStatus(ChangeEnumUtils.changeEnum(ProjectEnum.YXMS.getProjectNo(), "refundStatus", e.getRefundStatus(), RefundTicket.RefundStatusEnum.class))
                                .setAccountNum(e.getAccountNum())
                                .setDueBillNo(e.getDueBillNo())
                                .setLoanAmount(e.getLoanAmount())
                                .setRefundDate(e.getRefundDate())
                                .setBatchDate(LocalDate.parse(batchDate))
                                .setCreateDate(LocalDateTime.parse(createDate, DateTimeFormatter.ofPattern("yyyyMMddHHmmss")))
                );
            });
            return "success";
        }
    }

    private static MsgLog getMsgLog(String batchDate, String dataLogId, String projectNo, String productNo, String content, String dueBillNo, MsgLog.MsgTypeEnum msgTypeEnum) {
        MsgLog msgLog = new MsgLog()
                .setApplyNo(dueBillNo)
                .setProjectNo(projectNo)
                .setProductNo(productNo)
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
