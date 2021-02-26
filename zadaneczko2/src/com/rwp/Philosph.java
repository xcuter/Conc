package com.rwp;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
public class Philosph extends Thread {
    Semaphore[] Fr;
    AtomicInteger ID = new AtomicInteger(0);
    CyclicBarrier w;

    Philosph(Semaphore[] fors, int ID,CyclicBarrier wr) {
        this.ID.set(ID);
        this.Fr = fors;
        this.w = wr;
    }

    public int getID() {
        return this.ID.get();
    }

    public void getLeftFork() throws InterruptedException {
        if (Fr[this.getID()].availablePermits() != 0) {
            Fr[this.getID()].tryAcquire();
            System.out.println(this.getID()+": leftForkAquired ...");
        }
    }

    public void getRightFork() throws InterruptedException{
        if(Fr[(this.getID()+1)%Fr.length].availablePermits() != 0) {
            Fr[(this.getID() + 1) % Fr.length].acquire();
            System.out.println(this.getID()+": RightForkAquired ...");
        }
    }

    public void putForks(){
        Fr[this.getID()].release();
        Fr[(this.getID()+1)%Fr.length].release();
    }

    public void eat() throws InterruptedException {
        System.out.println(this.getID()+": Eating . . .");
        sleep(500);
    }

    public void think() throws InterruptedException {
        System.out.println(this.getID()+": Thinking . . .");
        sleep(100);
    }

    @Override
    public void run(){
        try {
            w.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
        while (true){
            try {
                this.think();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                try {
                    this.getLeftFork();
                    this.getRightFork();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    try {
                        this.eat();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }finally {
                        this.putForks();
                    }
                }
            }

        }
    }
}
