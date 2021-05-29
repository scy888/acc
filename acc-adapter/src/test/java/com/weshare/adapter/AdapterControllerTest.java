package com.weshare.adapter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.weshare.adapter.dao.AdapterDao;
import com.weshare.adapter.entity.IncomeApply;
import com.weshare.adapter.service.AdapterService;
import com.weshare.service.api.client.AdapterClient;
import com.weshare.service.api.entity.RepaymentPlanReq;
import com.weshare.service.api.entity.UserBaseReq;
import common.JsonUtil;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.adapter.controller
 * @date: 2021-05-16 20:31:07
 * @describe:
 */
@SpringBootTest
@RunWith(SpringRunner.class)
class AdapterControllerTest {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private AdapterClient adapterClient;
    @Autowired
    private AdapterService adapterService;
    @Autowired
    private AdapterDao adapterDao;

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
        adapterService.saveAllRepayPlan(repaymentPlanReqs);
    }
}