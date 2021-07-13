package com.weshare.repay.provider;

import com.weshare.repay.dao.RepayDao;
import com.weshare.repay.entity.RepayPlan;
import com.weshare.repay.entity.RepaySummary;
import com.weshare.repay.entity.RepaySummaryBack;
import com.weshare.repay.repo.ReceiptDetailRepo;
import com.weshare.repay.repo.RepayPlanRepo;
import com.weshare.repay.repo.RepaySummaryRepo;
import com.weshare.repay.repo.RepayTransFlowRepo;
import com.weshare.service.api.client.RepayClient;
import com.weshare.service.api.entity.RepayPlanReq;
import com.weshare.service.api.enums.AssetStatusEnum;
import com.weshare.service.api.enums.FeeTypeEnum;
import com.weshare.service.api.enums.ProjectEnum;
import com.weshare.service.api.enums.TermStatusEnum;
import com.weshare.service.api.result.DataCheckResult;
import com.weshare.service.api.vo.Tuple2;
import com.weshare.service.api.vo.Tuple3;
import com.weshare.service.api.vo.Tuple4;
import common.JsonUtil;
import common.ProxyUtils;
import common.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.persistence.Index;
import javax.persistence.Table;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.repay.provider
 * @date: 2021-05-29 02:42:35
 * @describe:
 */
@SpringBootTest
@Slf4j
class RepayProviderTest {
    @Autowired
    private RepayClient repayClient;
    @Autowired
    private RepaySummaryRepo repaySummaryRepo;
    @Autowired
    private RepayPlanRepo repayPlanRepo;
    @Autowired
    private RepayTransFlowRepo repayTransFlowRepo;
    @Autowired
    private ReceiptDetailRepo receiptDetailRepo;
    @Autowired
    private RepayDao repayDao;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private HttpServletRequest request;

    @Test
    void count() {
        System.out.println(repaySummaryRepo.countByProjectNo("WS121212"));
        System.out.println((int) Math.ceil(17 * 1.0 / 4));

        repayClient.RefreshRepaySummaryCurrentTerm("WS121212", "2020-05-15");
    }

    @Test
    public void testDelete() {
        repayTransFlowRepo.deleteByBatchDateAndDueBillNoIn(LocalDate.parse("2021-05-30"), List.of("Yx-101"));
        receiptDetailRepo.deleteByBatchDateAndDueBillNoIn(LocalDate.parse("2021-05-30"), List.of("Yx-101"));
    }

    @Test
    public void testFourth() {
        List<Tuple4<BigDecimal, BigDecimal, LocalDate, Integer>> tuple4s = repayClient.getRepayPlanFourth("YX-102").getData();
        System.out.println("tuple4s:\n" + JsonUtil.toJson(tuple4s, true));
    }

    @Test
    public void testTwo() {
        List<Tuple2<BigDecimal, FeeTypeEnum>> tuple2s = repayClient.getReceiptDetailTwo("YX-102", 1).getData();
        System.out.println("tuple2s:\n" + JsonUtil.toJson(tuple2s, true));
    }

    @Test
    public void testSorted() {
        List<Tuple4<BigDecimal, BigDecimal, LocalDate, Integer>> tuple4s = repayClient.getRepayPlanFourth("YX-102").getData();
        List<LocalDate> dateList = tuple4s.stream().sorted(Comparator.comparing(Tuple4::getThird, Comparator.reverseOrder()))
                .map(Tuple4::getThird).sorted(Comparator.reverseOrder()).collect(Collectors.toList());
        System.out.println(dateList);

        LocalDate min = tuple4s.stream().max(Comparator.comparing(Tuple4::getThird, Comparator.reverseOrder())).map(Tuple4::getThird).orElse(null);
        System.out.println(min);
        Optional<Tuple4<BigDecimal, BigDecimal, LocalDate, Integer>> or = tuple4s.stream().max(Comparator.comparing(Tuple4::getThird)).or(() -> Optional.of(Tuple4.of(BigDecimal.ZERO, BigDecimal.ZERO, LocalDate.now(), 12)));
        LocalDate min_ = tuple4s.stream().map(Tuple4::getThird).max(Comparator.comparing(localDate -> localDate, Comparator.reverseOrder())).orElse(null);
        System.out.println(min_);
        System.out.println(tuple4s.stream().map(Tuple4::getThird).map(LocalDate::toString).collect(Collectors.toList()));

        List<String> list = List.of("2020-06-15", "2020-06-15", "2020-06-15");
        System.out.println(list.stream().map(LocalDate::parse).collect(Collectors.toList()));

        List<String> list1 = List.of("2020/06/15 12:12:25", "2020/06/15 12:12:25", "2020/06/15 12:12:25");
        System.out.println(list1.stream().map(e -> LocalDateTime.parse(e, DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"))).collect(Collectors.toList()));
    }

    @Test
    public void testOneRepayPlan() {
        RepayPlan repayPlan = repayDao.getRepayPlan("YX-102", 1);
        log.info("repayPlan:{}", JsonUtil.toJson(repayPlan, true));
        System.out.println(repayPlan.getTermStatus() == TermStatusEnum.REPAID);
    }

    @Test
    public void testOneMap() {
        Map<String, Object> map = repayDao.getRepayPlan(1, "YX-102");
        log.info("map:{}", JsonUtil.toJson(map, true));
        System.out.println(TermStatusEnum.valueOf(map.get("term_status").toString()) == TermStatusEnum.REPAID);
    }

    @Test
    public void testListRepayPlan() {
        List<RepayPlan> repayPlanList = repayDao.getRepayPlanList("YX-102", List.of(1, 2));
        log.info("repayPlanList:{}", JsonUtil.toJson(repayPlanList, true));

    }

    @Test
    public void testListMap() {
        List<Map<String, Object>> mapList = repayDao.getRepayPlanList(List.of(1, 2), "YX-102");
        log.info("mapList:{}", JsonUtil.toJson(mapList, true));

    }

    @Test
    public void testTuple3() {
        Tuple3<LocalDate, BigDecimal, TermStatusEnum> tuple3 = repayDao.getTuple3("YX-102", 1);
        log.info("tuple3:{}", JsonUtil.toJson(tuple3, true));

    }

    @Test
    public void testTuple3List() {
        List<Tuple3<LocalDate, BigDecimal, TermStatusEnum>> tuple3s = repayDao.getTuple3("YX-102", List.of(1, 2));
        log.info("tuple3:{}", JsonUtil.toJson(tuple3s, true));

    }

    @Test
    public void testRepayList() {
        List<RepayDao.Repay> list = repayDao.getRepayList("YX-102", List.of(1, 2));
        log.info("list:{}", JsonUtil.toJson(list, true));

    }

    @Test
    public void testMapList() {
        List<Map<String, Object>> mapList = repayDao.getMapList("YX-102", List.of(1, 2));
        log.info("mapList:{}", JsonUtil.toJson(mapList, true));
    }

    @Test
    public void testBoth() {
        List<Tuple3<String, BigDecimal, BigDecimal>> tuple3s = repayDao.getBoth("WS121212");
        log.info("tuple3s:{}", JsonUtil.toJson(tuple3s, true));
    }

    @Test
    public void testLike() {
        List<RepayPlan> plans = repayPlanRepo.findByDueBillNoLikeAndTerm("YX-%", 2);
        log.info("plans:{}", JsonUtil.toJson(plans, true));

    }

    @Test
    public void testSpec() {
        List<RepayPlanReq> data = repayClient.getRepayPlanPage(new RepayClient.PageReq()
                .setPageNum(1)
                .setPageSize(2)
                .setDueBillNo("YX-102")
                .setTerms(List.of(2, 3, 4))
                .setTermStatus(TermStatusEnum.REPAID)
                .setStartDate(LocalDate.parse("2020-01-01"))
                .setEndDate(LocalDate.parse("2020-12-31"))).getData();
        log.info("data:{}", JsonUtil.toJson(data, true));
    }

    @Test
    public void test() {
        Class<RepayPlan> clazz = RepayPlan.class;
        System.out.println("是否是注解：" + clazz.isAnnotation());
        System.out.println(clazz.isAnnotationPresent(Table.class));
        Index[] indexes = clazz.getAnnotation(Table.class).indexes();
        Arrays.asList(indexes).stream().forEach(e -> System.out.println(e));
        System.out.println(clazz.getAnnotation(org.hibernate.annotations.Table.class).appliesTo());
        System.out.println(clazz.getAnnotation(org.hibernate.annotations.Table.class).comment());
        System.out.println(UUID.randomUUID().toString().replaceAll("-", ""));
    }

    @Test
    public void tes002t() {
        List<String> list = new ArrayList<>();
        List<List<Integer>> collect = list.stream().map(e -> {
            List<Integer> list2 = new ArrayList<>();
            list2.add(Integer.parseInt(e));
            return list2;
        }).collect(Collectors.toList());
        System.out.println(collect);
    }

    @Test
    public void testInsertBatch() {
        long start = System.currentTimeMillis();
        jdbcTemplate.batchUpdate("truncate table acc_repay.repay_summary_back");
        RepaySummary repaySummary = repaySummaryRepo.findByDueBillNo("YX-101");
        LocalDate batchDate = LocalDate.parse("2020-12-16");
        for (int i = 1; i <= 1000; i++) {
            List<RepaySummaryBack> list = new ArrayList<>();
            repaySummary.setBatchDate(batchDate.plusDays(i));
            if (i % 5 == 0) {
                repaySummary.setProjectNo(ProjectEnum.YXMS.getProjectNo());
            } else if (i % 5 == 1) {
                repaySummary.setProjectNo(ProjectEnum.BDGM.getProjectNo());
            } else if (i % 5 == 2) {
                repaySummary.setProjectNo(ProjectEnum.GQZL.getProjectNo());
            } else if (i % 5 == 3) {
                repaySummary.setProjectNo(ProjectEnum.HTGY.getProjectNo());
            } else {
                repaySummary.setProjectNo(ProjectEnum.LXYX.getProjectNo());
            }
            for (int j = 1; j <= 1000; j++) {
                RepaySummaryBack repaySummaryBack = new RepaySummaryBack();
                BeanUtils.copyProperties(repaySummary, repaySummaryBack);
                repaySummaryBack.setId((i - 1) * 1000 + j + "");
                repaySummaryBack.setDueBillNo("SCY-" + (i - 1) * 1000 + j);
                repaySummaryBack.setLastModifiedBy("scy");
                list.add(repaySummaryBack);
            }
            String sql = StringUtils.getInsertSql("acc_repay.repay_summary_back", RepaySummaryBack.class, "product_no", "repay_day", "last_modified_by");
            List<Object[]> objects = list.stream().map(e -> StringUtils.getFieldValue(e, "productNo", "repayDay", "lastModifiedBy")).collect(Collectors.toList());
            jdbcTemplate.batchUpdate(sql, objects);
        }
        long end = System.currentTimeMillis();
        System.out.println("耗时:" + (end - start) / 1000.0 + "秒");
    }

    @Test
    public void testInsertBatch_() {
        long start = System.currentTimeMillis();
        jdbcTemplate.batchUpdate("truncate table acc_repay.repay_summary_back");
        RepaySummary repaySummary = repaySummaryRepo.findByDueBillNo("YX-101");
        List<RepaySummaryBack> list = new ArrayList<>();
        for (int i = 1; i <= 100012; i++) {
            RepaySummaryBack repaySummaryBack = new RepaySummaryBack();
            BeanUtils.copyProperties(repaySummary, repaySummaryBack);
            if (i % 5 == 0) {
                repaySummaryBack.setProjectNo(ProjectEnum.YXMS.getProjectNo());
            } else if (i % 5 == 1) {
                repaySummaryBack.setProjectNo(ProjectEnum.BDGM.getProjectNo());
            } else if (i % 5 == 2) {
                repaySummaryBack.setProjectNo(ProjectEnum.GQZL.getProjectNo());
            } else if (i % 5 == 3) {
                repaySummaryBack.setProjectNo(ProjectEnum.HTGY.getProjectNo());
            } else {
                repaySummaryBack.setProjectNo(ProjectEnum.LXYX.getProjectNo());
            }
            list.add(repaySummaryBack);
        }
        //每1000条提交一次
        List<RepaySummaryBack> tempList = new ArrayList<>();
        int num = 0;
        LocalDate batchDate = LocalDate.parse("2020-12-16");
        for (int i = 0; i < list.size(); i++) {
            RepaySummaryBack repaySummaryBack = list.get(i);
            repaySummaryBack.setId(String.valueOf(i + 1));
            repaySummaryBack.setDueBillNo("SCY-" + i + 1);
            tempList.add(repaySummaryBack);
            // num++; (num%1000==0||num=list.size())
            if (tempList.size() % 1000 == 0 || (tempList.size() == list.size() % 1000 && num == list.size() / 1000)) {
                num++;
                String sql = StringUtils.getInsertSql("acc_repay.repay_summary_back", RepaySummaryBack.class, "product_no", "repay_day", "last_modified_by");
                int finalI = i;
                List<Object[]> objects = tempList.stream().map(e -> {
                    return StringUtils.getFieldValue(e.setBatchDate(batchDate.plusDays(finalI % 1000 == 0 ? finalI / 1000 : finalI / 1000 + 1)), "productNo", "repayDay", "lastModifiedBy");
                }).collect(Collectors.toList());
                jdbcTemplate.batchUpdate(sql, objects);
                tempList.clear();
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("耗时:" + (end - start) / 1000.0 + "秒");
    }

    @Test
    public void testCheckResult() {
        for (Tuple3<String, BigDecimal, BigDecimal> tuple3 : repayDao.checkLoanAmount("WS121212")) {
            System.out.println("checkLoanAmount:" + tuple3);
        }
        System.out.println("===================================================================");
        for (Tuple3<String, BigDecimal, BigDecimal> tuple3 : repayDao.checkRemainPrin("WS121212")) {
            System.out.println("checkRemainPrin:" + tuple3);
        }
        System.out.println("===================================================================");
        for (Tuple4<String, BigDecimal, BigDecimal, BigDecimal> tuple4 : repayDao.checkActualAmount("WS121212")) {
            System.out.println("checkActualAmount:" + tuple4);
        }
        System.out.println("===================================================================");
        for (Tuple4<String, String, BigDecimal, BigDecimal> tuple4 : repayDao.checkFlowSn("WS121212")) {
            System.out.println("checkFlowSn:" + tuple4);
        }
        System.out.println("===================================================================");
        for (Tuple3<String, Integer, Integer> tuple3 : repayDao.checkTotalTerm("WS121212")) {
            System.out.println("checkTotalTerm:" + tuple3);
        }
        System.out.println("===================================================================");
        for (Tuple3<String, TermStatusEnum, AssetStatusEnum> tuple3 : repayDao.checkNoamal("WS121212")) {
            System.out.println("checkNoamal:" + tuple3);
        }
        System.out.println("===================================================================");
        for (Tuple3<String, Integer, Integer> tuple3 : repayDao.checkOverdue("WS121212")) {
            System.out.println("checkOverdue:" + tuple3);
        }
        System.out.println("===================================================================");
        for (Tuple3<String, Integer, Integer> tuple3 : repayDao.checkSettled("WS121212")) {
            System.out.println("checkSettled:" + tuple3);
        }
        System.out.println("===================================================================");
        for (Tuple3<String, Integer, Integer> tuple3 : repayDao.checkOverdueSkip("WS121212")) {
            System.out.println("checkOverdueSkip:" + tuple3);
        }
        System.out.println("===================================================================");
        for (Tuple4<String, Integer, Integer, Integer> tuple4 : repayDao.checkUndueSkip("WS121212")) {
            System.out.println("checkUndueSkip:" + tuple4);

        }
    }

    @Test
    public void checkResultTest() {
        for (DataCheckResult result : repayClient.checkDataResult("WS121212").getData()) {
            System.out.println(JsonUtil.toJson(result, true));
        }

    }

    @Autowired
    private ApplicationContext context;

    @Test
    public void testReflect() {
        RepayClient proxyInstance = (RepayClient) ProxyUtils.getProxyInstance( RepayProvider.repayProvider);
        //String str = proxyInstance.getStr("WS121212");
        List<DataCheckResult> list = proxyInstance.checkDataResult("WS121212").getData();
        System.out.println(list);
        System.out.println(request.getRequestURI());
        System.out.println(request.getRemoteAddr());
        System.out.println(request.getLocalAddr());
    }
}