package com.ff.jvm.hotspot.src.share.vm.runtime;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Java线程容器
 */
public class Threads {


    private static JavaThread currentThread;


    private static List<JavaThread> threadList = new ArrayList<>();

    public static JavaThread getCurrentThread() {
        return currentThread;
    }

    public static void setCurrentThread(JavaThread currentThread) {
        Threads.currentThread = currentThread;
    }

    public static List<JavaThread> getThreadList() {
        return threadList;
    }

    public static void setThreadList(List<JavaThread> threadList) {
        Threads.threadList = threadList;
    }
}


