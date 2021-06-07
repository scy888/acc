package com.weshare.batch.test;

import org.junit.Test;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.batch.test
 * @date: 2021-06-05 21:54:28
 * @describe:
 */
public class MyThread implements Runnable {

    private static int ticket = 100;

    @Override
    public void run() {

        while (true) {

            if (ticket > 0) {
                System.out.println("当前窗口:" + Thread.currentThread().getName() + " 正在卖第:" + ticket-- + "张票");
                //Thread.sleep(100);
            }
        }
    }

    public static void main(String[] args) {
        MyThread myThread = new MyThread();
        new Thread(myThread,"窗口-1").start();
        new Thread(myThread,"窗口-2").start();
        new Thread(myThread,"窗口-3").start();
        new Thread(myThread,"窗口-4").start();
    }
    @Test
    public void test() {

    }
}
