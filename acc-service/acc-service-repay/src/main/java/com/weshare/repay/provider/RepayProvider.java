package com.weshare.repay.provider;

import com.weshare.repay.entity.RepayPlan;
import com.weshare.repay.entity.RepaySummary;
import com.weshare.repay.repo.RepayPlanRepo;
import com.weshare.repay.repo.RepaySummaryRepo;
import com.weshare.service.api.client.RepayClient;
import com.weshare.service.api.entity.RepayPlanReq;
import com.weshare.service.api.entity.RepaySummaryReq;
import com.weshare.service.api.result.Result;
import common.ReflectUtils;
import common.SnowFlake;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.diff.myers.Snake;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
}
