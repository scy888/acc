package com.weshare.batch.entity;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.batch.entity
 * @date: 2021-05-25 12:34:49
 * @describe:
 */
public class Check {

    public static class 可见性 {

        public int num = 0;

        public void changeNum(int changeNum) {
            num = changeNum;
        }

        public static void main(String[] args)  {
            可见性 可见性 = new 可见性();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Thread thread = Thread.currentThread();
                    thread.setName("Thread_1");
                    System.out.println("当前线程已经执行的名字:" + thread.currentThread().getName() + " 变量num:" + 可见性.num);
                }
            }).start();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    可见性.changeNum(10);
                    Thread thread = Thread.currentThread();
                    thread.setName("Thread_2");
                    System.out.println("当前线程已经执行的名字:" + thread.currentThread().getName() + " 变量num:" + 可见性.num);
                }
            }).start();
        }
    }
}
