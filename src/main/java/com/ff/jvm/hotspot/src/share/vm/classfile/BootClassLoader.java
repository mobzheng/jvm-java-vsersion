package com.ff.jvm.hotspot.src.share.vm.classfile;

import com.ff.exampl.Test;
import com.ff.jvm.hotspot.src.share.vm.interceptor.Bytecodes;
import com.ff.jvm.hotspot.src.share.vm.interceptor.StreamCode;
import com.ff.jvm.hotspot.src.share.vm.oops.CodeAttributeInfo;
import com.ff.jvm.hotspot.src.share.vm.oops.ConstantPool;
import com.ff.jvm.hotspot.src.share.vm.oops.Klass;
import com.ff.jvm.hotspot.src.share.vm.oops.MethodInfo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
@Data
public class BootClassLoader {


    public static final String CLASS_PATH = System.getProperty("user.dir") + "/target/classes/";

    public static final String SUF_FIX = ".class";

    public static Klass loadCLass(String className) {
        String realPath = CLASS_PATH + className.replace(".", "/") + SUF_FIX;
        byte[] bytes = null;
        try {
            bytes = Files.readAllBytes(Paths.get(realPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        KlassReader reader = new KlassReader(bytes);
        return new Klass(reader);

    }

    public static Klass loadMainClass(String name) {
        return loadCLass(name);
    }

    public static void main(String[] args) {
        Klass klass = loadMainClass(Test.class.getName());
        for (MethodInfo method : klass.getMethods()) {
            String methodName = method.getMethodName();
            log.info("找到方法[{}]", methodName);
            for (CodeAttributeInfo attributeInfo : method.getAttributes()) {
                StreamCode code = attributeInfo.getCode();
                Klass belongKlass = method.getBelongKlass();
                ConstantPool constantPoolInfo = belongKlass.getConstantPoolInfo();
                String name = (String) constantPoolInfo.getDataMap().get(attributeInfo.getAttrNameIndex());
                log.info("开始解析{}==>{}", name, code.getCode());
                while (code.hasNext()) {
                    int c = code.redU1();
                    switch (c) {
                        case Bytecodes.LDC:
                            log.info("执行LDC指令");
                            // 取出操作书
                            int operno = code.redU1();
                            int tag = constantPoolInfo.getTag()[operno];
                            switch (tag) {
                                case ConstantPool.JVM_CONSTANT_String:
                                    String content = (String) constantPoolInfo.getDataMap().get(operno);

                                    String methodMethodName = method.getMethodName();
                                    String descriptor = (String) constantPoolInfo.getDataMap().get(method.getDescriptorIndex());

                                    log.info("JVM_CONSTANT_String");
                                    break;
                                case ConstantPool.JVM_CONSTANT_Double:
                                    log.info("JVM_CONSTANT_Double");
                                    break;
                                default:
                                    throw new RuntimeException("暂不支持类型");
                            }
                            break;
                        case Bytecodes.ALOAD_0:
                            log.info("ALOAD_0");
                            break;
                        case Bytecodes.INVOKESPECIAL:
                            log.info("INVOKESPECIAL");
                            break;
                        case Bytecodes.RETURN:
                            log.info("RETURN");
                            break;
                        case Bytecodes.GETSTATIC:
                            log.info("GETSTATIC");
                            break;
                        case Bytecodes.INVOKEVIRTUAL:
                            log.info("INVOKEVIRTUAL");
                            break;

                    }

                }
            }
        }
    }

}
