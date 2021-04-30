package entity;

import common.CaseReasonEnum;
import common.IdGeneratorConfig;
import common.SnowFlake;
import common.SpringContextUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: scyang
 * @program: tensquare_parent
 * @package: entity
 * @date: 2020-01-30 13:27:04
 * @describe:
 */
public class Constant {
    public static final String IS_HOT = "0";
    public static final String IS_NOT_HOT = "1";

    public static final String DEPARTMENT_CODE_ONE = "1";
    public static final String DEPARTMENT_CODE_TWO = "2";
    public static final String DEPARTMENT_CODE_THREE = "3";
    public static final String DEPARTMENT_CODE_FOUR = "4";
    public static final Map<String, Object> DEPARTMENT_CODE_MAP = new HashMap<>();

    static {
        DEPARTMENT_CODE_MAP.put(DEPARTMENT_CODE_ONE, "中国建设银行");
        DEPARTMENT_CODE_MAP.put(DEPARTMENT_CODE_TWO, "中国农业银行");
        DEPARTMENT_CODE_MAP.put(DEPARTMENT_CODE_THREE, "中国工商银行");
        DEPARTMENT_CODE_MAP.put(DEPARTMENT_CODE_FOUR, "中国招商银行");
    }

    public static final String DEPARTMENT_LEVEL_ONE = "1";
    public static final String DEPARTMENT_LEVEL_TWO = "2";
    public static final String DEPARTMENT_LEVEL_THREE = "3";
    public static final String DEPARTMENT_LEVEL_FOUR = "4";

    public static final String CASE_STATUS_ONE = "1";
    public static final String CASE_STATUS_TWO = "2";
    public static final String CASE_STATUS_THREE = "3";
    public static final String CASE_STATUS_FOUR = "4";
    public static final Map<String, Object> CASE_STATUS_MAP = new HashMap<>();

    static {
        CASE_STATUS_MAP.put(CASE_STATUS_ONE, "已结案");
        CASE_STATUS_MAP.put(CASE_STATUS_TWO, "已报案");
        CASE_STATUS_MAP.put(CASE_STATUS_THREE, "已归档");
        CASE_STATUS_MAP.put(CASE_STATUS_FOUR, "已理算");
    }

    public static final String CURRENCY_CODE_CNY = "1";
    public static final String CURRENCY_CODE_HKD = "2";
    public static final String CURRENCY_CODE_AUD = "3";
    public static final String CURRENCY_CODE_TWD = "4";
    public static final Map<String, Object> CURRENCY_CODE_MAP = new HashMap<>();

    static {
        CURRENCY_CODE_MAP.put(CURRENCY_CODE_CNY, "人民币");
        CURRENCY_CODE_MAP.put(CURRENCY_CODE_HKD, "港币");
        CURRENCY_CODE_MAP.put(CURRENCY_CODE_AUD, "澳币");
        CURRENCY_CODE_MAP.put(CURRENCY_CODE_TWD, "台币");
    }

    public static final String CCB = CaseReasonEnum.CASE_REASON_ONE.getStatusCodeArray()[0];
    public static final String ACB = CaseReasonEnum.CASE_REASON_TWO.getStatusCodeArray()[0];
    public static final String ICBC = CaseReasonEnum.CASE_REASON_THREE.getStatusCodeArray()[0];
    public static final String CMB = CaseReasonEnum.CASE_REASON_FOUR.getStatusCodeArray()[0];

    /**
     * 船舶险 SS
     */
    public static final String PRODUCT_TYPE_SS = "SS";
    /**
     * 车险 SB
     */
    public static final String PRODUCT_TYPE_SB = "SB";

    /**
     * 赔付结论类型
     */
    public static final String CONCLUSION_TYPE_PAY = "1";
    /**
     * 赔付
     */
    public static final String CONCLUSION_TYPE_ZERO = "2";
    /**
     * 零结
     */
    public static final String CONCLUSION_TYPE_REJECT = "3";
    /**
     * 拒赔
     */
    public static final String CONCLUSION_TYPE_REPORT_OFF = "4";/** 报案注销 */

    /**
     * 风险反馈值
     */
    public static final String WITHOUT_RISK = "0";
    /**
     * 无风险
     */
    public static final String FALSE_LOSS = "1";
    /**
     * 虚报损失
     */
    public static final String INSURED_CHEAT = "2";
    /**
     * 保险欺诈
     */
    public static final String OTHER_RISK = "3";/** 其他风险 */

    /**
     * 合议类型
     */
    public static final String CONSULTATION_TYPE_REGISTER = "1";
    /**
     * 立案合议
     */
    public static final String CONSULTATION_TYPE_SETTLE = "2";
    /**
     * 结案合议
     */
    public static final String CONSULTATION_TYPE_COURSE = "3";
    /**
     * 过程合议
     */
    public static final String CONSULTATION_TYPE_ALIPAY = "4";/** 支付合议 */

    /**
     * 全国折标件均值
     */
    public static final String FOLDING_UPPER_WARN = "均衡度上限预警阀值";
    public static final String FOLDING_UPPER = "均衡度上限阀值";
    public static final String FOLDING_FLOOR_WARN = "均衡度下限预警阀值";
    public static final String FOLDING_FLOOR = "均衡度下限阀值";
    public static final String FOLDING_AVG = "均衡度平均值";

    /**
     * 支付清单中的赔款类型
     */
    public static final String PAYMENT_TYPE_PAY = "赔款";
    public static final String PAYMENT_TYPE_PREPAY = "预赔";
    public static final String PAYMENT_TYPE_CHASE = "追偿";
    public static final String PAYMENT_TYPE_SALVAGE = "残值";

    public static final List<String> PAYMENT_TYPE_LIST = new ArrayList<>();

    static {
        PAYMENT_TYPE_LIST.add(PAYMENT_TYPE_PAY);
        PAYMENT_TYPE_LIST.add(PAYMENT_TYPE_PREPAY);
        PAYMENT_TYPE_LIST.add(PAYMENT_TYPE_CHASE);
        PAYMENT_TYPE_LIST.add(PAYMENT_TYPE_SALVAGE);
    }

    public static final String COMMON_Y = "Y";
    public static final String COMMON_N = "N";

   @Test
   public void test(){
       System.out.println(new SnowFlake(1,1).nextId());
//       User user = SpringContextUtil.getBean(User.class);
//       user.setAddress("武汉");
//       System.out.println(user);
       System.out.println(new IdGeneratorConfig().snowFlake());
   }
}
