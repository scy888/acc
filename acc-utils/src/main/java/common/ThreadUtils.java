package common;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author: scyang
 * @program: tensquare_parent
 * @package: common
 * @date: 2020-02-08 19:44:07
 * @describe: 线程池工具类
 */
public class ThreadUtils {
    private static final Logger logger= LoggerFactory.getLogger(ThreadUtils.class);
    private static final ExecutorService pool= Executors.newFixedThreadPool(5);

    public static void getThread(Runnable runnable) throws Exception {
        for (int i = 0; i < 5; i++) {
            //logger.info("i{}:"+i);
            pool.execute(runnable);
            TimeUnit.MICROSECONDS.sleep(500);
        }
        //pool.shutdown();
    }
    @Test
    public void test() throws Exception {
        getThread(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName()+": "+"盛重阳");
            }
        });
    }
}
