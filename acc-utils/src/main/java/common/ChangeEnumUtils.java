package common;

import lombok.Getter;
import lombok.Value;

import java.util.Arrays;
import java.util.List;

/**
 * @author: scyang
 * @program: acc
 * @package: common
 * @date: 2021-05-25 14:07:56
 * @describe:
 */

@Getter
public enum ChangeEnumUtils {
    YXMS("WS121212", List.of(
            new TransferEnum("termStatus", "提前结清", "PRE"),
            new TransferEnum("termStatus", "正常结清", "NORMAL"),
            new TransferEnum("termStatus", "逾期结清", "OVERDUE"),

            new TransferEnum("loanStatus", "01", "成功"),
            new TransferEnum("loanStatus", "02", "失败"),

            new TransferEnum("loanStatusEnum", "01", "SUCCESS"),

            new TransferEnum("transactionResult", "01", "成功"),
            new TransferEnum("transactionResult", "02", "失败"),
            new TransferEnum("transactionResult", "03", "处理中"),

            new TransferEnum("debitType", "01", "正常扣款"),
            new TransferEnum("debitType", "02", "提前结清扣款"),
            new TransferEnum("debitType", "03", "逾期扣款"),
            new TransferEnum("debitType", "04", "减免扣款"),
            new TransferEnum("debitType", "05", "退票扣款"),

            new TransferEnum("receiptType", "01", "NORMAL"),
            new TransferEnum("receiptType", "02", "PRE"),
            new TransferEnum("receiptType", "03", "OVERDUE"),
            new TransferEnum("receiptType", "04", "REDUCE"),
            new TransferEnum("receiptType", "05", "REFUND"),

            new TransferEnum("refundStatus", "01", "退票成功"),
            new TransferEnum("refundStatus", "02", "退票失败"),

            new TransferEnum("transFlowType", "正常还款", "正常还款"),
            new TransferEnum("transFlowType", "提前还款", "提前还款"),
            new TransferEnum("transFlowType", "退票", "退票"),
            new TransferEnum("transFlowType", "逾期还款", "逾期还款"),
            new TransferEnum("transFlowType", "减免", "减免"),

            new TransferEnum("transStatus", "成功", "成功"),
            new TransferEnum("transStatus", "失败", "失败")


    ));

    private String projectNo;
    private List<TransferEnum> transferEnumList;

    ChangeEnumUtils(String projectNo, List<TransferEnum> transferEnumList) {
        this.projectNo = projectNo;
        this.transferEnumList = transferEnumList;
    }

    public static <T extends Enum<T>> T changeEnum(String projectNo, String group, String channelValue, Class<T> clazz) {
        String transFerValue = Arrays.stream(ChangeEnumUtils.values()).filter(e -> e.projectNo.equals(projectNo))
                .findFirst()
                .flatMap(e -> e.transferEnumList.stream()
                        .filter(a -> a.getGroup().equals(group)
                                && a.channelValue.equals(channelValue))
                        .findFirst()
                ).map(TransferEnum::getTransferValue).orElse("");

        return Enum.valueOf(clazz, transFerValue);

    }

    @Value
    public static class TransferEnum {
        private String group;//要转化的枚举字段名
        private String channelValue;//外部渠道的枚举值
        private String transferValue;//要转化渠道的枚举值
    }
}
