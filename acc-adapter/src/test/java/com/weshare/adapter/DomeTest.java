package com.weshare.adapter;

import com.weshare.adapter.entity.IncomeApply;
import com.weshare.service.api.entity.UserBaseReq;
import com.weshare.service.api.enums.ProjectEnum;
import common.JsonUtil;
import common.SnowFlake;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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
        String s="{\"user_id\":\"55\",\"due_bill_no\":\"111\"}";
        UserBaseReq.LinkManReq linkManReq = JsonUtil.fromJson(s, UserBaseReq.LinkManReq.class);
        System.out.println(linkManReq);

        UserBaseReq.ManReq manReq = new UserBaseReq.ManReq();
        BeanUtils.copyProperties(linkManReq,manReq);
        System.out.println(manReq);
    }
    @Test
    public void test03(){
        ArrayList<String> 数组越界了 = Optional.ofNullable(new ArrayList<String>()).orElseThrow(() -> new RuntimeException("数组越界了"));

        System.out.println(数组越界了);
    }
}
