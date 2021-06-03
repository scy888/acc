package com.weshare.repay.provider;

import com.weshare.repay.entity.ReceiptDetail;
import com.weshare.repay.entity.RepayPlan;
import com.weshare.repay.entity.RepaySummary;
import com.weshare.repay.entity.RepayTransFlow;
import com.weshare.repay.repo.ReceiptDetailRepo;
import com.weshare.repay.repo.RepayPlanRepo;
import com.weshare.repay.repo.RepaySummaryRepo;
import com.weshare.repay.repo.RepayTransFlowRepo;
import com.weshare.service.api.client.RepayClient;
import com.weshare.service.api.entity.ReceiptDetailReq;
import com.weshare.service.api.entity.RepayPlanReq;
import com.weshare.service.api.entity.RepaySummaryReq;
import com.weshare.service.api.entity.RepayTransFlowReq;
import com.weshare.service.api.enums.FeeTypeEnum;
import com.weshare.service.api.enums.ProjectEnum;
import com.weshare.service.api.enums.TransFlowTypeEnum;
import com.weshare.service.api.enums.TransStatusEnum;
import com.weshare.service.api.result.Result;
import com.weshare.service.api.vo.DueBillNoAndTermDueDate;
import com.weshare.service.api.vo.Tuple2;
import com.weshare.service.api.vo.Tuple3;
import com.weshare.service.api.vo.Tuple4;
import common.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.repay.provider
 * @date: 2021-04-26 21:59:00
 * @describe:
 */
@RestController
@Slf4j
public class RepayProvider implements RepayClient {
    @Autowired
    private RepayPlanRepo repayPlanRepo;
    @Autowired
    private RepaySummaryRepo repaySummaryRepo;
    @Autowired
    private RepayTransFlowRepo repayTransFlowRepo;
    @Autowired
    private ReceiptDetailRepo receiptDetailRepo;
    private Integer pageSize = 1;

    @Override
    public String getRepayClient(String repayClient, Boolean isInvoking) {
        if (isInvoking) {
            repayClient = "放款服务远程调用了还款服务==>" + repayClient;
            log.info(repayClient);
            return repayClient;
        }

        repayClient = "放款服务没有远程调用还款服务==>" + repayClient;
        log.info(repayClient);
        return repayClient;
    }

    @Override
    public Result saveRepayPlan(List<RepayPlanReq> list) {
        for (RepayPlanReq repayPlanReq : list) {
            RepayPlan repayPlan = repayPlanRepo.findByDueBillNoAndTerm(repayPlanReq.getDueBillNo(), repayPlanReq.getTerm());
            LocalDate batchDate = repayPlanReq.getBatchDate();
            LocalDateTime localDateTime = LocalDateTime.now().withYear(batchDate.getYear()).withMonth(batchDate.getMonthValue()).withDayOfMonth(batchDate.getDayOfMonth());

            repayPlan = Optional.ofNullable(repayPlan).orElseGet(() -> new RepayPlan()
                    .setId(SnowFlake.getInstance().nextId() + "")
                    .setCreatedDate(localDateTime));
            BeanUtils.copyProperties(repayPlanReq, repayPlan);

            repayPlanRepo.save(repayPlan.setLastModifiedDate(localDateTime));
        }

        return Result.result(true);
    }

    @Override
    @Async
    public Result saveRepaySummary(List<RepaySummaryReq> list) {
        log.info("saveRepaySummary()方法的异步调用的线程名:{}", Thread.currentThread().getName());
        List<RepaySummary> repaySummaryList = repaySummaryRepo.findByDueBillNoIn(list.stream()
                .map(RepaySummaryReq::getDueBillNo).collect(Collectors.toList()));
        Map<String, RepaySummary> map = repaySummaryList.stream().collect(Collectors.toMap(RepaySummary::getDueBillNo, Function.identity()));
        for (RepaySummaryReq summaryReq : list) {
            LocalDate batchDate = summaryReq.getBatchDate();
            LocalDateTime localDateTime = LocalDateTime.now().withYear(batchDate.getYear()).withMonth(batchDate.getMonthValue()).withDayOfMonth(batchDate.getDayOfMonth());

            RepaySummary repaySummary = Optional.ofNullable(map.get(summaryReq.getDueBillNo()))
                    .orElseGet(
                            () -> new RepaySummary().setId(SnowFlake.getInstance().nextId() + "")
                                    .setCreatedDate(localDateTime));
            BeanUtils.copyProperties(summaryReq, repaySummary);
            repaySummaryRepo.save(repaySummary.setLastModifiedDate(localDateTime));
        }
        return Result.result(true);
    }

    @Override
    public Result<List<RepaySummaryReq>> findRepaySummaryByDueBillNoIn(List<String> list) {
        List<RepaySummary> repaySummaryList = repaySummaryRepo.findByDueBillNoIn(list);
        List<RepaySummaryReq> summaryReqList = ReflectUtils.getBeanUtils(repaySummaryList, RepaySummaryReq.class);
        Result result = Result.result(true, summaryReqList);
        return result;
    }

    @Override
    @Deprecated(forRemoval = true)
    public Result RefreshRepaySummaryCurrentTerm(String projectNo, String batchDate) {

        int countTotal = repaySummaryRepo.countByProjectNo(projectNo);
        int pageTotal = (int) Math.ceil(countTotal * 1.0 / pageSize);

        for (int pageNum = 1; pageNum <= pageTotal; pageNum++) {
            Page<String> page = repaySummaryRepo.findByProjectNo(projectNo, PageRequest.of(pageNum - 1, pageSize));
            List<String> dueBillNoList = page.getContent();
            List<DueBillNoAndTermDueDate> list = repayPlanRepo.findByDueBillNoIn(dueBillNoList);
            Map<String, List<DueBillNoAndTermDueDate>> map = list.stream().collect(Collectors.groupingBy(DueBillNoAndTermDueDate::getDueBillNo));
            for (Map.Entry<String, List<DueBillNoAndTermDueDate>> entry : map.entrySet()) {
                String dueBillNo = entry.getKey();
                List<DueBillNoAndTermDueDate> dateList = entry.getValue();
                LocalDate firstDate = dateList.stream().map(DueBillNoAndTermDueDate::getTermDueDate).min(LocalDate::compareTo).orElse(null);
                LocalDate endDate = dateList.stream().map(DueBillNoAndTermDueDate::getTermDueDate).max(LocalDate::compareTo).orElse(null);
                RepaySummary repaySummary = repaySummaryRepo.findByDueBillNo(dueBillNo);
                repaySummary.setCurrentTerm(StringUtils.getCurrentTerm(firstDate, endDate, LocalDate.parse(batchDate), dateList.size()));
                Integer currentTerm = repaySummary.getCurrentTerm();
                for (DueBillNoAndTermDueDate dueBillNoAndTermDueDate : dateList) {
                    if (dueBillNoAndTermDueDate.getTerm().equals(currentTerm)) {
                        repaySummary.setCurrentTermDueDate(dueBillNoAndTermDueDate.getTermDueDate());
                        break;
                    }
                }
                LocalDate localDate = LocalDate.parse(batchDate);
                LocalDateTime localDateTime = LocalDateTime.now().withYear(localDate.getYear()).withMonth(localDate.getMonthValue()).withDayOfMonth(localDate.getDayOfMonth());
                repaySummary.setLastModifiedDate(localDateTime);
                repaySummaryRepo.save(repaySummary);
            }
        }

        return Result.result(true);
    }

    @Override
    public Result UpdateRepaySummaryCurrentTerm(UpdateRepaySummaryCurrentTerm updateRepaySummaryCurrentTerm) {
        String batchDate = updateRepaySummaryCurrentTerm.getBatchDate();
        Integer currentTerm = updateRepaySummaryCurrentTerm.getCurrentTerm();
        String dueBillNo = updateRepaySummaryCurrentTerm.getDueBillNo();
        LocalDate localDate = LocalDate.parse(batchDate);
        LocalDateTime localDateTime = LocalDateTime.now().withYear(localDate.getYear()).withMonth(localDate.getMonthValue()).withDayOfMonth(localDate.getDayOfMonth());
        RepaySummary repaySummary = repaySummaryRepo.findByDueBillNo(dueBillNo);
        repaySummary.setCurrentTerm(currentTerm);
        repaySummary.setLastModifiedDate(localDateTime);
        LocalDate termDueDate = repayPlanRepo.findByDueBillNoAndTerm(dueBillNo, currentTerm).getTermDueDate();
        repaySummary.setCurrentTermDueDate(termDueDate);
        repaySummaryRepo.save(repaySummary);
        return Result.result(true);
    }

    @Override
    @Transactional
    public Result saveAllRepayTransFlow(List<RepayTransFlowReq> list, String batchDate) {
        repayTransFlowRepo.deleteByBatchDateAndDueBillNoIn(LocalDate.parse(batchDate), list.stream().map(RepayTransFlowReq::getDueBillNo)
                .collect(Collectors.toList()));
        repayTransFlowRepo.saveAll(
                list.stream().map(e -> {
                            RepayTransFlow repayTransFlow = new RepayTransFlow();
                            BeanUtils.copyProperties(e, repayTransFlow);
                            return repayTransFlow.setId(SnowFlake.getInstance().nextId() + "")
                                    .setTransFlowType(ChangeEnumUtils.changeEnum(ProjectEnum.YXMS.getProjectNo(), "transFlowType", e.getTransFlowType(), TransFlowTypeEnum.class))
                                    .setTransStatus(ChangeEnumUtils.changeEnum(ProjectEnum.YXMS.getProjectNo(), "transStatus", e.getTransStatus(), TransStatusEnum.class))
                                    .setCreatedDate(DateUtils.getLocalDateTime(e.getBatchDate()))
                                    .setLastModifiedDate(DateUtils.getLocalDateTime(e.getBatchDate()));
                        }
                ).collect(Collectors.toList())
        );
        return Result.result(true);
    }

    @Override
    public Result saveAllReceiptDetail(List<ReceiptDetailReq> list, String batchDate) {
        receiptDetailRepo.deleteByBatchDateAndDueBillNoIn(LocalDate.parse(batchDate), list.stream().map(ReceiptDetailReq::getDueBillNo)
                .collect(Collectors.toList()));

        receiptDetailRepo.saveAll(
                list.stream().map(e -> {
                    ReceiptDetail receiptDetail = new ReceiptDetail();
                    BeanUtils.copyProperties(e, receiptDetail);
                    return receiptDetail
                            .setId(SnowFlake.getInstance().nextId() + "")
                            .setCreatedDate(DateUtils.getLocalDateTime(e.getBatchDate()))
                            .setLastModifiedDate(DateUtils.getLocalDateTime(e.getBatchDate()));
                }).collect(Collectors.toList())
        );
        return Result.result(true);
    }

    @Override
    public Result<List<RepayPlanReq>> findRepayPlanListByDueBillNo(String dueBillNo) {
        List<RepayPlan> repayPlanList = repayPlanRepo.findByDueBillNo(dueBillNo);
        List<RepayPlanReq> repayPlanReqList = ReflectUtils.getBeanUtils(repayPlanList, RepayPlanReq.class);
        Result result = Result.result(true, repayPlanReqList);
        return result;
    }

    @Override
    public Result<List<Tuple2<BigDecimal, FeeTypeEnum>>> getReceiptDetailTwo(String dueBillNo, Integer term) {
        List<ReceiptDetail> receiptDetails = receiptDetailRepo.findByDueBillNoAndTerm(dueBillNo, term);
        List<Tuple2<BigDecimal, FeeTypeEnum>> tuple2s = receiptDetails.stream().map(e -> Tuple2.of(e.getAmount(), e.getFeeType()))
                .collect(Collectors.toList());
        return Result.result(true, tuple2s);
    }

    @Override
    public Result<List<Tuple3<String, String, BigDecimal>>> getFlowSn(String dueBillNo, String batchDate) {
        List<RepayTransFlow> list = repayTransFlowRepo.findByDueBillNoAndBatchDate(dueBillNo, LocalDate.parse(batchDate));
        List<Tuple3<String, String, BigDecimal>> tuple3s = list.stream().map(e -> Tuple3.of(e.getDueBillNo(), e.getFlowSn(), e.getTransAmount())).collect(Collectors.toList());
        return Result.result(true, tuple3s);
    }

    @Override
    public Result<Integer> getTotalTerm(String dueBillNo, String projectNo) {
        Integer totalTerm = repaySummaryRepo.findByDueBillNoAndProjectNo(dueBillNo, projectNo);
        log.info("totalTerm:{}", totalTerm);
        return Result.result(true, totalTerm);
    }

    @Override
    public Result<List<Tuple4<BigDecimal, BigDecimal, LocalDate, Integer>>> getRepayPlanFourth(String dueBillNo) {
        List<RepayPlan> planList = repayPlanRepo.findByDueBillNo(dueBillNo);
        List<Tuple4<BigDecimal, BigDecimal, LocalDate, Integer>> tuple4s = planList.stream().map(repayPlan ->
                Tuple4.of(repayPlan.getTermBillAmount(), repayPlan.getTermPrin(), repayPlan.getTermDueDate(), repayPlan.getTerm())).collect(Collectors.toList());
        return Result.result(true, tuple4s);
    }

    @Override
    public Result updateRepayPlan(RepayPlanReq repayPlanReq) {
        RepayPlan repayPlan = repayPlanRepo.findByDueBillNoAndTerm(repayPlanReq.getDueBillNo(), repayPlanReq.getTerm());
        String[] args = {"projectNo", "productNo", "termStartDate", "termDueDate", "termPrin", "termInt"};
        BeanUtils.copyProperties(repayPlanReq, repayPlan, args);
        repayPlanRepo.save(repayPlan.setLastModifiedDate(DateUtils.getLocalDateTime(repayPlanReq.getBatchDate())));
        return Result.result(true);
    }

    @Override
    public Result updateRepaySummary(RepaySummaryReq repaySummaryReq) {
        RepaySummary repaySummary = repaySummaryRepo.findByDueBillNo(repaySummaryReq.getDueBillNo());
        String[] args = {"projectNo", "productNo", "userId", "contractAmount", "loanDate", "repayDay","totalTerm"};
        BeanUtils.copyProperties(repaySummaryReq, repaySummary, args);
        repaySummaryRepo.save(repaySummary.setLastModifiedDate(DateUtils.getLocalDateTime(repaySummaryReq.getBatchDate())));
        return null;
    }
}
