package com.ff.jvm.hotspot.src.share.vm.oops;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: ziya
 * @Date: 2021/3/6 10:27
 */
@Data
public class ConstantPool {

    public static final int JVM_CONSTANT_Utf8 = 1;
    public static final int JVM_CONSTANT_Unicode = 2;   /* unused */
    public static final int JVM_CONSTANT_Integer = 3;
    public static final int JVM_CONSTANT_Float = 4;
    public static final int JVM_CONSTANT_Long = 5;
    public static final int JVM_CONSTANT_Double = 6;
    public static final int JVM_CONSTANT_Class = 7;
    public static final int JVM_CONSTANT_String = 8;
    public static final int JVM_CONSTANT_Fieldref = 9;
    public static final int JVM_CONSTANT_Methodref = 10;
    public static final int JVM_CONSTANT_InterfaceMethodref = 11;
    public static final int JVM_CONSTANT_NameAndType = 12;
    public static final int JVM_CONSTANT_MethodHandle = 15; /* JSR 292 */
    public static final int JVM_CONSTANT_MethodType = 16;   /* JSR 292 */
    public static final int JVM_CONSTANT_InvokeDynamic = 18;    /* JSR 292 */
    public static final int JVM_CONSTANT_ExternalMax = 18;  /* Last tag found in classfiles */

    private Klass klass;

    private int length;

    // index=>type
    private int[] tag;

    // index=>value
    private Map<Integer, Object> dataMap;

    public void initContainer() {
        tag = new int[length];
        dataMap = new HashMap<>(length);
    }

    public String getClassName(int index) {
        if (0 == index || index > length) {
            return null;
        }

        /**
         * 解释：
         *  1、参数index对应的是JVM_CONSTANT_Class
         *  2、JVM_CONSTANT_Class中的信息才是类的全限定名在常量池中的索引
         */
        return (String) getDataMap().get(getDataMap().get(index));
    }

    public String getSuperClassName(int index) {
        if (0 == index || index > length) {
            return null;
        }

        /**
         * 解释：
         *  1、参数index对应的是JVM_CONSTANT_Class
         *  2、JVM_CONSTANT_Class中的信息才是类的全限定名在常量池中的索引
         */
        return (String) getDataMap().get(index);
    }

    public String getMethodName(int index) {
        if (0 == index || index > length) {
            return null;
        }

        return (String) getDataMap().get(index);
    }

    public String getDescriptorName(int index) {
        if (0 == index || index > length) {
            return null;
        }

        return (String) getDataMap().get(index);
    }

//    public String getClassNameByFieldInfo(int index) {
//        // 获取Class信息在常量池中的index
//        int data = (int) getDataMap().get(index);
//        int classIndex = data >> 16;
//
//        // 获取Class全限定名的index
//        int classNameIndex = (int) getDataMap().get(classIndex);
//
//        return (String) getDataMap().get(classNameIndex);
//    }

    public String getClassNameByFieldInfo(int index) {

        byte[] bytes = (byte[]) getDataMap().get(index);
        int i = (int) getDataMap().get(Byte.toUnsignedInt(bytes[0]));

        return ((String) getDataMap().get(i));
    }

//    public String getFieldName(int index) {
//        // 获取NameAndType在常量池中的index
//        int data = (int) getDataMap().get(index);
//        int i = data & 0xFF;
//
//        int nameAndType = (int) getDataMap().get(i);
//        i = nameAndType >> 16;
//
//        return (String) getDataMap().get(i);
//    }

    public String getFieldName(int index)
    {
        // 因为本常量池中存储的是原生byte[] 并没有转换到int所以不能使用位运算进行取值
        byte[] bytes = (byte[]) getDataMap().get(index);
        byte[] fieldBytes = ((byte[]) getDataMap().get(Byte.toUnsignedInt(bytes[1])));
        return (String)getDataMap().get(Byte.toUnsignedInt(fieldBytes[0]));
    }

//    public String getMethodNameByMethodInfo(int operand) {
//        // 获取Methodinfo在常量池中的index
//        int i = (int) getDataMap().get(operand);
//        int nameAndTypeIndex = i & 0xff;
//
//        // 获取NameAndType的值
//        int data = (int) getDataMap().get(nameAndTypeIndex);
//        i = data >> 16;
//
//        return (String) getDataMap().get(i);
//    }

    public String getMethodNameByMethodInfo(int index){
        byte[] bytes = (byte[]) getDataMap().get(index);
        return ((String) getDataMap().get(Byte.toUnsignedInt(bytes[0])));

    }

//    public String getDescriptorNameByMethodInfo(int operand) {
//        // 获取Methodinfo在常量池中的index
//        int i = (int) getDataMap().get(operand);
//        int nameAndTypeIndex = i & 0xff;
//
//        // 获取NameAndType的值
//        int data = (int) getDataMap().get(nameAndTypeIndex);
//        i = data & 0xFF;
//
//        return (String) getDataMap().get(i);
//    }


    public String getDescriptorNameByMethodInfo(int index) {
        byte[] i = (byte[]) getDataMap().get(index);
        byte[] bytes = (byte[]) getDataMap().get(Byte.toUnsignedInt(i[1]));
        return ((String) getDataMap().get(Byte.toUnsignedInt(bytes[1])));
    }
}
