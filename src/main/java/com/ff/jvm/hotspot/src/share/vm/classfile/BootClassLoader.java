package com.ff.jvm.hotspot.src.share.vm.classfile;

import com.ff.exampl.Test;
import com.ff.jvm.hotspot.src.share.vm.interceptor.Bytecodes;
import com.ff.jvm.hotspot.src.share.vm.interceptor.StreamCode;
import com.ff.jvm.hotspot.src.share.vm.memory.MethodInvokeParser;
import com.ff.jvm.hotspot.src.share.vm.oops.CodeAttributeInfo;
import com.ff.jvm.hotspot.src.share.vm.oops.ConstantPool;
import com.ff.jvm.hotspot.src.share.vm.oops.Klass;
import com.ff.jvm.hotspot.src.share.vm.oops.MethodInfo;
import com.ff.jvm.hotspot.src.share.vm.runtime.JavaThread;
import com.ff.jvm.hotspot.src.share.vm.runtime.JavaVFrame;
import com.ff.jvm.hotspot.src.share.vm.runtime.StackValue;
import com.ff.jvm.hotspot.src.share.vm.utils.BasicType;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

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
                JavaVFrame frame = (JavaVFrame) javaThread.getStack().peek();
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
                                    frame.getStack().push(new StackValue(BasicType.T_OBJECT, content));
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
                            c = code.readU2Simple();
                            String classNameByFieldInfo1 = constantPoolInfo.getClassNameByFieldInfo(c);
                            String fieldName1 = constantPoolInfo.getFieldName(c);
                            try {
                                Class<?> targetClass = Class.forName(classNameByFieldInfo1.replace("/", "."));
                                Field field = targetClass.getDeclaredField(fieldName1);
                                Object val = field.get(null);
                                frame.getStack().push(new StackValue(BasicType.T_OBJECT, val));
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (NoSuchFieldException e) {
                                e.printStackTrace();
                            }


                            break;
                        case Bytecodes.RETURN:
                            log.info("RETURN");
                            break;
                        case Bytecodes.GETSTATIC:
                            log.info("GETSTATIC");
                            c = code.readU2Simple();
                            String classNameByFieldInfo = constantPoolInfo.getClassNameByFieldInfo(c);
                            String fieldName = constantPoolInfo.getFieldName(c);
                            if (classNameByFieldInfo.startsWith("java")) {
                                try {
                                    Class<?> targetClass = Class.forName(classNameByFieldInfo.replace("/", "."));
                                    Field field = targetClass.getDeclaredField(fieldName);
                                    Object val = field.get(null);
                                    frame.getStack().push(new StackValue(BasicType.T_OBJECT, val));
                                } catch (ClassNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                } catch (NoSuchFieldException e) {
                                    e.printStackTrace();
                                }


                            }
                            break;
                        case Bytecodes.INVOKEVIRTUAL:
                            log.info("INVOKEVIRTUAL");
                            c = code.readU2Simple();
//                            String classNameByFieldInfo = (String) constantPoolInfo.getClassNameByFieldInfo(c);
                            String infoFieldName = (String) constantPoolInfo.getFieldName(c);
                            String descriptorNameByMethodInfo = (String) constantPoolInfo.getDescriptorNameByMethodInfo(c);
//                            执行方法
                            MethodInvokeParser methodInvokeParser = new MethodInvokeParser(descriptorNameByMethodInfo);
                            methodInvokeParser.parseMethod();
                            methodInvokeParser.parseParamsVal(frame);
                            Object obj = frame.getStack().pop().getObject();
                            Method invokeMethod = null;
                            try {
                                invokeMethod = obj.getClass().getMethod(infoFieldName, Arrays.stream(methodInvokeParser.getParameterDescs()).map(MethodInvokeParser.ParameterDesc::getKlass).toArray(Class[]::new));
                                invokeMethod.invoke(obj, Arrays.stream(methodInvokeParser.getParameterDescs()).map(MethodInvokeParser.ParameterDesc::getVal).toArray(Object[]::new));
                            } catch (NoSuchMethodException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                            break;
                    }

                }
            }
        }
    }

}
