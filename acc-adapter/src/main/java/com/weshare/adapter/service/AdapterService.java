package com.weshare.adapter.service;

import com.weshare.adapter.feignCilent.LoanFeignClient;
import com.weshare.adapter.feignCilent.RepayFeignClient;
import com.weshare.service.api.client.LoanClient;
import com.weshare.service.api.entity.*;
import com.weshare.service.api.enums.*;
import com.weshare.service.api.result.Result;
import com.weshare.service.api.vo.Tuple2;
import com.weshare.service.api.vo.Tuple3;
import com.weshare.service.api.vo.Tuple4;
import common.*;
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
                            .setLoanStatusEnum(ChangeEnumUtils.changeEnum(ProjectEnum.YXMS.getProjectNo(), "loanStatusEnum", req.getLoanStatus(), LoanStatusEnum.class))
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
        //修改loan_contract表的放款状态
        loanFeignClient.UpdateLoanContractStatus(
                list.stream().map(e -> new LoanClient.UpdateLoanContractStatus()
                        .setBatchDate(e.getBatchDate().toString())
                        .setDueBillNo(e.getDueBillNo())
                ).collect(Collectors.toList())
        );
        //新增一条放款流水
        loanFeignClient.saveAllLoanTransFlow(
                list.stream().map(e -> new LoanTransFlowReq()
                        .setBatchDate(e.getBatchDate())
                        .setProjectNo(ProjectEnum.YXMS.getProjectNo())
                        .setProductNo(ProjectEnum.YXMS.getProducts().get(0).getProductNo())
                        .setDueBillNo(e.getDueBillNo())
                        .setFlowSn(SnowFlake.getInstance().nextId() + "")
                        .setTransAmount(e.getLoanAmount())
                        .setRemark("退票还款流水")
                        .setTransTime(DateUtils.getLocalDateTime(e.getBatchDate()))
                ).collect(Collectors.toList()), batchDate);
        return Result.result(true);
    }

    public Result createAllRepayTransFlow(List<? extends RebackDetailReq> list, String batchDate) {
        list = list.stream().filter(e -> e.getTransactionResult().equals(StatusEnum.成功.getCode())).collect(Collectors.toList());
        repayFeignClient.saveAllRepayTransFlow(
                list.stream().map(e -> {
                    RepayTransFlowReq repayTransFlowReq = new RepayTransFlowReq();
                    repayTransFlowReq
                            .setProjectNo(ProjectEnum.YXMS.getProjectNo())
                            .setProductNo(ProjectEnum.YXMS.getProducts().get(0).getProductNo())
                            .setFlowSn(SnowFlake.getInstance().nextId() + "")
                            .setDueBillNo(e.getDueBillNo())
                            .setTransFlowType(e.getTransFlowType().name())
                            .setTransAmount(e.getDebitAmount())
                            .setTransTime(e.getDebitDate())
                            .setTransStatus(ChangeEnumUtils.changeEnum(ProjectEnum.YXMS.getProjectNo(), "transactionResult", e.getTransactionResult(), RebackDetailReq.TransactionResult.class).name())
                            .setRemark(e.getTransFlowType().name())
                            .setBatchDate(e.getBatchDate());
                    return repayTransFlowReq;
                }).collect(Collectors.toList()), batchDate
        );
        return Result.result(true);
    }

    public Result createAllReceiptDetail(List<? extends RepaymentDetailReq> list, String batchDate) {
        Map<String, ? extends List<? extends RepaymentDetailReq>> map = list.stream().collect(Collectors.groupingBy(RepaymentDetailReq::getDueBillNo));
        for (Map.Entry<String, ? extends List<? extends RepaymentDetailReq>> entry : map.entrySet()) {
            List<ReceiptDetailReq> receiptDetailReqList = new ArrayList<>();
            String dueBillNo = entry.getKey();
            List<? extends RepaymentDetailReq> repaymentDetailReqs = entry.getValue();
            //根据借据号和时间查出当天所有的还款流水（返回借据号,还款金额,流水号）
            List<Tuple3<String, String, BigDecimal>> tuple3s = repayFeignClient.getFlowSn(dueBillNo, batchDate).getData();
            Integer totalTerm = repayFeignClient.getTotalTerm(dueBillNo, ProjectEnum.YXMS.getProjectNo()).getData();
            for (RepaymentDetailReq repaymentDetailReq : repaymentDetailReqs) {
                //根据借据号和扣款金额匹配上的取出流水号,并从该集合中移除掉
                Tuple3<String, String, BigDecimal> tuple3 = tuple3s.stream().
                        filter(e -> e.getFirst().equals(repaymentDetailReq.getDueBillNo())
                                && e.getThird().compareTo(repaymentDetailReq.getRepaymentAmount()) == 0)
                        .findFirst().orElse(null);
                String flowSn = tuple3.getSecond();
                tuple3s.remove(tuple3);
                receiptDetailReqList.addAll(
                        createReceiptReqList(repaymentDetailReq).stream().peek(e ->
                                e.setTotalTerm(totalTerm).setFlowSn(flowSn)).collect(Collectors.toList())
                );
            }
            repayFeignClient.saveAllReceiptDetail(receiptDetailReqList, batchDate);
            //更新还款计划，一次根据借据号查出所有的期次还款计划
            List<Tuple4<BigDecimal, BigDecimal, LocalDate, Integer>> tuple4s = repayFeignClient.getRepayPlanFourth(dueBillNo).getData();
            for (Map.Entry<Integer, List<ReceiptDetailReq>> entity : receiptDetailReqList.
                    stream().collect(Collectors.groupingBy(ReceiptDetailReq::getTerm)).entrySet()) {
                Integer term = entity.getKey();
                //List<ReceiptDetailReq> receiptDetailReqs = entity.getValue();
                Tuple4<BigDecimal, BigDecimal, LocalDate, Integer> tuple4 = tuple4s.stream()
                        .filter(e -> e.getFourth().equals(term)).findFirst().orElse(null);
                BigDecimal termBillAmount = tuple4.getFirst();//本期账单应还金额
                BigDecimal termPrin = tuple4.getSecond();//本期账单的应还本金
                LocalDate termDueDate = tuple4.getThird();//本期账单应还日
                //一次根据借据号和期次查出实还表中费用类型的金额(幂等性)
                List<Tuple2<BigDecimal, FeeTypeEnum>> tuple2s = repayFeignClient.getReceiptDetailTwo(dueBillNo, term).getData();
                RepayPlanReq repayPlanReq = new RepayPlanReq();
                repayPlanReq.setDueBillNo(dueBillNo);
                repayPlanReq.setTerm(term);
                repayPlanReq.setBatchDate(LocalDate.parse(batchDate));
                repayPlanReq.setTermRepayPrin(getAmount(tuple2s, FeeTypeEnum.PRINCIPAL));//已还本金
                repayPlanReq.setTermRepayInt(getAmount(tuple2s, FeeTypeEnum.INTEREST));//已还利息
                repayPlanReq.setTermRepayPenalty(getAmount(tuple2s, FeeTypeEnum.PENALTY));//已还罚息
                repayPlanReq.setTermReduceInt(getAmount(tuple2s, FeeTypeEnum.REDUCE_INTEREST));//减免利息
                repayPlanReq.setTermPenalty(getAmount(tuple2s, FeeTypeEnum.PENALTY));//应还罚息
                repayPlanReq.setTermBillAmount(termBillAmount.add(repayPlanReq.getTermPenalty()));//应还金额
                repayPlanReq.setTermStatus(getTermStatus(repayPlanReq.getTermBillAmount(), tuple2s.stream().map(Tuple2::getFirst).reduce(BigDecimal.ZERO, BigDecimal::add), termDueDate, batchDate));//期次状态
                repayPlanReq.setRepayDate(repayPlanReq.getTermStatus() == TermStatusEnum.REPAID ? LocalDate.parse(batchDate) : null);//已还日
                repayPlanReq.setTermPaidOutType(getTermPaidOutType(repayPlanReq.getTermStatus(), repayPlanReq.getRepayDate(), termDueDate));//还清类型
                repayPlanReq.setRemark(repayPlanReq.getTermPaidOutType() == null ? "本期次未结清" : repayPlanReq.getTermPaidOutType().getDesc());//备注
                repayFeignClient.updateRepayPlan(repayPlanReq);
            }
            //更新summary表
            List<RepayPlanReq> planReqList = repayFeignClient.findRepayPlanListByDueBillNo(dueBillNo).getData();
            LocalDate firstDate = planReqList.stream().map(RepayPlanReq::getTermDueDate).min(LocalDate::compareTo).orElse(null);
            LocalDate lastDate = planReqList.stream().map(RepayPlanReq::getTermDueDate).max(LocalDate::compareTo).orElse(null);
            RepaySummaryReq repaySummaryReq = new RepaySummaryReq();
            repaySummaryReq.setDueBillNo(dueBillNo);//借据号
            repaySummaryReq.setBatchDate(LocalDate.parse(batchDate));
            repaySummaryReq.setAssetStatus(getAssetStatus(planReqList.stream()
                    .map(RepayPlanReq::getTermStatus).collect(Collectors.toList())));//资产状态
            repaySummaryReq.setReturnTerm((int) planReqList.stream()
                    .filter(e -> e.getTermStatus() == TermStatusEnum.REPAID).count());//已还期次
            repaySummaryReq.setRemainPrincipal(planReqList.stream().map(e -> e.getTermPrin()
                    .subtract(e.getTermRepayPrin())).reduce(BigDecimal.ZERO, BigDecimal::add));//剩余本金
            repaySummaryReq.setRemainInterest(planReqList.stream().map(e -> e.getTermInt()
                    .subtract(e.getTermRepayInt()).subtract(e.getTermReduceInt())).reduce(BigDecimal.ZERO, BigDecimal::add));//剩余利息
            repaySummaryReq.setCurrentTerm(StringUtils.getCurrentTerm(firstDate, lastDate, LocalDate.parse(batchDate), totalTerm));//当前期次
            repaySummaryReq.setCurrentTermDueDate(planReqList.stream().filter(e -> e.getTerm().equals(repaySummaryReq.getCurrentTerm()))
                    .map(RepayPlanReq::getTermDueDate).findFirst().orElse(null));//当前期次的应还日
            repaySummaryReq.setCurrentPaidOutDate(planReqList.stream().filter(e -> e.getRepayDate() != null &&
                    e.getTerm().equals(repaySummaryReq.getCurrentTerm())).map(RepayPlanReq::getRepayDate).findFirst().orElse(null));//当前期次结清日
            repaySummaryReq.setSettleDate(planReqList.stream().allMatch(e -> e.getRepayDate() != null) ?
                    planReqList.stream().map(RepayPlanReq::getRepayDate).max(LocalDate::compareTo).orElse(null) : null);//结清日期
            repaySummaryReq.setSettleType(getSettleType(planReqList, repaySummaryReq.getAssetStatus()));//结清类型
            repaySummaryReq.setRemark(repaySummaryReq.getAssetStatus().getDesc());
            repayFeignClient.updateRepaySummary(repaySummaryReq);
        }
        return Result.result(true);
    }

    @Deprecated
    private SettleTypeEnum getSettleType(List<RepayPlanReq> planReqList, AssetStatusEnum assetStatus) {
        switch (assetStatus) {
            case SETTLED:
                List<LocalDate> termDueDateList = planReqList.stream().map(RepayPlanReq::getTermDueDate).sorted(Comparator.reverseOrder()).collect(Collectors.toList());
                List<LocalDate> repayDateList = planReqList.stream().map(RepayPlanReq::getRepayDate).sorted(Comparator.reverseOrder()).collect(Collectors.toList());
                if (repayDateList.get(0).compareTo(termDueDateList.get(1)) < 0) {
                    //实还日期的最后一期<应还日期的倒数第二期
                    return SettleTypeEnum.PRE_SETTLE;
                }
                if (repayDateList.get(0).compareTo(termDueDateList.get(1)) >= 0
                        && repayDateList.get(0).compareTo(termDueDateList.get(0)) <= 0) {
                    // 应还日期的倒数第二期 =<实还日期的最后一期<=应还日期的倒数第一期
                    return SettleTypeEnum.NORMAL_SETTLE;
                }
                if (repayDateList.get(0).compareTo(termDueDateList.get(0)) >= 0) {
                    //实还日期的最后一期>应还日期的倒数第一期
                    return SettleTypeEnum.OVERDUE_SETTLE;
                }
        }
        return null;
    }

    private AssetStatusEnum getAssetStatus(List<TermStatusEnum> termStatusEnums) {

        if (termStatusEnums.stream().anyMatch(e -> e == TermStatusEnum.UNDUE)
                && termStatusEnums.stream().noneMatch(e -> e == TermStatusEnum.OVERDUE)) {
            //期次状态有正常或已还的并且没有一个逾期的,资产状态就是正常
            return AssetStatusEnum.NORMAL;
        }
        if (termStatusEnums.stream().anyMatch(e -> e == TermStatusEnum.OVERDUE)) {
            //期次状态有一个逾期,资产状态就逾期
            return AssetStatusEnum.OVERDUE;
        }
        if (termStatusEnums.stream().allMatch(e -> e == TermStatusEnum.REPAID)) {
            return AssetStatusEnum.SETTLED;
        }
        return null;
    }


    private TermPaidOutTypeEnum getTermPaidOutType(TermStatusEnum termStatus, LocalDate repayDate, LocalDate termDueDate) {
        if (termStatus == TermStatusEnum.REPAID) {
            if (repayDate.isBefore(termDueDate)) {
                return TermPaidOutTypeEnum.PRE_PAIDOUT;
            }
            if (repayDate.isEqual(termDueDate)) {
                return TermPaidOutTypeEnum.NORMAL_PAIDOUT;
            }
            if (repayDate.isAfter(termDueDate)) {
                return TermPaidOutTypeEnum.OVERDUE_PAIDOUT;
            }
        }
        return null;
    }

    private TermStatusEnum getTermStatus(BigDecimal termBillAmount, BigDecimal receiptAmount, LocalDate termDueDate, String batchDate) {
        LocalDate localDate = LocalDate.parse(batchDate);
        if (localDate.isAfter(termDueDate)) {//实际还款日期大于等于应还日期
            if (receiptAmount.compareTo(termBillAmount) >= 0) {//实际已还金额大于等于应还金额
                return TermStatusEnum.REPAID;
            } else {
                return TermStatusEnum.OVERDUE;
            }
        } else {//实际还款日期小于应还日期
            if (receiptAmount.compareTo(termBillAmount) >= 0) {//实际已还金额大于等于应还金额
                return TermStatusEnum.REPAID;
            } else {
                return TermStatusEnum.UNDUE;
            }
        }
    }

    private BigDecimal getAmount(List<Tuple2<BigDecimal, FeeTypeEnum>> tuple2s, FeeTypeEnum feeTypeEnum) {
        return tuple2s.stream().filter(e -> e.getSecond() == feeTypeEnum).map(Tuple2::getFirst).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<ReceiptDetailReq> createReceiptReqList(RepaymentDetailReq req) {

        List<ReceiptDetailReq> list = new ArrayList<>();
        if (req.getPrincipal().compareTo(BigDecimal.ZERO) > 0) {
            list.add(
                    createReceiptReq(req)
                            .setAmount(req.getPrincipal())
                            .setFeeType(FeeTypeEnum.PRINCIPAL)
            );
        }
        if (req.getInterest().compareTo(BigDecimal.ZERO) > 0) {
            list.add(
                    createReceiptReq(req)
                            .setAmount(req.getInterest())
                            .setFeeType(FeeTypeEnum.INTEREST)
            );
        }
        if (req.getPenalty().compareTo(BigDecimal.ZERO) > 0) {
            list.add(
                    createReceiptReq(req)
                            .setAmount(req.getPenalty())
                            .setFeeType(FeeTypeEnum.PENALTY)
            );
        }
        if (req.getReduceInterest().compareTo(BigDecimal.ZERO) > 0) {
            list.add(
                    createReceiptReq(req)
                            .setAmount(req.getReduceInterest())
                            .setFeeType(FeeTypeEnum.REDUCE_INTEREST)
            );
        }
        return list;
    }

    private ReceiptDetailReq createReceiptReq(RepaymentDetailReq req) {
        ReceiptDetailReq receiptDetailReq = new ReceiptDetailReq();
        receiptDetailReq
                .setProjectNo(ProjectEnum.YXMS.getProjectNo())
                .setProductNo(ProjectEnum.YXMS.getProducts().get(0).getProductNo())
                .setBatchDate(req.getBatchDate())
                .setDueBillNo(req.getDueBillNo())
                .setTerm(req.getTerm())
                .setRepayDate(req.getTradeDate().toLocalDate())
                .setReceiptType(getReceiptType(req.getDebitType()))
                .setRemark(receiptDetailReq.getReceiptType().getDesc());
        return receiptDetailReq;
    }

    private ReceiptTypeEnum getReceiptType(String debitType) {
        switch (debitType) {
            case "01":
                return ReceiptTypeEnum.NORMAL;
            case "02":
                return ReceiptTypeEnum.PRE;
            case "03":
                return ReceiptTypeEnum.OVERDUE;
            case "04":
                return ReceiptTypeEnum.REDUCE;
            case "05":
                return ReceiptTypeEnum.REFUND;
            default:
                return null;
        }
    }

    private ReceiptDetailReq getReceiptDetail(RepayTransFlowReq repayTransFlow, int term, int totalTerm) {

        return new ReceiptDetailReq()
                .setProductNo(repayTransFlow.getProductNo())
                .setProjectNo(repayTransFlow.getProjectNo())
                .setDueBillNo(repayTransFlow.getDueBillNo())
                .setTotalTerm(totalTerm)
                .setTerm(term)
                .setAmount(repayTransFlow.getTransAmount())
                .setFeeType(FeeTypeEnum.PRINCIPAL)
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
                .setTransStatus(TransStatusEnum.成功.name())
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
