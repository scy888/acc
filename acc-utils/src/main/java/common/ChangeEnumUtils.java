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
            new TransferEnum("termStatus", "逾期结清", "OVERDUE")));

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
