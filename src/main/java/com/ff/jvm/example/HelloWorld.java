package com.ff.jvm.example;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 *<init>()V
 * main([Ljava/lang/String;)V
 */
public class HelloWorld {
    public static final int age = 10;
    public static final String name = "1";
    public static final int age2 = 1;

    public static void main(String[] args) throws IOException,IllegalAccessError,RuntimeException {
        Files.readAllBytes(Paths.get(""));

    }

}
