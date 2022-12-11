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

/**
 * Threads Creation - Part 2. Thread Inheritance
 * https://www.udemy.com/java-multithreading-concurrency-performance-optimization
 */

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class Main {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");


    public static final int MAX_PASSWORD = 9999;

    public static void main(String[] args) {
        Random random = new Random();

        Vault vault = new Vault(random.nextInt(MAX_PASSWORD));

        List<Thread> threads = new ArrayList<>();

        CountDownLatch latch = new CountDownLatch(2);


        threads.add(new AscendingHackerThread(vault, latch));
        threads.add(new DescendingHackerThread(vault, latch));
        threads.add(new PoliceThread(latch));


        for (Thread thread : threads) {
            thread.start();
        }
    }

    private static class Vault {
        private final int password;

        public Vault(int password) {
            this.password = password;
        }

        public boolean isCorrectPassword(int guess) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
            }
            return this.password == guess;
        }
    }

    private static abstract class HackerThread extends Thread {
        protected Vault vault;

        public HackerThread(Vault vault) {
            this.vault = vault;
            this.setName(this.getClass().getSimpleName());
            this.setPriority(Thread.MAX_PRIORITY);
        }

        @Override
        public void start() {
            System.out.println("Starting thread " + this.getName());
            super.start();
        }
    }

    private static class AscendingHackerThread extends HackerThread {

        private final CountDownLatch latch;

        public AscendingHackerThread(Vault vault, CountDownLatch latch) {
            super(vault);
            this.latch = latch;
        }

        @Override
        public void run() {
            for (int guess = 0; guess < MAX_PASSWORD; guess++) {
                if (vault.isCorrectPassword(guess)) {
                    System.out.println(this.getName() + " guessed the password " + guess);
                    System.exit(0);
                }
                latch.countDown();
            }
        }
    }

    private static class DescendingHackerThread extends HackerThread {

        private final CountDownLatch latch;

        public DescendingHackerThread(Vault vault, CountDownLatch latch) {
            super(vault);
            this.latch = latch;
        }

        @Override
        public void run() {
            for (int guess = MAX_PASSWORD; guess >= 0; guess--) {
                if (vault.isCorrectPassword(guess)) {
                    System.out.println(this.getName() + " guessed the password " + guess);
                    System.exit(0);
                }
                latch.countDown();
            }
        }
    }

    private static class PoliceThread extends Thread {
        private final CountDownLatch latch;

        public PoliceThread(CountDownLatch latch) {
            this.latch = latch;
            setPriority(MIN_PRIORITY);
        }

        @Override
        public void run() {

            try {
                System.out.printf("%s -- Waiting to start\n", LocalDateTime.now().format(TIME_FORMATTER));
                latch.await();
                System.out.printf("%s -- Started\n", LocalDateTime.now().format(TIME_FORMATTER));
                for (int i = 10; i > 0; i--) {
                    Thread.sleep(1000);

                    System.out.println(i);
                }
            } catch (InterruptedException e) {
            }

            System.out.println("Game over for you hackers");
            System.exit(0);
        }
    }
}
