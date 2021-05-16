package com.weshare.service.api.enums;

import lombok.Getter;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.service.api.enums
 * @date: 2021-05-16 21:37:51
 * @describe:
 */

@Getter
public enum ProjectEnum {
    YXMS("WS121212", "易鑫民生", List.of(new Product("0101", "乐花卡"),
            new Product("0102", "乐福卡")),
            new BigDecimal("10000.00"),
            LocalDate.parse("2099-12-31"));


    private String projectNo;
    private String projectName;
    private List<Product> products;
    private BigDecimal maxAmount;
    private LocalDate maxDate;

    ProjectEnum(String projectNo, String projectName, List<Product> products, BigDecimal maxAmount, LocalDate maxDate) {
        this.projectNo = projectNo;
        this.projectName = projectName;
        this.products = products;
        this.maxAmount = maxAmount;
        this.maxDate = maxDate;
    }

   @Value
    public static class Product {
        private String productNo;
        private String projectName;
    }

}
