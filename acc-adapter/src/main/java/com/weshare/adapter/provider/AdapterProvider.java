package com.weshare.adapter.provider;

import com.weshare.adapter.dao.AdapterDao;
import com.weshare.adapter.entity.LoanDetail;
import com.weshare.adapter.entity.RefundTicket;
import com.weshare.adapter.entity.RepaymentPlan;
import com.weshare.adapter.repo.LoanDetailRepo;
import com.weshare.adapter.repo.RefundTicketRepo;
import com.weshare.adapter.service.AdapterService;
import com.weshare.service.api.client.AdapterClient;
import com.weshare.service.api.entity.LoanDetailReq;
import com.weshare.service.api.entity.RefundTicketReq;
import com.weshare.service.api.entity.RepaymentPlanReq;
import com.weshare.service.api.enums.ProjectEnum;
import com.weshare.service.api.result.Result;
import common.ChangeEnumUtils;
import common.DateUtils;
import common.SnowFlake;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;
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
 * @package: com.weshare.adapter.provider
 * @date: 2021-05-27 10:42:56
 * @describe:
 */
@RestController
@Slf4j
public class AdapterProvider implements AdapterClient {

    @Autowired
    private LoanDetailRepo loanDetailRepo;
    @Autowired
    private RefundTicketRepo refundTicketRepo;
    @Autowired
    private AdapterService adapterService;
    @Autowired
    private AdapterDao adapterDao;

    @Transactional
    @Override
    @Async
    public Result saveAllLoanDetail(List<? extends LoanDetailReq> list) {
        log.info("saveAllLoanDetail()方法的异步调用的线程名:{}", Thread.currentThread().getName());
        loanDetailRepo.deleteByDueBillNoList(list.stream().map(LoanDetailReq::getDueBillNo).collect(Collectors.toList()));
        loanDetailRepo.saveAll(
                list.stream().map(e -> {
                    LoanDetail loanDetail = new LoanDetail();
                    BeanUtils.copyProperties(e, loanDetail);
                    loanDetail.setId(SnowFlake.getInstance().nextId() + "")
                            .setLoanStatus(ChangeEnumUtils.changeEnum("WS121212", "loanStatus", e.getLoanStatus(), LoanDetail.LoanStatusEnum.class))
                            .setCreatedDate(LocalDateTime.now().withYear(((LoanDetailReq) e).getBatchDate().getYear()).withMonth(((LoanDetailReq) e).getBatchDate().getMonthValue()).withDayOfMonth(((LoanDetailReq) e).getBatchDate().getDayOfMonth()))
                            .setLastModifiedDate(loanDetail.getCreatedDate());
                    return loanDetail;
                }).collect(Collectors.toSet())
        );
        //int a = 2 / 0;
        return Result.result(true);
    }

    @Override
    @Async
    public Result saveAllRepaymentPlan(List<? extends RepaymentPlanReq> list) {
        log.info("saveAllRepaymentPlan()方法的异步调用的线程名:{}", Thread.currentThread().getName());
        List<RepaymentPlan> repaymentPlanReqList = adapterDao.findByDueBillNoAndTerm(list);
        Map<String, RepaymentPlan> map = repaymentPlanReqList.stream().collect(Collectors.toMap(e -> e.getDueBillNo() + "_" + e.getTerm(), Function.identity()));

        for (RepaymentPlanReq planReq : list) {
            RepaymentPlan repaymentPlan = map.get(planReq.getDueBillNo() + "_" + planReq.getTerm());
            Optional.ofNullable(repaymentPlan).ifPresentOrElse(e -> {
                BeanUtils.copyProperties(planReq, e);
                LocalDate batchDate = e.getBatchDate();
                LocalDateTime localDateTime = LocalDateTime.now().withYear(batchDate.getYear()).withMonth(batchDate.getMonthValue()).withDayOfMonth(batchDate.getDayOfMonth());
                adapterDao.updateRepaymentPlan(e.setLastModifiedDate(localDateTime));
            }, () -> {
                RepaymentPlan plan = new RepaymentPlan();
                BeanUtils.copyProperties(planReq, plan);
                LocalDate batchDate = plan.getBatchDate();
                LocalDateTime localDateTime = LocalDateTime.now().withYear(batchDate.getYear()).withMonth(batchDate.getMonthValue()).withDayOfMonth(batchDate.getDayOfMonth());
                adapterDao.insertRepaymentPlan(plan.setId(SnowFlake.getInstance().nextId() + "")
                        .setCreatedDate(localDateTime)
                        .setLastModifiedDate(localDateTime));
            });
        }
        return Result.result(true);
    }

    @Override
    public void saveAllLoanContractAndLoanTransFlowAndRepaySummary(List<? extends LoanDetailReq> list, String batchDate) {
        adapterService.saveAllLoanContractAndLoanTransFlowAndRepaySummary(list, batchDate);
    }

    @Override
    public void saveAllRepayPlanUpdateLoanContractAndRepaySummary(List<? extends RepaymentPlanReq> list) {
        adapterService.saveAllRepayPlanUpdateLoanContractAndRepaySummary(list);
    }

    @Override
    public Result saveAllRefundTicket(List<? extends RefundTicketReq> list) {
        for (RefundTicketReq refundTicketReq : list) {
            String dueBillNo = refundTicketReq.getDueBillNo();
            RefundTicket.RefundStatusEnum refundStatus = ChangeEnumUtils.changeEnum(ProjectEnum.YXMS.getProjectNo(),
                    "refundStatus", refundTicketReq.getRefundStatus(), RefundTicket.RefundStatusEnum.class);
            RefundTicket refundTicket = refundTicketRepo.findByDueBillNoAndRefundStatus(dueBillNo, refundStatus);
            refundTicket = Optional.ofNullable(refundTicket).orElseGet(() ->
                    new RefundTicket().setId(SnowFlake.getInstance().nextId() + "")
                            .setCreateDate(DateUtils.getLocalDateTime(refundTicketReq.getBatchDate()))
                            .setRefundStatus(refundStatus));
            BeanUtils.copyProperties(refundTicketReq, refundTicket, "refundStatus");
            refundTicketRepo.save(refundTicket
                    .setLastModifiedDate(DateUtils.getLocalDateTime(refundTicketReq.getBatchDate())));
        }
        return Result.result(true);
    }
}
