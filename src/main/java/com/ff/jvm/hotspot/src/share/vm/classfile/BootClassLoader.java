package com.ff.jvm.hotspot.src.share.vm.classfile;

import com.ff.exampl.Test;
import com.ff.jvm.hotspot.src.share.vm.interceptor.Bytecodes;
import com.ff.jvm.hotspot.src.share.vm.interceptor.StreamCode;
import com.ff.jvm.hotspot.src.share.vm.oops.CodeAttributeInfo;
import com.ff.jvm.hotspot.src.share.vm.oops.ConstantPool;
import com.ff.jvm.hotspot.src.share.vm.oops.Klass;
import com.ff.jvm.hotspot.src.share.vm.oops.MethodInfo;
import com.ff.jvm.hotspot.src.share.vm.runtime.JavaThread;
import com.ff.jvm.hotspot.src.share.vm.runtime.JavaVFrame;
import com.ff.jvm.hotspot.src.share.vm.runtime.VFrame;
import com.sun.xml.internal.ws.api.ha.StickyFeature;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import sun.jvm.hotspot.runtime.Bytes;

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
        JavaThread javaThread = new JavaThread();
        for (MethodInfo method : klass.getMethods()) {
            String methodName = method.getMethodName();
            log.info("找到方法[{}]", methodName);
            for (CodeAttributeInfo attributeInfo : method.getAttributes()) {
                JavaVFrame javaVFrame = new JavaVFrame(attributeInfo.getMaxLocals(), method);
                javaThread.getStack().push(javaVFrame);
                log.info("压栈");
                // 获取栈帧
                JavaVFrame frame = (JavaVFrame)javaThread.getStack().peek();
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
                                    int strIndex = (int) constantPoolInfo.getDataMap().get(operno);
                                    String content = (String) constantPoolInfo.getDataMap().get(strIndex);
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
                            int methodIndex = code.readU2Simple();
                            byte[] methodInfo = (byte[]) constantPoolInfo.getDataMap().get(methodIndex);
                            int classIndex = Byte.toUnsignedInt(methodInfo[0]);
                            int signInfoIndex = Byte.toUnsignedInt(methodInfo[1]);
                            byte[] signInfo = (byte[]) constantPoolInfo.getDataMap().get(signInfoIndex);
                            int filedIndex = Byte.toUnsignedInt(signInfo[0]);
                            int returnTypeIndex = Byte.toUnsignedInt(signInfo[1]);

                            break;
                        case Bytecodes.RETURN:
                            log.info("RETURN");
                            break;
                        case Bytecodes.GETSTATIC:
                            log.info("GETSTATIC");
                            int i = code.readU2Simple();
                            log.info("分析参数");
                            break;
                        case Bytecodes.INVOKEVIRTUAL:
                            log.info("INVOKEVIRTUAL");
//                            int invokevirtualmethodIndex = code.readU2Simple();
//                            byte[] invokevirtualmethodInfo = (byte[]) constantPoolInfo.getDataMap().get(invokevirtualmethodIndex);
//                            int invokevirtualclassIndex = Byte.toUnsignedInt(invokevirtualmethodInfo[0]);
//                            int invokevirtualsignInfoIndex = Byte.toUnsignedInt(invokevirtualmethodInfo[1]);
//                            byte[] invokevirtualsignInfo = (byte[]) constantPoolInfo.getDataMap().get(invokevirtualsignInfoIndex);
//                            int invokevirtualfiledIndex = Byte.toUnsignedInt(invokevirtualsignInfo[0]);
//                            int invokevirtualreturnTypeIndex = Byte.toUnsignedInt(invokevirtualsignInfo[1]);
//
//
//                            String targetClass = ((String) constantPoolInfo.getDataMap().get(constantPoolInfo.getDataMap().get(invokevirtualclassIndex)));
//                            String targetMethodName = (String)constantPoolInfo.getDataMap().get(invokevirtualfiledIndex);
//                            String targetMethodDescript = ((String) constantPoolInfo.getDataMap().get(invokevirtualreturnTypeIndex));

                             c = code.readU2Simple();
                            String classNameByFieldInfo = (String) constantPoolInfo.getClassNameByFieldInfo(c);
                            String fieldName = (String) constantPoolInfo.getFieldName(c);
                            String descriptorNameByMethodInfo = (String) constantPoolInfo.getDescriptorNameByMethodInfo(c);
                            log.info("");
//                            log.info("执行{}类方法{},{}", targetClass, targetMethodName, targetMethodDescript);
//                            // 执行方法
//                            if (targetClass.startsWith("java")) {
//                                // jdk的方法 ，反射执行
//                                targetClass = targetClass.replace(".", "/");
//                                try {
//                                    Class<?> target = Class.forName(targetClass);
//
////                                    target.getDeclaredField()
//                                } catch (ClassNotFoundException e) {
//                                }
//                            }
                            break;
                    }

                }
            }
        }
    }

}
