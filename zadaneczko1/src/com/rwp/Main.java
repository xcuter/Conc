package com.rwp;

import com.google.common.util.concurrent.Monitor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.Thread.sleep;

public class Main {
    static int iReaders = 20;
    static Monitor monitor = new Monitor();
    static AtomicBoolean swi = new AtomicBoolean(false);

    static ExecutorService readexec = Executors.newFixedThreadPool(20000);
    static ExecutorService writeexec = Executors.newFixedThreadPool(1);

    static Monitor.Guard Readers = new Monitor.Guard(monitor) {
        @Override
        public boolean isSatisfied() {
            return (iReaders > 0);
        }
    };

    static Monitor.Guard Writer = new Monitor.Guard(monitor) {
        @Override
        public boolean isSatisfied() {
            return swi.get();
        }
    };



    public static void main(String[] args) throws InterruptedException {
        Runnable runnableTaskRead = () ->{
            while (true){
                try {
                    monitor.enterWhen(Readers);
                    try {
                        System.out.println(Thread.currentThread().getId()+": reading ...");
                    } finally {
                        iReaders--;
                        monitor.leave();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        Runnable runnableTaskWrite= () ->{
            while (true){
                try {
                    monitor.enterWhen(Writer);
                    try {
                        System.out.println("Write !");
                        iReaders=100;
                    } finally {
                        swi.set(false);
                        monitor.leave();
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        readexec.execute(new Thread(runnableTaskRead));
        writeexec.execute(new Thread(runnableTaskWrite));
        while (true) {
                swi.set(true);
        }
    }
}