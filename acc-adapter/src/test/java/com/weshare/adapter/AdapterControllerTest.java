package com.weshare.adapter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.weshare.adapter.dao.AdapterDao;
import com.weshare.adapter.entity.IncomeApply;
import com.weshare.adapter.feignCilent.RepayFeignClient;
import com.weshare.adapter.repo.RebackDetailRepo;
import com.weshare.adapter.service.AdapterService;
import com.weshare.service.api.client.AdapterClient;
import com.weshare.service.api.entity.*;
import com.weshare.service.api.enums.TransFlowTypeEnum;
import com.weshare.service.api.vo.Tuple3;
import com.weshare.service.api.vo.Tuple4;
import common.DateUtils;
import common.JsonUtil;
import common.ReflectUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.adapter.controller
 * @date: 2021-05-16 20:31:07
 * @describe:
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
class AdapterControllerTest {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private AdapterClient adapterClient;
    @Autowired
    private RepayFeignClient repayFeignClient;
    @Autowired
    private AdapterService adapterService;
    @Autowired
    private AdapterDao adapterDao;
    @Autowired
    private RebackDetailRepo rebackDetailRepo;

    @Test
    public void mongodbTest() throws Exception {
        if (!mongoTemplate.collectionExists(IncomeApply.class)) {
            mongoTemplate.createCollection(IncomeApply.class);
        }

        String readString = Files.readString(Paths.get("/incomeApply", "incomeApply.json"), StandardCharsets.UTF_8);
        List<IncomeApply> incomeApplyList = JsonUtil.fromJson(readString, new TypeReference<List<IncomeApply>>() {
        });
        System.out.println(JsonUtil.toJson(incomeApplyList, true));
        System.out.println("================================================================================");

        List<String> readAllLines = Files.readAllLines(Paths.get("/incomeApply", "incomeApply.json"), StandardCharsets.UTF_8);
        incomeApplyList.clear();
        incomeApplyList = JsonUtil.fromJson(String.join(System.lineSeparator(), readAllLines), new TypeReference<List<IncomeApply>>() {
        });
        System.out.println(JsonUtil.toJson(incomeApplyList, true));

//        for (IncomeApply incomeApply : incomeApplyList) {
//            Optional.ofNullable(mongoTemplate.findById(incomeApply.getId(), IncomeApply.class)).orElseGet(() -> {
//                mongoTemplate.insert(incomeApply);
//                return null;
//            });
//        }
        for (IncomeApply incomeApply : incomeApplyList) {
            Optional.ofNullable(mongoTemplate.findOne(Query.query(Criteria.where("due_bill_no").is(incomeApply.getDueBillNo())), IncomeApply.class))
                    .orElseGet(() -> mongoTemplate.insert(incomeApply)
                    );
        }

        System.out.println("==========================================================");
        IncomeApply apply1 = mongoTemplate.findOne(Query.query(Criteria.where("due_bill_no").is("YX-101")), IncomeApply.class);
        IncomeApply apply2 = mongoTemplate.findOne(Query.query(Criteria.where("dueBillNo").is("YX-102")), IncomeApply.class);
        System.out.println("apply:" + apply1);
        System.out.println("apply:" + apply2);

        System.out.println("============================================================");
        incomeApplyList.clear();
        //incomeApplyList = mongoTemplate.find(Query.query(Criteria.where("due_bill_no").regex(Pattern.compile("^.*" + "YX-" + "*.$",Pattern.CASE_INSENSITIVE))), IncomeApply.class);
        incomeApplyList = mongoTemplate.find(Query.query(Criteria.where("due_bill_no").in("YX-101", "YX-102")), IncomeApply.class);
        System.out.println(JsonUtil.toJson(incomeApplyList, true));

        List<UserBaseReq> userBaseReqList = incomeApplyList.stream().map(e -> new UserBaseReq()
                .setId(e.getId())
                .setUserId(e.getUserId())
                .setUserName(e.getUserName())
                .setIdCardType(e.getIdCardType())
                .setIdCardNum(e.getIdCardNum())
                .setCarNum(e.getCarNum())
                .setIphone(e.getIphone())
                .setSex(e.getSex())
                .setProjectNo(e.getProjectNo())
                .setDueBillNo(e.getDueBillNo())
                .setBatchDate(e.getBatchDate())
                .setLinkManList(JsonUtil.fromJson(e.getLinkMan(), new TypeReference<List<UserBaseReq.LinkManReq>>() {
                }))
                .setBackCardList(JsonUtil.fromJson(e.getBackCard(), new TypeReference<List<UserBaseReq.BackCardReq>>() {
                }))).collect(Collectors.toList());

        System.out.println("aaaa" + JsonUtil.toJson(userBaseReqList, true));

        Files.writeString(Paths.get("/incomeApply", "userbase.json"), JsonUtil.toJson(userBaseReqList, true));
    }

    @Test
    public void RepaymentPlanTest() {

        LocalDate batchDate = LocalDate.parse("2020-05-15");
        List<RepaymentPlanReq> repaymentPlanReqs = List.of(
                new RepaymentPlanReq("YX-101", 1, batchDate.plusMonths(1), new BigDecimal(300), new BigDecimal(170), new BigDecimal(130), batchDate),
                new RepaymentPlanReq("YX-101", 2, batchDate.plusMonths(2), new BigDecimal(300), new BigDecimal(180), new BigDecimal(120), batchDate),
                new RepaymentPlanReq("YX-101", 3, batchDate.plusMonths(3), new BigDecimal(300), new BigDecimal(190), new BigDecimal(110), batchDate),
                new RepaymentPlanReq("YX-101", 4, batchDate.plusMonths(4), new BigDecimal(300), new BigDecimal(200), new BigDecimal(100), batchDate),
                new RepaymentPlanReq("YX-101", 5, batchDate.plusMonths(5), new BigDecimal(300), new BigDecimal(210), new BigDecimal(90), batchDate),
                new RepaymentPlanReq("YX-101", 6, batchDate.plusMonths(6), new BigDecimal(300), new BigDecimal(250), new BigDecimal(50), batchDate),

                new RepaymentPlanReq("YX-102", 1, batchDate.plusMonths(1), new BigDecimal(300), new BigDecimal(170), new BigDecimal(130), batchDate),
                new RepaymentPlanReq("YX-102", 2, batchDate.plusMonths(2), new BigDecimal(300), new BigDecimal(180), new BigDecimal(120), batchDate),
                new RepaymentPlanReq("YX-102", 3, batchDate.plusMonths(3), new BigDecimal(300), new BigDecimal(190), new BigDecimal(110), batchDate),
                new RepaymentPlanReq("YX-102", 4, batchDate.plusMonths(4), new BigDecimal(300), new BigDecimal(200), new BigDecimal(100), batchDate),
                new RepaymentPlanReq("YX-102", 5, batchDate.plusMonths(5), new BigDecimal(300), new BigDecimal(210), new BigDecimal(90), batchDate),
                new RepaymentPlanReq("YX-102", 6, batchDate.plusMonths(6), new BigDecimal(300), new BigDecimal(250), new BigDecimal(50), batchDate)
        );

        adapterClient.saveAllRepaymentPlan(repaymentPlanReqs);
        adapterService.saveAllRepayPlanUpdateLoanContractAndRepaySummary(repaymentPlanReqs);
    }

    @Test
    public void saveAllRefundTicketTest() {
        LocalDate batchDate = LocalDate.parse("2020-05-30");
        List<RefundTicketReq> refundTicketReqs = List.of(
                new RefundTicketReq("YX-101", new BigDecimal(1200), LocalDate.parse("2020-05-15"), "02", "6217 0028 7001 5622 705", DateUtils.getLocalDateTime(batchDate), batchDate),
                new RefundTicketReq("YX-101", new BigDecimal(1200), LocalDate.parse("2020-05-15"), "01", "6217 0028 7001 5622 705", DateUtils.getLocalDateTime(batchDate), batchDate)
        );
        adapterClient.saveAllRefundTicket(refundTicketReqs);
        adapterService.saveRefundDownRepayTransFlowAndReceiptDetail(refundTicketReqs, batchDate.toString());
    }

    @Test
    public void saveAllRebackDetailAndRepaymentDetail0615Test() throws Exception {
        LocalDate batchDate = LocalDate.parse("2020-06-15");
        List<RebackDetailReq> rebackDetailReqs_01 = List.of(
                new RebackDetailReq("YX-102", 1, new BigDecimal(150), new BigDecimal(80), new BigDecimal(70), BigDecimal.ZERO, BigDecimal.ZERO, "02", TransFlowTypeEnum.正常还款, RebackDetailReq.FailReasonEnum.手机号错误, DateUtils.getLocalDateTime(batchDate), batchDate),
                new RebackDetailReq("YX-102", 1, new BigDecimal(150), new BigDecimal(80), new BigDecimal(70), BigDecimal.ZERO, BigDecimal.ZERO, "02", TransFlowTypeEnum.正常还款, RebackDetailReq.FailReasonEnum.身份证号错误, DateUtils.getLocalDateTime(batchDate), batchDate),
                new RebackDetailReq("YX-102", 1, new BigDecimal(150), new BigDecimal(80), new BigDecimal(70), BigDecimal.ZERO, BigDecimal.ZERO, "02", TransFlowTypeEnum.正常还款, RebackDetailReq.FailReasonEnum.银行卡号错误, DateUtils.getLocalDateTime(batchDate), batchDate),
                new RebackDetailReq("YX-102", 1, new BigDecimal(150), new BigDecimal(80), new BigDecimal(70), BigDecimal.ZERO, BigDecimal.ZERO, "01", TransFlowTypeEnum.正常还款, null, DateUtils.getLocalDateTime(batchDate), batchDate)
        );
        List<RebackDetailReq> rebackDetailReqs_02 = List.of(
                new RebackDetailReq("YX-102", 1, new BigDecimal(150), new BigDecimal(90), new BigDecimal(60), BigDecimal.ZERO, BigDecimal.ZERO, "02", TransFlowTypeEnum.正常还款, RebackDetailReq.FailReasonEnum.手机号错误, DateUtils.getLocalDateTime(batchDate), batchDate),
                new RebackDetailReq("YX-102", 1, new BigDecimal(150), new BigDecimal(90), new BigDecimal(60), BigDecimal.ZERO, BigDecimal.ZERO, "02", TransFlowTypeEnum.正常还款, RebackDetailReq.FailReasonEnum.身份证号错误, DateUtils.getLocalDateTime(batchDate), batchDate),
                new RebackDetailReq("YX-102", 1, new BigDecimal(150), new BigDecimal(90), new BigDecimal(60), BigDecimal.ZERO, BigDecimal.ZERO, "02", TransFlowTypeEnum.正常还款, RebackDetailReq.FailReasonEnum.银行卡号错误, DateUtils.getLocalDateTime(batchDate), batchDate),
                new RebackDetailReq("YX-102", 1, new BigDecimal(150), new BigDecimal(90), new BigDecimal(60), BigDecimal.ZERO, BigDecimal.ZERO, "01", TransFlowTypeEnum.正常还款, null, DateUtils.getLocalDateTime(batchDate), batchDate)
        );
        ArrayList<RebackDetailReq> rebackDetailReqs = new ArrayList<>();
        rebackDetailReqs.addAll(rebackDetailReqs_01);
        rebackDetailReqs.addAll(rebackDetailReqs_02);
        rebackDetailReqs = new ArrayList<>(rebackDetailReqs);
        //rebackDetailReqs.clear();

        List<RepaymentDetailReq> repaymentDetailReqs = List.of(
                new RepaymentDetailReq("YX-102", "01", DateUtils.getLocalDateTime(batchDate), 1, new BigDecimal(150), new BigDecimal(80), new BigDecimal(70), BigDecimal.ZERO, BigDecimal.ZERO, batchDate),
                new RepaymentDetailReq("YX-102", "01", DateUtils.getLocalDateTime(batchDate), 1, new BigDecimal(150), new BigDecimal(90), new BigDecimal(60), BigDecimal.ZERO, BigDecimal.ZERO, batchDate)
        );
        repaymentDetailReqs = new ArrayList<>(repaymentDetailReqs);
        //repaymentDetailReqs.clear();

        adapterClient.saveAllRebackDetal(rebackDetailReqs);//保存adapter库的reback_detail表（扣款明细表）
        adapterService.createRepayTransFlow(rebackDetailReqs, batchDate.toString());//保存repay库的repay_trans_flow表（还款流水表）

        adapterClient.saveAllRepaymentDetail(repaymentDetailReqs);//保存adapter库的repayment_detail表（还款明细表）
        adapterService.createAllReceiptDetail(repaymentDetailReqs, batchDate.toString());//保存repay的库receipt_detail表(实还记录)


        Path path = Paths.get("/reback");
        if (Files.notExists(path)) {
            Files.createDirectories(path);
        }
        for (File file : new File(path.toUri()).listFiles()) {
            file.delete();
        }
        String fieldNames1 = ReflectUtils.getFieldNames(RebackDetailReq.class, "batchDate");
        List<String> list1 = rebackDetailReqs_01.stream().map(e -> ReflectUtils.getFieldValues(e, "batchDate")).collect(Collectors.toList());
        list1.add(0, fieldNames1);
        Files.write(Paths.get(String.valueOf(path), "rebackDetailReqs_01.csv"), list1);

        String fieldNames2 = ReflectUtils.getFieldNames(RebackDetailReq.class, "batchDate");
        List<String> list2 = rebackDetailReqs_02.stream().map(e -> ReflectUtils.getFieldValues(e, "batchDate")).collect(Collectors.toList());
        list2.add(0, fieldNames2);
        Files.write(Paths.get(String.valueOf(path), "rebackDetailReqs_02.csv"), list2);

        List<String> list3 = Files.readAllLines(Paths.get(String.valueOf(path), "rebackDetailReqs_01.csv"));
        List<String> list4 = Files.readAllLines(Paths.get(String.valueOf(path), "rebackDetailReqs_02.csv"));
        Stream<String> stream1 = list3.stream().skip(1);
        Stream<String> stream2 = list4.stream().skip(1);
        List<String> listConcat = Stream.concat(stream1, stream2).collect(Collectors.toList());
        listConcat.add(0, ReflectUtils.getFieldNames(RebackDetailReq.class, "batchDate"));
        Files.write(Paths.get(String.valueOf(path), "rebackDetailReqs.csv"), listConcat);

    }

    @Test
    public void testDelete() {
        rebackDetailRepo.deleteByBatchDateAndDueBillNoIn(LocalDate.parse("2020-05-15"), List.of("123"));
        List<Tuple3<String, String, BigDecimal>> tuple3s = repayFeignClient.getFlowSn("YX-102", "2020-06-15").getData();
        System.out.println(tuple3s);
    }

    @Test
    public void testFourth() {
        List<Tuple4<BigDecimal, BigDecimal, LocalDate, Integer>> tuple4s = repayFeignClient.getRepayPlanFourth("YX-102").getData();
        Tuple4<BigDecimal, BigDecimal, LocalDate, Integer> tuple4 = tuple4s.stream().filter(e -> e.getFourth() == 6).findFirst().orElse(null);
        log.info("tuple4:{}",JsonUtil.toJson(tuple4,true));
    }
}