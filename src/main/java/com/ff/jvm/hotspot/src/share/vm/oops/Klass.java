package com.ff.jvm.hotspot.src.share.vm.oops;

import com.ff.jvm.hotspot.src.share.vm.classfile.KlassReader;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;

@Data
@Slf4j
public class Klass {

    private byte[] magic = new byte[4];
    private byte[] minorVersion = new byte[2];
    private byte[] majorVersion = new byte[2];
    /**
     * 常量池
     */
    private ConstantPool constantPoolInfo;


    private KlassReader reader;

    public Klass(KlassReader reader) {
        this.reader = reader;
        this.magic = reader.readU4();
        this.minorVersion = reader.readU2();
        this.majorVersion = reader.readU2();
        readConstantPool();
    }


    public void readConstantPool() {
//        byte[] U1 = new byte[1];
//        byte[] U2 = new byte[2];
//        byte[] U4 = new byte[4];


        this.constantPoolInfo = new ConstantPool();
        // 获取常量池大小 u2
        // 常量池大小
        constantPoolInfo.setLength(reader.readU2toSimple());
        constantPoolInfo.initContainer();
        byte[] content = new byte[this.constantPoolInfo.getLength()];
        for (int i = 1; i < content.length; i++) {
            byte index = reader.readU1()[0];
            log.info("\tindex\t#{}", i);
            switch (index) {
                case ConstantPool.JVM_CONSTANT_Utf8:
                    try {
                        // 获取字符串的长度
                        int strLength = reader.readU2toSimple();
                        // 获取字符串
                        byte[] strBytes = new byte[strLength];
                        reader.readBytes(strLength, strBytes);
                        String str = new String(strBytes, "utf-8");
                        constantPoolInfo.getDataMap().put(i, str);
                        log.info("获取到的字符串长度[{}],内容[{}]", strLength, str);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    break;
                case ConstantPool.JVM_CONSTANT_Integer:
                    // int值 高位在前存储的int
                    byte[] bytes = reader.readU4();
                    constantPoolInfo.getDataMap().put(i, bytes);
                    log.info("int[{}]", bytes);
                    break;
                case ConstantPool.JVM_CONSTANT_Class:
                    int classInfo = reader.readU2toSimple();
                    constantPoolInfo.getDataMap().put(i, classInfo);
                    log.info("class[#{},#{}]", classInfo);
                    break;
                case ConstantPool.JVM_CONSTANT_Double:
//                    byte[] doubleInfo = new byte[8];
//                    reader.readBytes(8,doubleInfo);

                    long doubleInfo = reader.readU8toSimple();
                    constantPoolInfo.getDataMap().put(i, doubleInfo);
                    log.info("double[{}]", doubleInfo);
                    break;
                case ConstantPool.JVM_CONSTANT_Float:
                    byte[] floatInfo = reader.readU4();
                    constantPoolInfo.getDataMap().put(i, floatInfo);
                    log.info("float[{}]", floatInfo);
                    break;
                case ConstantPool.JVM_CONSTANT_Fieldref:
                    byte[] fieldInfo = new byte[2];
                    fieldInfo[0] = ((byte) reader.readU2toSimple());
                    fieldInfo[1] = ((byte) reader.readU2toSimple());
                    constantPoolInfo.getDataMap().put(i, fieldInfo);
                    log.info("field[{}]", fieldInfo);
                    break;
                case ConstantPool.JVM_CONSTANT_InterfaceMethodref:
                    byte[] interfaceInfo = new byte[2];
                    interfaceInfo[0] = ((byte) reader.readU2toSimple());
                    interfaceInfo[1] = ((byte) reader.readU2toSimple());
                    constantPoolInfo.getDataMap().put(i, interfaceInfo);
                    log.info("interface[{}]", interfaceInfo);
                    break;
                case ConstantPool.JVM_CONSTANT_Long:
//                    byte[] longInfo = new byte[8];
//                    reader.readBytes(8,longInfo);

                    long constantLong = reader.readU8toSimple();
                    constantPoolInfo.getDataMap().put(i, constantLong);
                    log.info("long[{}]", constantLong);
                    break;
                case ConstantPool.JVM_CONSTANT_NameAndType:
                    byte[] nameAndType = new byte[2];
                    nameAndType[0] = ((byte) reader.readU2toSimple());
                    nameAndType[1] = ((byte) reader.readU2toSimple());
                    constantPoolInfo.getDataMap().put(i, nameAndType);
                    log.info("JVM_CONSTANT_NameAndType[{}]", nameAndType);
                    break;
                case ConstantPool.JVM_CONSTANT_Methodref:
                    byte[] methodInfo = new byte[2];
                    methodInfo[0] = ((byte) reader.readU2toSimple());
                    methodInfo[1] = ((byte) reader.readU2toSimple());
                    constantPoolInfo.getDataMap().put(i, methodInfo);
                    log.info("method{}",methodInfo);
                    break;
                case ConstantPool.JVM_CONSTANT_String:
                    int strIndex = reader.readU2toSimple();
                    constantPoolInfo.getDataMap().put(i, strIndex);
                    log.info("str", strIndex);
                    break;
                default:
                    new RuntimeException("未适配的index+" + index);

            }
        }

    }
}
