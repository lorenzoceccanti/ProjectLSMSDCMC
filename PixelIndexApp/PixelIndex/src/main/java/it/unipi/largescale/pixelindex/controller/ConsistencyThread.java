package it.unipi.largescale.pixelindex.controller;

import java.util.concurrent.BlockingQueue;

public class ConsistencyThread extends Thread{
    private boolean running = true;
    private final BlockingQueue<Runnable> taskQueue;
    public ConsistencyThread (BlockingQueue<Runnable> taskQueue) {
        this.taskQueue = taskQueue;
    }

    @Override
    public void run(){
        try{
            while(running || !taskQueue.isEmpty()){
                Runnable task = taskQueue.take();
                task.run();
                Thread.sleep(5000);
            }
        }catch(InterruptedException ex)
        {
            Thread.currentThread().interrupt();
            System.out.println("The consistency thread has been interrupted");
        }
    }

    public void addTask(Runnable task) {
        taskQueue.add(task);
    }
    public void stopThread() {
        this.running = false;
    }
}