package com.weshare.repay.provider;

import com.weshare.repay.RepayApplication;
import com.weshare.repay.entity.RepaySummary;
import com.weshare.repay.repo.ReceiptDetailRepo;
import com.weshare.repay.repo.RepayPlanRepo;
import com.weshare.repay.repo.RepaySummaryRepo;
import com.weshare.repay.repo.RepayTransFlowRepo;
import com.weshare.service.api.client.RepayClient;
import com.weshare.service.api.enums.FeeTypeEnum;
import com.weshare.service.api.vo.DueBillNoAndTermDueDate;
import com.weshare.service.api.vo.Tuple2;
import com.weshare.service.api.vo.Tuple4;
import common.JsonUtil;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.repay.provider
 * @date: 2021-05-29 02:42:35
 * @describe:
 */
@SpringBootTest
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
        System.out.println("tuple4s:\n"+ JsonUtil.toJson(tuple4s,true));
    }
    @Test
    public void testTwo(){
        List<Tuple2<BigDecimal, FeeTypeEnum>> tuple2s = repayClient.getReceiptDetailTwo("YX-102", 1).getData();
        System.out.println("tuple2s:\n"+ JsonUtil.toJson(tuple2s,true));
    }
    @Test
    public void testSorted(){
        List<Tuple4<BigDecimal, BigDecimal, LocalDate, Integer>> tuple4s = repayClient.getRepayPlanFourth("YX-102").getData();
        List<LocalDate> dateList = tuple4s.stream().sorted(Comparator.comparing(Tuple4::getThird,Comparator.reverseOrder()))
                .map(Tuple4::getThird).sorted(Comparator.reverseOrder()).collect(Collectors.toList());
        System.out.println(dateList);

        LocalDate min = tuple4s.stream().max(Comparator.comparing(Tuple4::getThird,Comparator.reverseOrder())).map(Tuple4::getThird).orElse(null);
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
}