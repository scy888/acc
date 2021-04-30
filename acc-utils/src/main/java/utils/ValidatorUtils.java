package utils;

import java.io.Serializable;
import java.util.List;

/**
 * @author: scyang
 * @program: tensquare_parent
 * @date: 2019-09-23 22:15:38
 */
public  class ValidatorUtils implements Serializable {
    private boolean isPass;
    /**
     * @Author:scyang @Date:2019/9/23 22:19 是否通过
     */
    private String message;
    /**
     * @Author:scyang @Date:2019/9/23 22:19 校验信息
     */
    private List<ValidatorRule> validatorRules;

    /**
     * @Author:scyang @Date:2019/9/23 22:20 校验规则
     */
    public boolean isPass() {
        return isPass;
    }

    public void setPass(boolean pass) {
        isPass = pass;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ValidatorRule> getValidatorRules() {
        return validatorRules;
    }

    public void setValidatorRules(List<ValidatorRule> validatorRules) {
        this.validatorRules = validatorRules;
    }

    public ValidatorUtils() {
    }

    //****************************************************************************************
    public static class ValidatorRule {
        /**
         * @Author:scyang @Date:2019/9/23 22:24 校验参数
         */
        private String key;
        /**
         * @Author:scyang @Date:2019/9/23 22:25 参数描叙
         */
        private String desc;
        /**
         * @Author:scyang @Date:2019/9/23 22:25 校验方式
         */
        private ValidatorType validatorType;
        /**
         * @Author:scyang @Date:2019/9/23 22:26 参数值
         */
        private String value;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public ValidatorType getValidatorType() {
            return validatorType;
        }

        public void setValidatorType(ValidatorType validatorType) {
            this.validatorType = validatorType;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public ValidatorRule(String key, String desc, ValidatorType validatorType, String value) {
            this.key = key;
            this.desc = desc;
            this.validatorType = validatorType;
            this.value = value;
        }

        public ValidatorRule(String key, String desc, ValidatorType validatorType) {
            this.key = key;
            this.desc = desc;
            this.validatorType = validatorType;
        }
    }

    //***********************************************************************************************
    public enum ValidatorType {
        NOTNULL("不为空"),
        MAXLENGTH("最大长度"),
        MAINLENGTH("最小长度"),
        DATETYPE("日期格式");
        private String message;

       private ValidatorType(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
