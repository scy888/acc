package com.weshare.adapter.service;

import com.weshare.adapter.feignCilent.LoanFeignClient;
import com.weshare.adapter.feignCilent.RepayFeignClient;
import com.weshare.service.api.entity.*;
import com.weshare.service.api.enums.*;
import com.weshare.service.api.result.Result;
import common.SnowFlake;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.adapter.service
 * @date: 2021-05-28 14:04:33
 * @describe:
 */
@Service
@Slf4j
public class AdapterService {

    @Autowired
    private LoanFeignClient loanFeignClient;
    @Autowired
    private RepayFeignClient repayFeignClient;

    public Result saveAllLoanContractAndLoanTransFlowAndRepaySummary(List<? extends LoanDetailReq> list, String batchDate) {

        //保存acc_loan.loan_contract
        loanFeignClient.saveAllLoanContract(
                list.stream().filter(e -> e.getLoanStatus().equals(StatusEnum.成功.getCode())).map(req -> {
                    LoanContractReq loanContractReq = new LoanContractReq();
                    return loanContractReq
                            .setDueBillNo(req.getDueBillNo())
                            .setProjectNo(ProjectEnum.YXMS.getProjectNo())
                            .setProductNo(ProjectEnum.YXMS.getProducts().get(0).getProductNo())
                            .setProductName(ProjectEnum.getProductName(loanContractReq.getProjectNo(), loanContractReq.getProductNo()))
                            .setContractAmount(req.getLoanAmount())
                            .setInterestRate(new BigDecimal("0.02"))
                            .setTotalTerm(req.getTerm())
                            .setPrincipal(req.getLoanAmount())
                            .setBatchDate(req.getBatchDate())
                            .setRemark("放款成功");
                }).collect(Collectors.toList())
        );

        //保存acc_loan.loan_trans_flow
        loanFeignClient.saveAllLoanTransFlow(
                list.stream().filter(e -> e.getLoanStatus().equals(StatusEnum.成功.getCode())).map(req -> {
                    LoanTransFlowReq loanTransFlowReq = new LoanTransFlowReq();
                    LocalDate localDate = req.getBatchDate();
                    LocalDateTime localDateTime = LocalDateTime.now().withYear(localDate.getYear()).withMonth(localDate.getMonthValue()).withDayOfMonth(localDate.getDayOfMonth());
                    loanTransFlowReq
                            .setFlowSn(SnowFlake.getInstance().nextId() + "")
                            .setProjectNo(ProjectEnum.YXMS.getProjectNo())
                            .setProductNo(ProjectEnum.YXMS.getProducts().get(0).getProductNo())
                            .setDueBillNo(req.getDueBillNo())
                            .setTransAmount(req.getLoanAmount())
                            .setTransTime(localDateTime)
                            .setRemark("放款交易流水成功")
                            .setBatchDate(req.getBatchDate());
                    return loanTransFlowReq;
                }).collect(Collectors.toList()), batchDate
        );

        //保存acc_repay.repay_summary
        repayFeignClient.saveRepaySummary(
                list.stream().filter(e -> e.getLoanStatus().equals(StatusEnum.成功.getCode())).map(e -> {
                    RepaySummaryReq summaryReq = new RepaySummaryReq();
                    summaryReq.setDueBillNo(e.getDueBillNo());
                    summaryReq.setProjectNo(ProjectEnum.YXMS.getProjectNo());
                    summaryReq.setProductNo(ProjectEnum.YXMS.getProducts().get(0).getProductNo());
                    summaryReq.setContractAmount(e.getLoanAmount());
                    summaryReq.setLoanDate(e.getLoanDate());
                    summaryReq.setTotalTerm(e.getTerm());
                    summaryReq.setReturnTerm(0);
                    summaryReq.setRemainPrincipal(e.getLoanAmount());
                    summaryReq.setBatchDate(e.getBatchDate());
                    summaryReq.setAssetStatus(AssetStatusEnum.NORMAL);
                    return summaryReq;
                }).collect(Collectors.toList())
        );
        return Result.result(true);
    }

    public Result saveAllRepayPlanUpdateLoanContractAndRepaySummary(@RequestBody List<? extends RepaymentPlanReq> list) {

        List<RepayPlanReq> planReqList = list.stream().map(e -> {
            RepayPlanReq planReq = new RepayPlanReq();
            planReq.setDueBillNo(e.getDueBillNo());
            planReq.setTerm(e.getTerm());
            planReq.setProjectNo(ProjectEnum.YXMS.getProjectNo());
            planReq.setProductNo(ProjectEnum.YXMS.getProducts().get(0).getProductNo());
            planReq.setTermStatus(TermStatusEnum.UNDUE);
            planReq.setTermStartDate(e.getRepaymentDate().plusDays(1));
            planReq.setTermDueDate(e.getRepaymentDate());
            planReq.setTermBillAmount(e.getShouldMonthMoney());
            planReq.setTermPrin(e.getShouldCapitalMoney());
            planReq.setTermInt(e.getShouldInterestMoney());
            planReq.setTermPenalty(BigDecimal.ZERO);
            planReq.setTermRepayPrin(BigDecimal.ZERO);
            planReq.setTermRepayInt(BigDecimal.ZERO);
            planReq.setTermRepayPenalty(BigDecimal.ZERO);
            planReq.setTermReduceInt(BigDecimal.ZERO);
            planReq.setBatchDate(e.getBatchDate());
            return planReq;
        }).collect(Collectors.toList());
        repayFeignClient.saveRepayPlan(planReqList);

        //补充loan_contract,repay_summary 的部分字段
        Map<String, ? extends List<? extends RepayPlanReq>> map = planReqList.stream().collect(Collectors.groupingBy(RepayPlanReq::getDueBillNo));
        List<LoanContractReq> contractReqList = new ArrayList<>();
        List<RepaySummaryReq> summaryReqList = new ArrayList<>();
        for (Map.Entry<String, ? extends List<? extends RepayPlanReq>> entry : map.entrySet()) {
            String dueBillNo = entry.getKey();
            List<? extends RepayPlanReq> reqs = entry.getValue();
            List<LoanContractReq> loanContractReqs = loanFeignClient.findLoanContractByDueBillNoIn(List.of(dueBillNo)).getData();
            LoanContractReq loanContractReq = loanContractReqs.get(0);
            loanContractReq.setFirstTermDueDate(reqs.stream().map(RepayPlanReq::getTermDueDate).min(LocalDate::compareTo).orElse(null));
            loanContractReq.setLastTermDueDate(reqs.stream().max(Comparator.comparing(RepayPlanReq::getTermDueDate)).map(RepayPlanReq::getTermDueDate).orElse(null));
            loanContractReq.setRepayDay(Objects.requireNonNull(reqs.stream().map(RepayPlanReq::getTermDueDate).findAny().orElse(null)).getDayOfMonth());
            loanContractReq.setInterest(reqs.stream().map(RepayPlanReq::getTermInt).reduce(BigDecimal.ZERO, BigDecimal::add));
            contractReqList.add(loanContractReq);
            /*****************************************************************/
            List<RepaySummaryReq> reqList = repayFeignClient.findRepaySummaryByDueBillNoIn(List.of(dueBillNo)).getData();
            RepaySummaryReq repaySummaryReq = reqList.get(0);
            repaySummaryReq.setUserId(loanContractReq.getUserId());
            repaySummaryReq.setRepayDay(loanContractReq.getRepayDay());
            repaySummaryReq.setRemainInterest(loanContractReq.getInterest());
            summaryReqList.add(repaySummaryReq);
        }
        loanFeignClient.saveAllLoanContract(contractReqList);
        repayFeignClient.saveRepaySummary(summaryReqList);
        return Result.result(true);
    }

    public Result saveRefundDownRepayTransFlowAndReceiptDetail(List<? extends RefundTicketReq> list, String batchDate) {
        list = list.stream().filter(e -> e.getRefundStatus().equals(StatusEnum.成功.getCode())).collect(Collectors.toList());
        for (RefundTicketReq refundTicketReq : list) {
            //退票要根据还款计划拆分,只虚拟还本金,不还利息
            List<RepayPlanReq> repayPlanReqList = repayFeignClient.findRepayPlanListByDueBillNo(refundTicketReq.getDueBillNo()).getData();
            List<RepayTransFlowReq> repayTransFlowReqList = new ArrayList<>();
            List<ReceiptDetailReq> receiptDetailReqList = new ArrayList<>();
            for (RepayPlanReq repayPlanReq : repayPlanReqList) {
                //生成虚拟还还款流水
                RepayTransFlowReq repayTransFlow = getRepayTransFlow(refundTicketReq, repayPlanReq);
                repayTransFlowReqList.add(repayTransFlow);
                //生成虚拟实还记录
                ReceiptDetailReq receiptDetailReq = getReceiptDetail(repayTransFlow, repayPlanReq.getTerm(), repayPlanReqList.size());
                receiptDetailReqList.add(receiptDetailReq);
            }
            repayFeignClient.saveAllRepayTransFlow(repayTransFlowReqList, refundTicketReq.getBatchDate().toString());
            repayFeignClient.saveAllReceiptDetail(receiptDetailReqList, refundTicketReq.getBatchDate().toString());
            //更新还款计划
            repayFeignClient.saveRepayPlan(repayPlanReqList.stream().map(e -> {
                RepayPlanReq repayPlanReq = new RepayPlanReq();
                BeanUtils.copyProperties(e, repayPlanReq);
                return e.setTermRepayPrin(e.getTermPrin())
                        .setRepayDate(refundTicketReq.getRefundDate().toLocalDate())
                        .setTermStatus(TermStatusEnum.REPAID)
                        .setRemark("退票只还本期本金")
                        .setTermPaidOutType(TermPaidOutTypeEnum.REFUND_PAIDOUT)
                        .setBatchDate(refundTicketReq.getBatchDate());
            }).collect(Collectors.toList()));
        }
        //更新repay_summary
        List<RepaySummaryReq> repaySummaryReqList = repayFeignClient.findRepaySummaryByDueBillNoIn(list.stream().map(RefundTicketReq::getDueBillNo)
                .collect(Collectors.toList())).getData();
        repayFeignClient.saveRepaySummary(
                repaySummaryReqList.stream().map(e -> {
                    RepaySummaryReq repaySummaryReq = new RepaySummaryReq();
                    BeanUtils.copyProperties(e, repaySummaryReq);
                    return repaySummaryReq
                            .setAssetStatus(AssetStatusEnum.SETTLED)
                            .setCurrentPaidOutDate(LocalDate.parse(batchDate))
                            .setSettleDate(LocalDate.parse(batchDate))
                            .setReturnTerm(e.getTotalTerm())
                            .setRemainPrincipal(BigDecimal.ZERO)
                            .setRemainInterest(BigDecimal.ZERO)
                            .setSettleType(SettleTypeEnum.RETURN_SETTLE)
                            .setRemark("退票结清")
                            .setBatchDate(LocalDate.parse(batchDate));
                }).collect(Collectors.toList())
        );

        return Result.result(true);
    }

    private ReceiptDetailReq getReceiptDetail(RepayTransFlowReq repayTransFlow, int term, int totalTerm) {

        return new ReceiptDetailReq()
                .setProductNo(repayTransFlow.getProductNo())
                .setProjectNo(repayTransFlow.getProjectNo())
                .setDueBillNo(repayTransFlow.getDueBillNo())
                .setTotalTerm(totalTerm)
                .setTerm(term)
                .setAmount(repayTransFlow.getTransAmount())
                .setFeeType(FeeTypeEnum.PRICINPAL)
                .setReceiptType(ReceiptTypeEnum.REFUND)
                .setFlowSn(repayTransFlow.getFlowSn())
                .setRepayDate(repayTransFlow.getTransTime().toLocalDate())
                .setRemark("退票结清")
                .setBatchDate(repayTransFlow.getBatchDate());
    }

    private RepayTransFlowReq getRepayTransFlow(RefundTicketReq refundTicketReq, RepayPlanReq repayPlanReq) {
        return new RepayTransFlowReq()
                .setProjectNo(ProjectEnum.YXMS.getProjectNo())
                .setProductNo(ProjectEnum.YXMS.getProducts().get(0).getProductNo())
                .setFlowSn(SnowFlake.getInstance().nextId() + "")
                .setDueBillNo(refundTicketReq.getDueBillNo())
                .setTransFlowType(TransFlowTypeEnum.退票.name())
                .setTransAmount(repayPlanReq.getTermPrin())
                .setTransTime(refundTicketReq.getRefundDate())
                .setTransStatus("成功")
                .setRemark("退票")
                .setBatchDate(refundTicketReq.getBatchDate());
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
