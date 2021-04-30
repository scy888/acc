package common;

/**
 * @author: scyang
 * @program: ssm_super
 * @package: com.itheima.commom
 * @date: 2019-10-23 20:31:26
 * @describe:
 */
public enum SwitchEnum {

    OPEN("1",true),//开
    CLOSE("0",false);//关

    private String key;
    private boolean value;

    SwitchEnum(String key, boolean value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }
}
