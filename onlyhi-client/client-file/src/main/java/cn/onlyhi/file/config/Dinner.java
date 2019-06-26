package cn.onlyhi.file.config;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

public class Dinner implements Runnable{
    private String name;
    private ConcurrentLinkedQueue<String> queue;
    private CountDownLatch count;

    public Dinner(String name, ConcurrentLinkedQueue<String> queue, CountDownLatch count) {
        this.name = name;
        this.queue = queue;
        this.count = count;
    }
    @Override
    public void run() {
        //while (queue.size() > 0){
        while (!queue.isEmpty()){
            //从队列取出一个元素 排队的人少一个
            System.out.println("【" +queue.poll() + "】----已吃完...， 饭桌编号：" + name);
        }
        count.countDown();//计数器-1
    }
}
