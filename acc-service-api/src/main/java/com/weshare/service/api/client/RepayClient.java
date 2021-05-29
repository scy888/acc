package com.weshare.service.api.client;

import com.weshare.service.api.entity.RepayPlanReq;
import com.weshare.service.api.entity.RepaySummaryReq;
import com.weshare.service.api.result.Result;
import org.springframework.web.bind.annotation.*;

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
}
