package com.weshare.adapter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.weshare.adapter.entity.IncomeApply;
import com.weshare.service.api.enums.ProjectEnum;
import common.JsonUtil;
import common.SnowFlake;
import jdk.swing.interop.SwingInterOpUtils;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
    }
}