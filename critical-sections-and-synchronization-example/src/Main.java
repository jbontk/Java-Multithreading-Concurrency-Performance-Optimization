/*
 * MIT License
 *
 * Copyright (c) 2019 Michael Pogrebinsky
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.util.Arrays;

/**
 * Critical Section & Synchronization
 * https://www.udemy.com/java-multithreading-concurrency-performance-optimization
 */
public class Main {

    private static final int COUNT = 100_000_000;
    private static boolean THREAD_SAFE;

    public static void main(String[] args) throws InterruptedException {
        THREAD_SAFE = getThreadSafe(args);
        System.out.printf("Is thread safe? %s\n", THREAD_SAFE);

        InventoryCounter inventoryCounter = new InventoryCounter();
        IncrementingThread incrementingThread = new IncrementingThread(inventoryCounter);
        DecrementingThread decrementingThread = new DecrementingThread(inventoryCounter);

        incrementingThread.start();
        decrementingThread.start();

        incrementingThread.join();
        decrementingThread.join();

        System.out.println("We currently have " + inventoryCounter.getItems() + " items");
    }

    private static boolean getThreadSafe(String[] args) {
        try {
            return Boolean.parseBoolean(args[0]);
        } catch (Exception e) {
            System.err.printf("Failed to parse boolean from %s. Error: %s\n", Arrays.toString(args), e.getMessage());
            return false;
        }
    }

    public static class DecrementingThread extends Thread {

        private final InventoryCounter inventoryCounter;

        public DecrementingThread(InventoryCounter inventoryCounter) {
            this.inventoryCounter = inventoryCounter;
        }

        @Override
        public void run() {
            for (int i = 0; i < COUNT; i++) {
                inventoryCounter.decrement();
            }
        }
    }

    public static class IncrementingThread extends Thread {

        private final InventoryCounter inventoryCounter;

        public IncrementingThread(InventoryCounter inventoryCounter) {
            this.inventoryCounter = inventoryCounter;
        }

        @Override
        public void run() {
            for (int i = 0; i < COUNT; i++) {
                inventoryCounter.increment();
            }
        }
    }

    private static class InventoryCounter {
        private int items = 0;

        final Object lock = new Object();

        public void increment() {
            if (THREAD_SAFE) {
                synchronized (this.lock) {
                    items++;
                }
            } else {
                items++;
            }
        }

        public void decrement() {
            if (THREAD_SAFE) {
                synchronized (this.lock) {
                    items--;
                }
            } else {
                items--;
            }
        }

        public int getItems() {
            if (THREAD_SAFE) {
                synchronized (this.lock) {
                    return items;
                }
            } else {
                return items;
            }
        }
    }
}
