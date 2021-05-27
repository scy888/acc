package com.weshare.adapter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.weshare.adapter.entity.IncomeApply;
import com.weshare.service.api.client.AdapterClient;
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

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
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
    public void LoanDetailTest() {


    }
}