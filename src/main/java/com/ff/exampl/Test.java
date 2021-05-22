package com.ff.exampl;

public class Test {

//    public static final String name = "sak";

    public static void main(String[] args) {
        add(10.1, 1201.2);
        System.out.println("hello world");
    }

    public static int add(int a, int b) {
        System.out.println("a+b");
        return a + b;
    }

    public static void add(double a, double b) {
        System.out.println(a + b);
    }

    public int add(int a) {
        return a + 1;
    }


//    public static void add(double a, double b) {
//        double c = a + b;
//    }


}
