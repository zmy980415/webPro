package com.example.demo.entity;

import java.util.concurrent.*;

public class TreadPoolFactory {
    public static ThreadPoolExecutor executor = new ThreadPoolExecutor(
            3,
            5,
            5,
            TimeUnit.MINUTES, new ArrayBlockingQueue<>(100));
    public static ThreadPoolExecutor downloadExecutor = new ThreadPoolExecutor(
            5,
            10,
            10,
            TimeUnit.MINUTES, new ArrayBlockingQueue<>(1000));
}
