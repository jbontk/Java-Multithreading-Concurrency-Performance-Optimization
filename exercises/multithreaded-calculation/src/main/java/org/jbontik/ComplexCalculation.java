package org.jbontik;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class ComplexCalculation {
    public BigInteger calculateResult(BigInteger base1, BigInteger power1, BigInteger base2, BigInteger power2) throws Exception {
        BigInteger result;

        List<PowerCalculatingThread> threads = new ArrayList<>(2);

        threads.add(new PowerCalculatingThread(base1, power1));
        threads.add(new PowerCalculatingThread(base2, power2));

        for (Thread thread : threads) {
            thread.start();
            thread.join();
        }

        result = threads.stream().map(PowerCalculatingThread::getResult)
                .reduce(BigInteger.ZERO, BigInteger::add);

        return result;
    }

    private static class PowerCalculatingThread extends Thread {
        private BigInteger result = BigInteger.ONE;
        private final BigInteger base;
        private final BigInteger power;

        public PowerCalculatingThread(BigInteger base, BigInteger power) {
            this.base = base;
            this.power = power;
        }

        @Override
        public void run() {
           for (BigInteger i = BigInteger.ZERO ; i.compareTo(power) != 0; i = i.add(BigInteger.ONE)) {
               result = result.multiply(base);
           }
        }

        public BigInteger getResult() { return result; }
    }


}
