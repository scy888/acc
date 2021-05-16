package com.weshare.adapter;

import com.weshare.adapter.entity.IncomeApply;
import com.weshare.service.api.enums.ProjectEnum;
import common.JsonUtil;
import common.SnowFlake;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

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
        IncomeApply.LinkMan linkMan = new IncomeApply.LinkMan()
                .setUserId("aa")
                .setUserName("scy");

        System.out.println(JsonUtil.toJson(linkMan, true));
        System.out.println(linkMan);
        System.out.println("=========================================");
        String s = "{\"user_id\":\"bb\",\"user_name\":\"wf\",\"age\":20}";
        IncomeApply.LinkMan man = JsonUtil.fromJson(s, IncomeApply.LinkMan.class);
        System.out.println(JsonUtil.toJson(man, true));
        System.out.println(man);
    }

    @Test
    public void test00() {

        IncomeApply incomeApply = new IncomeApply();
        incomeApply.setId("123")
                .setIdCardType(IncomeApply.IdCardType.S)
                .setComeList(List.of(new IncomeApply.Come("scy", "hb")));

        System.out.println(JsonUtil.toJson(incomeApply));

    }

    @Test
    public void test01() {
        IncomeApply incomeApplyOne = new IncomeApply().setId(SnowFlake.getInstance().nextId() + "")
                .setUserId("348691356")
                .setUserName("张三")
                .setIdCardType(IncomeApply.IdCardType.S)
                .setIdCardNum("422202199506063496")
                .setIphone("13297053058")
                .setIdCardNum("粤*B6685k")
                .setSex(IncomeApply.Sex.M)
                .setProjectNo(ProjectEnum.YXMS.getProjectNo())
                .setDueBillNo("YX-101")
                .setBatchDate(LocalDate.parse("2021-05-12"))
                .setComeList(List.of(
                        new IncomeApply.Come("湖北省", "湖北省应城市城北办事处魏河村17"),
                        new IncomeApply.Come("湖北省", "湖北省应城市城北办事处柳林村26")))
                .setLinkMan(JsonUtil.toJson(List.of(
                        new IncomeApply.LinkMan("348691356", "YX-101", "张芳", "女", "13555763086", IncomeApply.IdCardType.S, "422202199206033485", "教师", "湖北省应城市城北办事处魏河村17", IncomeApply.RelationalType.PO),
                        new IncomeApply.LinkMan("348691356", "YX-101", "张冲", "男", "13455664456", IncomeApply.IdCardType.H, "422202199310253455", "厨师", "湖北省应城市城北办事处柳林村26", IncomeApply.RelationalType.ZN))))
                .setBackCard(JsonUtil.toJson(List.of(
                        new IncomeApply.BackCard("348691356", "YX-101", "张三", IncomeApply.BackName.中国建行设银行, IncomeApply.BackName.中国建行设银行.getCode(), IncomeApply.BackName.中国建行设银行.getNum()),
                        new IncomeApply.BackCard("348691356", "YX-101", "张三", IncomeApply.BackName.中国建招商银行, IncomeApply.BackName.中国建招商银行.getCode(), IncomeApply.BackName.中国建招商银行.getNum()))));

        IncomeApply incomeApplyTwo = new IncomeApply().setId(SnowFlake.getInstance().nextId() + "")
                .setUserId("316613046")
                .setUserName("李四")
                .setIdCardType(IncomeApply.IdCardType.H)
                .setIdCardNum("422202199608083466")
                .setIphone("13597054456")
                .setIdCardNum("粤*B3596f")
                .setSex(IncomeApply.Sex.W)
                .setProjectNo(ProjectEnum.YXMS.getProjectNo())
                .setDueBillNo("YX-102")
                .setBatchDate(LocalDate.parse("2021-05-12"))
                .setComeList(List.of(
                        new IncomeApply.Come("湖北省", "湖北省应城市城北办事处魏河村27"),
                        new IncomeApply.Come("湖北省", "湖北省应城市城北办事处柳林村36")))
                .setLinkMan(JsonUtil.toJson(List.of(
                        new IncomeApply.LinkMan("316613046", "YX-102", "李悦", "女", "13555893086", IncomeApply.IdCardType.S, "422202199406033485", "护士", "湖北省应城市城北办事处魏河村27", IncomeApply.RelationalType.PY),
                        new IncomeApply.LinkMan("316613046", "YX-102", "李阳", "男", "13402664456", IncomeApply.IdCardType.H, "422202199510253455", "医生", "湖北省应城市城北办事处柳林村36", IncomeApply.RelationalType.TS))))
                .setBackCard(JsonUtil.toJson(List.of(
                        new IncomeApply.BackCard("316613046", "YX-102", "李四", IncomeApply.BackName.中国建农业银行, IncomeApply.BackName.中国建农业银行.getCode(), IncomeApply.BackName.中国建农业银行.getNum()),
                        new IncomeApply.BackCard("316613046", "YX-102", "李四", IncomeApply.BackName.中国建工商银行, IncomeApply.BackName.中国建工商银行.getCode(), IncomeApply.BackName.中国建工商银行.getNum()))));

        List<IncomeApply> list=List.of(incomeApplyOne,incomeApplyTwo);
        System.out.println(JsonUtil.toJson(list, true));


    }
}
