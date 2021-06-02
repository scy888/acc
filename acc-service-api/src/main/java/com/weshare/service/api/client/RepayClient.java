package com.weshare.service.api.client;

import com.weshare.service.api.entity.ReceiptDetailReq;
import com.weshare.service.api.entity.RepayPlanReq;
import com.weshare.service.api.entity.RepaySummaryReq;
import com.weshare.service.api.entity.RepayTransFlowReq;
import com.weshare.service.api.result.Result;
import com.weshare.service.api.vo.Tuple3;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.client
 * @date: 2021-04-26 21:54:57
 * @describe:
 */

@RequestMapping("/client")
public interface RepayClient {

    @GetMapping("/getRepayClient/{repayClient}/{isInvoking}")
    String getRepayClient(@PathVariable("repayClient") String repayClient,
                          @PathVariable("isInvoking") Boolean isInvoking);

    @PostMapping("/saveRepayPlan")
    Result saveRepayPlan(@RequestBody List<RepayPlanReq> list);

    @PostMapping("/saveRepaySummary")
    Result saveRepaySummary(@RequestBody List<RepaySummaryReq> list);

    @PostMapping("/findRepaySummaryByDueBillNoIn")
    Result<List<RepaySummaryReq>> findRepaySummaryByDueBillNoIn(@RequestBody List<String> list);

    @GetMapping("/RefreshRepaySummaryCurrentTerm/{projectNo}/{batchDate}")
    Result RefreshRepaySummaryCurrentTerm(@PathVariable("projectNo") String projectNo,
                                          @PathVariable("batchDate") String batchDate);

    @PostMapping("/UpdateRepaySummaryCurrentTerm")
    Result UpdateRepaySummaryCurrentTerm(@RequestBody UpdateRepaySummaryCurrentTerm updateRepaySummaryCurrentTerm);

    @PostMapping("/saveAllRepayTransFlow")
    Result saveAllRepayTransFlow(@RequestBody List<RepayTransFlowReq> list, @RequestParam("batchDate") String batchDate);

    @PostMapping("/saveAllReceiptDetail")
    Result saveAllReceiptDetail(@RequestBody List<ReceiptDetailReq> list, @RequestParam("batchDate") String batchDate);

    @GetMapping("/findRepayPlanListByDueBillNo/{dueBillNo}")
    Result<List<RepayPlanReq>> findRepayPlanListByDueBillNo(@PathVariable("dueBillNo") String dueBillNo);

    @GetMapping("/getFlowSn/{dueBillNo}/{batchDate}")
    Result<List<Tuple3<String,String, BigDecimal>>> getFlowSn(@PathVariable("dueBillNo") String dueBillNo, @PathVariable("batchDate") String batchDate);

    @GetMapping("/getTotalTerm")
    Result<Integer> getTotalTerm(@RequestParam("dueBillNo")String dueBillNo,@RequestParam("projectNo")String projectNo);

    @Data
    @Accessors(chain = true)
    class UpdateRepaySummaryCurrentTerm {

        private String batchDate;
        private String dueBillNo;
        private Integer currentTerm;
    }

}
