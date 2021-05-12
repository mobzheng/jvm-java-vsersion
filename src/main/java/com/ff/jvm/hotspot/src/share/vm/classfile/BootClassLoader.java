package com.ff.jvm.hotspot.src.share.vm.classfile;

import com.ff.exampl.Test;
import com.ff.jvm.hotspot.src.share.vm.oops.Klass;
import lombok.Data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


@Data
public class BootClassLoader {


    public static final String CLASS_PATH = System.getProperty("user.dir")+"/target/classes/";

    public static final String SUF_FIX = ".class";

    public static void loadCLass(String className) {
        String realPath = CLASS_PATH + className.replace(".", "/") + SUF_FIX;
        byte[] bytes = null;
        try {
            bytes = Files.readAllBytes(Paths.get(realPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        KlassReader reader = new KlassReader(bytes);
        Klass klass = new Klass(reader);
        System.out.println(klass);

    }

    public static void main(String[] args) {
        loadCLass(Test.class.getName());
    }
}
