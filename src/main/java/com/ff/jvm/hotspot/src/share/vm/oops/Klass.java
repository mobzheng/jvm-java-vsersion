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
                        log.info("获取到的字符串长度[{}],内容[{}]", strLength, new String(strBytes, "utf-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    break;
                case ConstantPool.JVM_CONSTANT_Integer:
                    // int值 高位在前存储的int
                    byte[] bytes = reader.readU4();
                    log.info("int[{}]", bytes);
                    break;
                case ConstantPool.JVM_CONSTANT_Class:
                    byte[] classInfo = reader.readU2();
                    log.info("class[#{},#{}]", classInfo[0], classInfo[1]);
                    break;
                case ConstantPool.JVM_CONSTANT_Double:
//                    byte[] doubleInfo = new byte[8];
//                    reader.readBytes(8,doubleInfo);

                    log.info("double[{}]", reader.readU8toSimple());
                    break;
                case ConstantPool.JVM_CONSTANT_Float:
                    byte[] floatInfo = reader.readU4();
                    log.info("float[{}]", floatInfo);
                    break;
                case ConstantPool.JVM_CONSTANT_Fieldref:
                    byte[] constantClassInfo = reader.readU2();
                    byte[] constantNameAndTypeInfo = reader.readU2();
                    log.info("field[{}]", constantClassInfo);
                    break;
                case ConstantPool.JVM_CONSTANT_InterfaceMethodref:
                    byte[] interfaceConstantClassInfo = reader.readU2();
                    byte[] interfaceConstantNameAndTypeInfo = reader.readU2();
                    log.info("interface[{}]", interfaceConstantClassInfo);
                    break;
                case ConstantPool.JVM_CONSTANT_Long:
//                    byte[] longInfo = new byte[8];
//                    reader.readBytes(8,longInfo);
                    log.info("long[{}]", reader.readU8toSimple());
                    break;
                case ConstantPool.JVM_CONSTANT_NameAndType:
                    byte[] feildMethodIndex = reader.readU2();
                    byte[] feildMethodDesIndex = reader.readU2();
                    log.info("JVM_CONSTANT_NameAndType[{}]", feildMethodDesIndex);
                    break;
                case ConstantPool.JVM_CONSTANT_Methodref:
                    byte[] bytes1 = reader.readU2();
                    byte[] bytes2 = reader.readU2();
                    log.info("method{}",bytes1);
                    break;
                default:
                    new RuntimeException("未适配的index+" + index);

            }
        }

    }
}
