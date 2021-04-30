package common;


import org.junit.Test;

/**
 * 雪花全局唯一id生成策略
 *
 * @author qujiayuan
 * @since 2020.07.04
 */

public class SnowFlake {
    /**
     * 起始的时间戳 2020-01-01 00:00:00
     */
    private final static long START_TEMP = 1577808000000L;
    /**
     * 每一部分占用的位数，就三个
     * 序列号占用的位数
     */
    private final static long SEQUENCE_BIT = 12;
    /**
     * 机器标识占用的位数
     */
    private final static long MACHINE_BIT = 5;
    /**
     * 数据中心占用的位数
     */
    private final static long DATA_CENTER_BIT = 5;
    /**
     * 每一部分最大值
     */
    private final static long MAX_DATA_CENTER_NUM = -1L ^ (-1L << DATA_CENTER_BIT);
    private final static long MAX_MACHINE_NUM = -1L ^ (-1L << MACHINE_BIT);
    private final static long MAX_SEQUENCE = -1L ^ (-1L << SEQUENCE_BIT);
    // 每一部分向左的位移
    private final static long MACHINE_LEFT = SEQUENCE_BIT;
    private final static long DATACENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;
    private final static long TIMESTMP_LEFT = DATACENTER_LEFT + DATA_CENTER_BIT;
    /**
     * 数据中心
     */
    private long dataCenterId;
    /**
     * 机器标识
     */
    private long machineId;
    /**
     * 序列号
     */
    private long sequence = 0L;
    /**
     * 上一次时间戳
     */
    private long lastTemp = -1L;

    public SnowFlake(long dataCenterId, long machineId) {
        if (dataCenterId > MAX_DATA_CENTER_NUM || dataCenterId < 0) {
            throw new IllegalArgumentException("dataCenterId 不能大于 MAX_DATA_CENTER_NUM 或者小于0");
        }
        if (machineId > MAX_MACHINE_NUM || machineId < 0) {
            throw new IllegalArgumentException("machineId 不能大于 MAX_MACHINE_NUM 或者小于0");
        }
        this.dataCenterId = dataCenterId;
        this.machineId = machineId;
    }

    /**
     * 产生下一个ID
     *
     * @return
     */
    public synchronized long nextId() {
        long currStmp = getNewstmp();
        if (currStmp < lastTemp) {
            throw new RuntimeException("时钟已回拨，不再生成id");
        }

        if (currStmp == lastTemp) {
            //if条件里表示当前调用和上一次调用落在了相同毫秒内，只能通过第三部分，序列号自增来判断为唯一，所以+1.
            sequence = (sequence + 1) & MAX_SEQUENCE;
            //同一毫秒的序列数已经达到最大，只能等待下一个毫秒
            if (sequence == 0L) {
                currStmp = getNextMill();
            }
        } else {
            //不同毫秒内，序列号置为0
            //执行到这个分支的前提是currTimestamp > lastTimestamp，说明本次调用跟上次调用对比，已经不再同一个毫秒内了，这个时候序号可以重新回置0了。
            sequence = 0L;
        }

        lastTemp = currStmp;
        //就是用相对毫秒数、机器ID和自增序号拼接
        //时间戳部分
        return (currStmp - START_TEMP) << TIMESTMP_LEFT
                | dataCenterId << DATACENTER_LEFT      //数据中心部分
                | machineId << MACHINE_LEFT            //机器标识部分
                | sequence;                            //序列号部分
    }

    private long getNextMill() {
        long mill = getNewstmp();
        while (mill <= lastTemp) {
            mill = getNewstmp();
        }
        return mill;
    }

    private long getNewstmp() {
        return System.currentTimeMillis();
    }

    /**
     * 构造方法设置机器码：第5个机房的第6台机器
     */
    public static SnowFlake snowFlake = new SnowFlake(5, 6);

    public static SnowFlake getInstance() {
        return snowFlake;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            System.out.println(SnowFlake.getInstance().nextId());
        }
    }
}
