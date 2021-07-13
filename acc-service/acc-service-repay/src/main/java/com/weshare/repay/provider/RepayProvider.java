package com.weshare.repay.provider;

import com.weshare.repay.dao.RepayDao;
import com.weshare.repay.entity.*;
import com.weshare.repay.repo.*;
import com.weshare.service.api.client.RepayClient;
import com.weshare.service.api.entity.*;
import com.weshare.service.api.enums.*;
import com.weshare.service.api.result.DataCheckResult;
import com.weshare.service.api.result.Result;
import com.weshare.service.api.vo.DueBillNoAndTermDueDate;
import com.weshare.service.api.vo.Tuple2;
import com.weshare.service.api.vo.Tuple3;
import com.weshare.service.api.vo.Tuple4;
import common.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
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
    @Autowired
    private PictureFileRepo pictureFileRepo;
    @Resource
    private RepayDao repayDao;

    public static RepayProvider repayProvider;

    @PostConstruct
    public void init() {
        repayProvider = this;
    }

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
        List<RepaySummaryReq> summaryReqList = ReflectUtil.getBeanUtils(repaySummaryList, RepaySummaryReq.class);
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
        List<RepayPlanReq> repayPlanReqList = ReflectUtil.getBeanUtils(repayPlanList, RepayPlanReq.class);
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
                Tuple4.of(repayPlan.getTermPrin().add(repayPlan.getTermInt()), repayPlan.getTermInt(), repayPlan.getTermDueDate(), repayPlan.getTerm())).collect(Collectors.toList());
        return Result.result(true, tuple4s);
    }

    @Override
    public Result updateRepayPlan(RepayPlanReq repayPlanReq) {
        RepayPlan repayPlan = repayPlanRepo.findByDueBillNoAndTerm(repayPlanReq.getDueBillNo(), repayPlanReq.getTerm());
        String[] args = {"projectNo", "productNo", "termStartDate", "termDueDate", "termPrin"};
        BeanUtils.copyProperties(repayPlanReq, repayPlan, args);
        repayPlanRepo.save(repayPlan.setLastModifiedDate(DateUtils.getLocalDateTime(repayPlanReq.getBatchDate())));
        return Result.result(true);
    }

    @Override
    public Result updateRepaySummary(RepaySummaryReq repaySummaryReq) {
        RepaySummary repaySummary = repaySummaryRepo.findByDueBillNo(repaySummaryReq.getDueBillNo());
        String[] args = {"projectNo", "productNo", "userId", "contractAmount", "loanDate", "repayDay", "totalTerm"};
        BeanUtils.copyProperties(repaySummaryReq, repaySummary, args);
        repaySummaryRepo.save(repaySummary.setLastModifiedDate(DateUtils.getLocalDateTime(repaySummaryReq.getBatchDate())));
        return null;
    }

    @Override
    public Result<List<Tuple2<BigDecimal, Integer>>> getRepayPlanTwo(String dueBillNo, Integer term) {
        List<RepayPlan> planList = repayPlanRepo.findByDueBillNoAndTermGreaterThanEqual(dueBillNo, term);
        List<Tuple2<BigDecimal, Integer>> tuple2s = planList.stream().map(e -> Tuple2.of(e.getTermPrin(), e.getTerm())).collect(Collectors.toList());
        return Result.result(true, tuple2s);
    }

    @Override
    public Result<List<RepayPlanReq>> getRepayPlan(String dueBillNo, TermStatusEnum termStatus) {
        List<RepayPlan> planList = repayPlanRepo.findByDueBillNoAndTermStatusNot(dueBillNo, termStatus);
        return Result.result(true, planList);
    }

    @Override
    public Result<List<RepayPlanReq>> getRepayPlanPage(PageReq pageReq) {

        Integer pageNum = pageReq.getPageNum();
        Integer pageSize = pageReq.getPageSize();
        String dueBillNo = pageReq.getDueBillNo();
        List<Integer> termList = pageReq.getTerms();
        LocalDate startDate = pageReq.getStartDate();
        LocalDate endDate = pageReq.getEndDate();
        TermStatusEnum termStatus = pageReq.getTermStatus();

        Specification<RepayPlan> spec = new Specification<RepayPlan>() {
            @Override
            public Predicate toPredicate(Root<RepayPlan> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicateList = new ArrayList<>();
                if (!StringUtils.isEmpyStr(dueBillNo)) {
                    predicateList.add(cb.equal(root.get("dueBillNo").as(String.class), dueBillNo));
                }
                if (!termList.isEmpty()) {
                    CriteriaBuilder.In<Object> in = cb.in(root.get("term"));
                    for (Integer integer : termList) {
                        in.value(integer);
                    }
                    predicateList.add(in);
                }
                if (termStatus != null) {
                    predicateList.add(cb.equal(root.get("termStatus").as(TermStatusEnum.class), termStatus));
                }
                if (startDate != null && endDate != null) {
                    predicateList.add(cb.between(root.get("repayDate").as(LocalDate.class), startDate, endDate));
                }
                return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));
            }
        };
        Page<RepayPlan> page = repayPlanRepo.findAll(spec, PageRequest.of(pageNum - 1, pageSize, Sort.Direction.DESC, "term"));
        log.info("page:{}", JsonUtil.toJson(page, true));
        List<RepayPlan> list = page.getContent();
        return Result.result(true, list);
    }

    @Override
    public Result addPictureFile(PictureFileReq pictureFileReq) {
        log.info("pictureFileReq:{}", JsonUtil.toJson(pictureFileReq, true));
        PictureFile pictureFile = new PictureFile();
        BeanUtils.copyProperties(pictureFileReq, pictureFile);
        pictureFileRepo.save(pictureFile.setId(UUID.randomUUID().toString().replaceAll("-", "")));
        return Result.result(true);
    }

    @Override
    public Result<PictureFileReq> viewPictureFile(String id) {
        Result<PictureFileReq> result = new Result();
        PictureFileReq pictureFileReq = new PictureFileReq();
        pictureFileRepo.findById(id).ifPresentOrElse(e -> {
            BeanUtils.copyProperties(e, pictureFileReq);
            result.setIsFlag(true);
            result.setData(pictureFileReq);
        }, () -> {
            log.info("该id:{},在表中不存在...", id);
            result.setIsFlag(true);
            result.setData(pictureFileReq);
        });
        return result;
    }

    @Override
    public Result<List<DataCheckResult>> checkDataResult(String projectNo) {
        List<DataCheckResult> list = new ArrayList<>();

        List<Tuple3<String, BigDecimal, BigDecimal>> tuple3s_1 = repayDao.checkLoanAmount(projectNo);
        //tuple3s_1.clear();
        list.add(
                DataCheckResult.dataCheckResult(DataCheckType.校验借款合同金额等于还款计划应还本金之和, tuple3s_1.size(),
                        tuple3s_1.stream().map(Tuple3::getFirst).collect(Collectors.toList()),
                        "放款金额:" + tuple3s_1.stream().map(Tuple3::getThird).reduce(BigDecimal.ZERO, BigDecimal::add) + "元," +
                                "应还本金金额之和:" + tuple3s_1.stream().map(Tuple3::getSecond).reduce(BigDecimal.ZERO, BigDecimal::add) + "元")
        );

        List<Tuple3<String, BigDecimal, BigDecimal>> tuple3s_2 = repayDao.checkRemainPrin(projectNo);
        //tuple3s_2.clear();
        list.add(
                DataCheckResult.dataCheckResult(DataCheckType.校验还款主信息剩余本金等于还款计划剩余本金之和, tuple3s_2.size(),
                        tuple3s_2.stream().map(Tuple3::getFirst).collect(Collectors.toList()),
                        "还款计划剩余本金额:" + tuple3s_2.stream().map(Tuple3::getSecond).reduce(BigDecimal::add).orElse(BigDecimal.ZERO) + "元," +
                                "还款主信息剩余本金额:" + tuple3s_2.stream().map(Tuple3::getThird).reduce(BigDecimal.ZERO, BigDecimal::add) + "元")
        );

        List<Tuple4<String, BigDecimal, BigDecimal, BigDecimal>> tuple4s_1 = repayDao.checkActualAmount(projectNo);
        //tuple4s_1.clear();
        list.add(
                DataCheckResult.dataCheckResult(DataCheckType.检验还款计划的实还金额等于还款流水或实还之和, tuple4s_1.size(),
                        tuple4s_1.stream().map(Tuple4::getFirst).collect(Collectors.toList()),
                        "还款计划的已还金额:" + tuple4s_1.stream().map(Tuple4::getSecond).reduce(BigDecimal.ZERO, BigDecimal::add) + "元," +
                                "还款流水金额:" + tuple4s_1.stream().map(Tuple4::getThird).reduce(BigDecimal.ZERO, BigDecimal::add) + "元," +
                                "实还金额:" + tuple4s_1.stream().map(Tuple4::getFourth).reduce(BigDecimal.ZERO, BigDecimal::add) + "元")
        );

        List<Tuple4<String, String, BigDecimal, BigDecimal>> tuple4s_2 = repayDao.checkFlowSn(projectNo);
        //tuple4s_2.clear();
        list.add(
                DataCheckResult.dataCheckResult(DataCheckType.校验还款流水表流水号等于实还表流流水号, tuple4s_2.size(),
                        tuple4s_2.stream().map(Tuple4::getFirst).collect(Collectors.toList()),
                        "还款流水金额:" + tuple4s_2.stream().map(Tuple4::getThird).reduce(BigDecimal.ZERO, BigDecimal::add) + "元," +
                                "实还金额:" + tuple4s_2.stream().map(Tuple4::getFourth).reduce(BigDecimal.ZERO, BigDecimal::add) + "元")
        );

        List<Tuple3<String, Integer, Integer>> tuple3s_3 = repayDao.checkTotalTerm(projectNo);
        //tuple3s_3.clear();
        list.add(
                DataCheckResult.dataCheckResult(DataCheckType.校验用户还款主信息期次等于还款计划总期次, tuple3s_3.size(),
                        tuple3s_3.stream().map(Tuple3::getFirst).collect(Collectors.toList()),
                        "还款计划总期次:" + tuple3s_3.stream().map(Tuple3::getThird).reduce(0, Integer::sum) + "," +
                                "还款主信息总期次:" + tuple3s_3.stream().mapToInt(Tuple3::getSecond).sum())
        );

        List<Tuple3<String, TermStatusEnum, AssetStatusEnum>> tuple3s_4 = repayDao.checkNoamal(projectNo);
        List<Tuple3<String, Integer, Integer>> tuple3s_5 = repayDao.checkOverdue(projectNo);
        List<Tuple3<String, Integer, Integer>> tuple3s_6 = repayDao.checkSettled(projectNo);

        List<String> list4 = tuple3s_4.stream().map(Tuple3::getFirst).collect(Collectors.toList());
        List<String> list5 = tuple3s_5.stream().map(Tuple3::getFirst).collect(Collectors.toList());
        List<String> list6 = tuple3s_6.stream().map(Tuple3::getFirst).collect(Collectors.toList());
        list4.addAll(list5);
        list4.addAll(list6);
        //list4.clear();
        list.add(
                DataCheckResult.dataCheckResult(DataCheckType.校验还款主信息表的借据状态和还款计划表的期次状态一致性, list4.size(),
                        list4, "")
        );

        List<Tuple3<String, Integer, Integer>> tuple3s_7 = repayDao.checkOverdueSkip(projectNo);
        List<Tuple4<String, Integer, Integer, Integer>> tuple4s_3 = repayDao.checkUndueSkip(projectNo);
        List<String> list7 = tuple3s_7.stream().map(Tuple3::getFirst).collect(Collectors.toList());
        List<String> list8 = tuple4s_3.stream().map(Tuple4::getFirst).collect(Collectors.toList());
        list7.addAll(list8);
        //list7.clear();
        list.add(
                DataCheckResult.dataCheckResult(DataCheckType.校验还款计划是否跳期, list7.size(),
                        list7, "")
        );

        return Result.result(true, list);
    }

    @Override
    public String getStr(String msg) {
        return new RepayDao().getStr(msg);
        //return repayDao.getStr(msg);
        // return msg;
    }

}
