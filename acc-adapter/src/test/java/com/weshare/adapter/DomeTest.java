package com.weshare.adapter;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.CaseFormat;
import com.weshare.adapter.entity.IncomeApply;
import com.weshare.adapter.entity.InterfaceLog;
import com.weshare.service.api.entity.UserBaseReq;
import com.weshare.service.api.enums.ProjectEnum;
import common.JsonUtil;
import common.SnowFlake;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;

import java.io.File;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.adapter
 * @date: 2021-05-16 22:30:16
 * @describe:
 */

public class DomeTest {
    @Test
    public void test() {
        UserBaseReq.LinkManReq linkMan = new UserBaseReq.LinkManReq()
                .setUserId("aa")
                .setUserName("scy");

        System.out.println(JsonUtil.toJson(linkMan, true));
        System.out.println(linkMan);
        System.out.println("=========================================");
        String s = "{\"user_id\":\"bb\",\"user_name\":\"wf\",\"age\":20}";
        UserBaseReq.LinkManReq man = JsonUtil.fromJson(s, UserBaseReq.LinkManReq.class);
        System.out.println(JsonUtil.toJson(man, true));
        System.out.println(man);
    }

    @Test
    public void test00() {

        IncomeApply incomeApply = new IncomeApply();
        incomeApply.setId("123")
                .setIdCardType(UserBaseReq.IdCardType.S)
                .setComeList(List.of(new UserBaseReq.Come("scy", "hb")));

        System.out.println(JsonUtil.toJson(incomeApply));

    }

    @Test
    public void test01() throws Exception {
        IncomeApply incomeApplyOne = new IncomeApply().setId(SnowFlake.getInstance().nextId() + "")
                .setUserId("348691356")
                .setUserName("张三")
                .setIdCardType(UserBaseReq.IdCardType.S)
                .setIdCardNum("422202199506063496")
                .setIphone("13297053058")
                .setCarNum("粤*B6685k")
                .setSex(UserBaseReq.Sex.M)
                .setProjectNo(ProjectEnum.YXMS.getProjectNo())
                .setDueBillNo("YX-101")
                .setBatchDate(LocalDate.parse("2021-05-12"))
                .setComeList(List.of(
                        new UserBaseReq.Come("湖北省", "湖北省应城市城北办事处魏河村17"),
                        new UserBaseReq.Come("湖北省", "湖北省应城市城北办事处柳林村26")))
                .setLinkMan(JsonUtil.toJson(List.of(
                        new UserBaseReq.LinkManReq("348691356", "YX-101", "张芳", "女", "13555763086", UserBaseReq.IdCardType.S, "422202199206033485", "教师", "湖北省应城市城北办事处魏河村17", UserBaseReq.RelationalType.PO),
                        new UserBaseReq.LinkManReq("348691356", "YX-101", "张冲", "男", "13455664456", UserBaseReq.IdCardType.H, "422202199310253455", "厨师", "湖北省应城市城北办事处柳林村26", UserBaseReq.RelationalType.ZN))))
                .setBackCard(JsonUtil.toJson(List.of(
                        new UserBaseReq.BackCardReq("348691356", "YX-101", "张三", UserBaseReq.BackName.中国建行设银行, UserBaseReq.BackName.中国建行设银行.getCode(), UserBaseReq.BackName.中国建行设银行.getNum()),
                        new UserBaseReq.BackCardReq("348691356", "YX-101", "张三", UserBaseReq.BackName.中国建招商银行, UserBaseReq.BackName.中国建招商银行.getCode(), UserBaseReq.BackName.中国建招商银行.getNum()))));

        IncomeApply incomeApplyTwo = new IncomeApply().setId(SnowFlake.getInstance().nextId() + "")
                .setUserId("316613046")
                .setUserName("李四")
                .setIdCardType(UserBaseReq.IdCardType.H)
                .setIdCardNum("422202199608083466")
                .setIphone("13597054456")
                .setCarNum("粤*B3596f")
                .setSex(UserBaseReq.Sex.W)
                .setProjectNo(ProjectEnum.YXMS.getProjectNo())
                .setDueBillNo("YX-102")
                .setBatchDate(LocalDate.parse("2021-05-12"))
                .setComeList(List.of(
                        new UserBaseReq.Come("湖北省", "湖北省应城市城北办事处魏河村27"),
                        new UserBaseReq.Come("湖北省", "湖北省应城市城北办事处柳林村36")))
                .setLinkMan(JsonUtil.toJson(List.of(
                        new UserBaseReq.LinkManReq("316613046", "YX-102", "李悦", "女", "13555893086", UserBaseReq.IdCardType.S, "422202199406033485", "护士", "湖北省应城市城北办事处魏河村27", UserBaseReq.RelationalType.PY),
                        new UserBaseReq.LinkManReq("316613046", "YX-102", "李阳", "男", "13402664456", UserBaseReq.IdCardType.H, "422202199510253455", "医生", "湖北省应城市城北办事处柳林村36", UserBaseReq.RelationalType.TS))))
                .setBackCard(JsonUtil.toJson(List.of(
                        new UserBaseReq.BackCardReq("316613046", "YX-102", "李四", UserBaseReq.BackName.中国建农业银行, UserBaseReq.BackName.中国建农业银行.getCode(), UserBaseReq.BackName.中国建农业银行.getNum()),
                        new UserBaseReq.BackCardReq("316613046", "YX-102", "李四", UserBaseReq.BackName.中国建工商银行, UserBaseReq.BackName.中国建工商银行.getCode(), UserBaseReq.BackName.中国建工商银行.getNum()))));

        List<IncomeApply> list = List.of(incomeApplyOne, incomeApplyTwo);
        System.out.println(JsonUtil.toJson(list, true));

        Path path = Paths.get("/incomeApply");
        if (Files.notExists(path)) {
            Files.createDirectories(path);
        }

        Files.writeString(Paths.get(String.valueOf(path), "incomeApply.json"), String.join(System.lineSeparator(), JsonUtil.toJson(list, true)), StandardCharsets.UTF_8, StandardOpenOption.CREATE);
        Files.write(Paths.get(String.valueOf(path), "incomeApply_.json"), Arrays.stream(JsonUtil.toJson(list, true).split(System.lineSeparator())).collect(Collectors.toList()), StandardCharsets.UTF_8, StandardOpenOption.CREATE);

        List<String> collect = list.stream().map(e -> JsonUtil.toJson(e, true)).collect(Collectors.toList());

        Files.writeString(Paths.get(String.valueOf(path), "incomeApply__.json"), collect.stream().collect(Collectors.joining(",", "[", "]")), StandardCharsets.UTF_8, StandardOpenOption.CREATE);
        Files.write(Paths.get(String.valueOf(path), "incomeApply___.json"), Arrays.stream(collect.stream().collect(Collectors.joining(",", "[", "]")).split(System.lineSeparator())).collect(Collectors.toList()), StandardCharsets.UTF_8, StandardOpenOption.CREATE);
    }

    @Test
    public void test02() {
        String s = "{\"user_id\":\"55\",\"due_bill_no\":\"111\"}";
        UserBaseReq.LinkManReq linkManReq = JsonUtil.fromJson(s, UserBaseReq.LinkManReq.class);
        System.out.println(linkManReq);

        UserBaseReq.ManReq manReq = new UserBaseReq.ManReq();
        BeanUtils.copyProperties(linkManReq, manReq);
        System.out.println(manReq);
    }

    @Test
    public void test03() {
        List<String> list1 = List.of("1-壹", "2-贰", "3-叁", "4-肆", "5-伍");
        list1 = new ArrayList<>(list1);

        List<String> list2 = List.of("1", "2", "3", "4");
        list2 = new ArrayList<>(list2);

//        for (String s2 : list2) {
//            String s = list1.stream().filter(s1 -> s1.split("-")[0].equals(s2)).findFirst().orElse(null);
//            list1.remove(s);
//        }

        for (String s2 : list2) {
            list1.removeIf(e -> e.split("-")[0].equals(s2));
        }

        System.out.println(list1);
    }

    @Test
    public void test04() {
        List<String> list = List.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
        list = new ArrayList<>(list);
        List<String> list1 = list.subList(0, 3);
        List<String> list2 = list.subList(3, 6);
        List<String> list3 = list.subList(6, list.size());

        System.out.println(list1);
        System.out.println(list2);
        System.out.println(list3);

        System.out.println("===================================================");
        list.clear();
        for (int i = 1; i <= 1001; i++) {
            list.add(String.valueOf(i));
        }
        List<String> list4 = list.subList(0, 250);
        List<String> list5 = list.subList(250, 500);
        List<String> list6 = list.subList(500, 750);
        List<String> list7 = list.subList(750, list.size());
        System.out.println(list4);
        System.out.println(list5);
        System.out.println(list6);
        System.out.println(list7);
    }

    @Test
    public void test05() {
        System.out.println((long) Math.ceil(13 / 3));
        System.out.println((int) Math.ceil(13 * 1.0 / 3));
        System.out.println((int) Math.ceil(13 / (3 * 1.0)));
        System.out.println((int) Math.ceil(13 / 3 * 1.0));
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class Teacher {
        @JsonProperty(value = "teach_name", index = 1)
        private String teachName;
        @JsonProperty(value = "sex", index = 2)
        private Sex sex;
        @JsonProperty(value = "student_list", index = 4)
        private String students;
        @JsonProperty(value = "batch_date", index = 5)
        @JsonFormat(pattern = "yyyyMMdd")
        private LocalDate batchDate;
        @JsonProperty(value = "create_date", index = 6)
        @JsonFormat(pattern = "yyyyMMddHHmmss")
        private LocalDateTime createDate;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class Student {
        @JsonProperty(value = "student_name", index = 1)
        private String studentName;
        @JsonProperty(value = "batch_date", index = 2)
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd")
        private LocalDate batchDate;
    }

    enum Sex {
        MAN,
        WOMAN;
    }

    @Test
    public void testJson() {
        Teacher teacher = new Teacher("张老师", Sex.MAN, JsonUtil.toJson(new Student("小明", LocalDate.now())), LocalDate.now(), LocalDateTime.now());
        String jsonStr = JsonUtil.toJson(teacher, true);
        System.out.println(jsonStr);
        System.out.println("================================================");
        teacher = JsonUtil.fromJson(jsonStr, Teacher.class);
        System.out.println(JsonUtil.toJson(teacher, true));
        System.out.println("================================================");
        System.out.println(teacher);
        System.out.println("================================================");
        //String students = teacher.getStudents();
        String studentJson = JsonUtil.toJsonNode(jsonStr, "student_list");
        Student student = JsonUtil.fromJson(studentJson, Student.class);
        System.out.println(JsonUtil.toJson(student, true));
        System.out.println("================================================");
        System.out.println(student);
        System.out.println("==================================");
        Teacher teacher1 = new Teacher("张老师", Sex.MAN, JsonUtil.toJson(new Student("小明", LocalDate.now())), LocalDate.now(), LocalDateTime.now());
        String json = JsonUtil.toJson(teacher1);
        //json = JsonUtil.revertToStandardJsonString(json, true);
        System.out.println(json);
        System.out.println("===============================");
        String student_list = JsonUtil.toJsonNode(json, "student_list");
        System.out.println(student_list);
        System.out.println("======================================================");
        Student studentList = JsonUtil.fromJson(student_list, Student.class);
        System.out.println(studentList);
    }

    @Test
    public void testInterface() throws Exception {
        List<InterfaceLog> interfaceLogs = List.of(
                new InterfaceLog(UUID.randomUUID().toString(), InterfaceLog.ServiceEnum.LOAN_DETAIL.name(), createLoanMsg(), LocalDate.now(), LocalDateTime.now(), LocalDateTime.now()),
                new InterfaceLog(UUID.randomUUID().toString(), InterfaceLog.ServiceEnum.REPAY_PLAN.name(), createPlanMsg(), LocalDate.now(), LocalDateTime.now(), LocalDateTime.now()),
                new InterfaceLog(UUID.randomUUID().toString(), InterfaceLog.ServiceEnum.REFUND_TICKET.name(), createRefundMsg(), LocalDate.now(), LocalDateTime.now(), LocalDateTime.now())

        );
        Path path = Paths.get("/interface");
        if (Files.notExists(path)) {
            Files.createDirectories(path);
        }
        for (File file : Objects.requireNonNull(new File(path.toUri()).listFiles())) {
            if (file.exists()) {
                file.delete();
            }
        }
        Files.writeString(Paths.get(String.valueOf(path), "interface.json"), JsonUtil.toJson(interfaceLogs, true), StandardOpenOption.CREATE);

        for (InterfaceLog reqLog : interfaceLogs) {
            String originalReqMsg = reqLog.getOriginalReqMsg();
            // System.out.println(originalReqMsg);
            String content;
            switch (reqLog.getServiceId()) {
                case "LOAN_DETAIL":
                    content = JsonUtil.toJsonNode(originalReqMsg, "content");
                    if (content != null) {
                        List<InterfaceLog.OriginalReqMsg.LoanDetail> loanDetails = JsonUtil.fromJson(content, new TypeReference<List<InterfaceLog.OriginalReqMsg.LoanDetail>>() {
                        });
                        System.out.println(JsonUtil.toJson(loanDetails, true));
                    }
                    System.out.println("==================================================");
                    break;
                case "REPAY_PLAN":
                    content = JsonUtil.toJsonNode(originalReqMsg, "content");
                    if (content != null) {
                        List<InterfaceLog.OriginalReqMsg.RepayPlan> repayPlans = JsonUtil.fromJson(content, new TypeReference<List<InterfaceLog.OriginalReqMsg.RepayPlan>>() {
                        });
                        System.out.println(JsonUtil.toJson(repayPlans, true));
                    }
                    break;
                case "REFUND_TICKET":
                    content = JsonUtil.toJsonNode(originalReqMsg, "content");
                    if (content != null) {
                        List<InterfaceLog.OriginalReqMsg.RefundTicket> refundTickets = JsonUtil.fromJson(content, new TypeReference<List<InterfaceLog.OriginalReqMsg.RefundTicket>>() {
                        });
                        System.out.println(JsonUtil.toJson(refundTickets, true));
                    }
                    break;
            }
        }
    }

    private String createLoanMsg() {
        InterfaceLog.OriginalReqMsg originalReqMsg = new InterfaceLog.OriginalReqMsg();
        originalReqMsg.setService(InterfaceLog.ServiceEnum.LOAN_DETAIL.name());
        originalReqMsg.setProjectNo("projectNo");
        originalReqMsg.setProductName("productName");
        originalReqMsg.setCreateDate(LocalDateTime.now());
        originalReqMsg.setBatchDate(LocalDate.now());
        originalReqMsg.setContent(
                JsonUtil.toJson(
                        List.of(
                                new InterfaceLog.OriginalReqMsg.LoanDetail("SCY-101", LocalDate.now(), new BigDecimal("1200.00"), SnowFlake.getInstance().nextId() + "", 6, "123", "01"),
                                new InterfaceLog.OriginalReqMsg.LoanDetail("SCY-102", LocalDate.now(), new BigDecimal("1200.00"), SnowFlake.getInstance().nextId() + "", 6, "456", "01")
                        )
                )
        );
        return JsonUtil.toJson(originalReqMsg);
    }

    private String createPlanMsg() {
        InterfaceLog.OriginalReqMsg originalReqMsg = new InterfaceLog.OriginalReqMsg();
        originalReqMsg.setService(InterfaceLog.ServiceEnum.REPAY_PLAN.name());
        originalReqMsg.setProjectNo("projectNo");
        originalReqMsg.setProductName("productName");
        originalReqMsg.setCreateDate(LocalDateTime.now());
        originalReqMsg.setBatchDate(LocalDate.now());
        originalReqMsg.setContent(
                JsonUtil.toJson(
                        List.of(
                                new InterfaceLog.OriginalReqMsg.RepayPlan("SCY-101", 6, List.of(
                                        new InterfaceLog.OriginalReqMsg.RepayPlan.DueBillNoList(1, LocalDate.parse("2020-06-15"), new BigDecimal("300.00"), new BigDecimal("170.00"), new BigDecimal("130.00")),
                                        new InterfaceLog.OriginalReqMsg.RepayPlan.DueBillNoList(2, LocalDate.parse("2020-07-15"), new BigDecimal("300.00"), new BigDecimal("180.00"), new BigDecimal("120.00")),
                                        new InterfaceLog.OriginalReqMsg.RepayPlan.DueBillNoList(3, LocalDate.parse("2020-08-15"), new BigDecimal("300.00"), new BigDecimal("190.00"), new BigDecimal("110.00")),
                                        new InterfaceLog.OriginalReqMsg.RepayPlan.DueBillNoList(4, LocalDate.parse("2020-09-15"), new BigDecimal("300.00"), new BigDecimal("200.00"), new BigDecimal("100.00")),
                                        new InterfaceLog.OriginalReqMsg.RepayPlan.DueBillNoList(5, LocalDate.parse("2020-10-15"), new BigDecimal("300.00"), new BigDecimal("210.00"), new BigDecimal("90.00")),
                                        new InterfaceLog.OriginalReqMsg.RepayPlan.DueBillNoList(6, LocalDate.parse("2020-11-15"), new BigDecimal("300.00"), new BigDecimal("250.00"), new BigDecimal("50.00"))
                                )),
                                new InterfaceLog.OriginalReqMsg.RepayPlan("SCY-102", 6, List.of(
                                        new InterfaceLog.OriginalReqMsg.RepayPlan.DueBillNoList(1, LocalDate.parse("2020-06-15"), new BigDecimal("300.00"), new BigDecimal("170.00"), new BigDecimal("130.00")),
                                        new InterfaceLog.OriginalReqMsg.RepayPlan.DueBillNoList(2, LocalDate.parse("2020-07-15"), new BigDecimal("300.00"), new BigDecimal("180.00"), new BigDecimal("120.00")),
                                        new InterfaceLog.OriginalReqMsg.RepayPlan.DueBillNoList(3, LocalDate.parse("2020-08-15"), new BigDecimal("300.00"), new BigDecimal("190.00"), new BigDecimal("110.00")),
                                        new InterfaceLog.OriginalReqMsg.RepayPlan.DueBillNoList(4, LocalDate.parse("2020-09-15"), new BigDecimal("300.00"), new BigDecimal("200.00"), new BigDecimal("100.00")),
                                        new InterfaceLog.OriginalReqMsg.RepayPlan.DueBillNoList(5, LocalDate.parse("2020-10-15"), new BigDecimal("300.00"), new BigDecimal("210.00"), new BigDecimal("90.00")),
                                        new InterfaceLog.OriginalReqMsg.RepayPlan.DueBillNoList(6, LocalDate.parse("2020-11-15"), new BigDecimal("300.00"), new BigDecimal("250.00"), new BigDecimal("50.00"))
                                )))
                )
        );
        return JsonUtil.toJson(originalReqMsg);
    }

    private String createRefundMsg() {
        InterfaceLog.OriginalReqMsg originalReqMsg = new InterfaceLog.OriginalReqMsg();
        originalReqMsg.setService(InterfaceLog.ServiceEnum.REFUND_TICKET.name());
        originalReqMsg.setProjectNo("projectNo");
        originalReqMsg.setProductName("productName");
        originalReqMsg.setCreateDate(LocalDateTime.now());
        originalReqMsg.setBatchDate(LocalDate.now());
        originalReqMsg.setContent(null);
        return JsonUtil.toJson(originalReqMsg);
    }

    @Test
    public void test002() {
        System.out.println(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, "LOAN_DETAIL"));
        System.out.println(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, "LOAN_DETAIL"));
        System.out.println(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, "repay_plan"));
        System.out.println(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, "repay_plan"));
        System.out.println(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, "REDUCE"));
        System.out.println(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, "REDUCE"));
        System.out.println("=======================");
        System.out.println(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, "loanDate"));
        System.out.println(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, "RepayPlan"));
        System.out.println(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, "reduce"));
    }
    @Test
    public void test003(){
        System.out.println(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, "TestData"));//test_data
        System.out.println(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, "testData"));//test_data
        System.out.println(CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, "LoanDetail"));//LOAN_DETAIL
        System.out.println(CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, "loanDetail"));//LOAN_DETAIL

    }
}
