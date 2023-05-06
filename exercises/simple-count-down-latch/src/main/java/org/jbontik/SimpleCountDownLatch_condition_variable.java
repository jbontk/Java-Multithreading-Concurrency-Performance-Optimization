package org.jbontik;


import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SimpleCountDownLatch_condition_variable {
    private int count;
    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();


    public SimpleCountDownLatch_condition_variable(int count) {
        this.count = count;
        if (count < 0) {
            throw new IllegalArgumentException("count cannot be negative");
        }
    }

    /**
     * Causes the current thread to wait until the latch has counted down to zero.
     * If the current count is already zero then this method returns immediately.
     */
    public void await() throws InterruptedException {
        try {
            lock.lock();
            while (count > 0) {
                condition.await();
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     *  Decrements the count of the latch, releasing all waiting threads when the count reaches zero.
     *  If the current count already equals zero then nothing happens.
     */
    public void countDown() {
        try {
            lock.lock();
            if (count > 0) {
                count--;
            }
            if (count == 0) {
                condition.signalAll();
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Returns the current count.
     */
    public int getCount() {
        try {
            lock.lock();
            return count;
        } finally {
            lock.unlock();
        }
    }
}
