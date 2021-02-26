package com.rwp;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.stream.IntStream;

public class Main {
    static Semaphore[] Forks = new Semaphore[5];
    static Philosph[] Philos = new Philosph[5];
    static CyclicBarrier w = new CyclicBarrier(6);

    public static void main(String[] args) throws BrokenBarrierException, InterruptedException {
        IntStream.range(0,Forks.length).forEach(x->{
            Forks[x]=new Semaphore(1,true);
            Philos[x]=new Philosph(Forks,x,w);
        });

        IntStream.range(0, Forks.length).forEach(x-> Philos[x].start());

        w.await();
	// write your code here
    }
}
