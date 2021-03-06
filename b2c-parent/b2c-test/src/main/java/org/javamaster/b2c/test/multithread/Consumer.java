package org.javamaster.b2c.test.multithread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;

/**
 * @author yudong
 * @date 2019/12/10
 */
public class Consumer {
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 容量为1的缓冲池
     */
    private final Queue<String> cachePoolQueue;

    public Consumer(Queue<String> cachePoolQueue) {
        this.cachePoolQueue = cachePoolQueue;
    }

    public void consume() {
        // 尝试获取cachePoolQueue对象锁
        synchronized (cachePoolQueue) {
            try {
                // 判断缓冲池状态,注意对于条件的判断须放在一个while循环里
                while (cachePoolQueue.size() == 0) {
                    cachePoolQueue.wait();// 缓冲池空,无可消费订单号,当前线程等待并释放cachePoolQueue对象锁,
                                          // 即线程会阻塞在这一行,无法继续往下执行,直到收到通知,也就是有其他
                                          // 线程调用了notifyAll方法(也就是生产者线程),此时线程被唤醒,重新尝
                                          // 试获取锁,如果获取到了,则线程就可以继续往下执行了
                }
                String orderCode = cachePoolQueue.poll();
                System.out.println("线程名称"+ Thread.currentThread().getName() + " 消费者消费订单号:" + orderCode);
                // 缓冲池发生变化,通知所有等待cachePoolQueue对象锁的线程,由这些线程重新去竞争锁
                cachePoolQueue.notifyAll();
            } catch (InterruptedException e) {
                logger.error("interrupted", e);
            }
        }
    }
}
