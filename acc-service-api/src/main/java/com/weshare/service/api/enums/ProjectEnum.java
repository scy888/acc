package com.weshare.service.api.enums;

import lombok.Getter;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
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
            LocalDate.parse("2099-12-31")),

    BDGM("WS121213", "百度国民", List.of(new Product("0101", "乐花卡"),
            new Product("0102", "乐福卡")),
            new BigDecimal("10000.00"),
            LocalDate.parse("2099-12-31")),

    HTGY("WS121214", "汇通国银", List.of(new Product("0101", "乐花卡"),
            new Product("0102", "乐福卡")),
            new BigDecimal("10000.00"),
            LocalDate.parse("2099-12-31")),

    LXYX("WS121215", "乐信云信", List.of(new Product("0101", "乐花卡"),
            new Product("0102", "乐福卡")),
            new BigDecimal("10000.00"),
            LocalDate.parse("2099-12-31")),

    GQZL("WS121216", "广汽租赁", List.of(new Product("0101", "乐花卡"),
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
        private String productName;
    }

    /**
     * 通过项目编号和产品编号获取产品名称
     */
    public static String getProductName(String projectNo, String productNo) {
        for (ProjectEnum projectEnum : ProjectEnum.values()) {
            if (projectEnum.getProjectNo().equals(projectNo)) {
                List<Product> productList = projectEnum.getProducts();
                for (Product product : productList) {
                    if (product.getProductNo().equals(productNo)) {
                        return product.getProductName();
                    }
                }
            }
        }
        return "未知";
    }

    public static String getProductName_(String projectNo, String productNo) {

//        Optional<List<Product>> optionalList = Arrays.stream(ProjectEnum.values())
//                .filter(project -> project.getProjectNo().equals(projectNo))
//                .findFirst().map(ProjectEnum::getProducts);
//
//        if (!optionalList.isEmpty()) {
//            return optionalList.get().stream().filter(product -> product.getProductNo().equals(productNo))
//                    .findFirst()
//                    .map(Product::getProductName)
//                    .orElse("未知");
//        }
//        return "未知";

        return Arrays.stream(ProjectEnum.values())
                .filter(project -> project.getProjectNo().equals(projectNo))
                .findFirst()
                .flatMap(project -> project.getProducts().stream().filter(product -> product.getProductNo().equals(productNo)).findFirst())
                .map(Product::getProductName)
                .orElse("未知");
    }
}
