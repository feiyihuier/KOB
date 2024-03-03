package com.kob.botrunningsystem.service.impl.utils;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BotPool extends Thread{//生产者消费者模型
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private final Queue<Bot> bots = new LinkedList<>();//两个线程同时对它操作

    public void addBot(Integer userId, String botCode, String input){
        lock.lock();
        try{
            bots.add(new Bot(userId, botCode, input));
            condition.signalAll();
        }finally {
            lock.unlock();
        }
    }
    private void consume(Bot bot){//执行Bot代码的函数
        Consumer consumer = new Consumer();
        consumer.startTimeout(2000, bot);//每个代码最多执行两秒
    }

    @Override
    public void run() {
        while(true){//消息队列
            lock.lock();
            if(bots.isEmpty()){
                try {
                    condition.await();//让当前的线程去等待，直到它被唤醒或者被中断
                    //await()函数包含锁的释放：lock.unlock()
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    lock.unlock();
                    break;
                }
            }else{
                Bot bot = bots.remove();//取出并移除对头
                lock.unlock();
                consume(bot);//比较耗时，可能会执行好几秒钟，是编译执行Bot代码
            }
        }
    }
}
