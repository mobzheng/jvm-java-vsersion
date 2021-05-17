package com.ff.jvm.hotspot.src.share.vm.oops;

import cn.hutool.json.JSONUtil;
import com.ff.jvm.hotspot.src.share.vm.classfile.KlassReader;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

    private int accessFlag;
    private int thisClass;
    private int superClass;

    private int interfacesLength;
    private List<InterfaceInfo> interfaceInfos = new ArrayList<>();

    private int fieldsLength;
    private List<FieldInfo> fields = new ArrayList<>();

    private int methodLength;
    private MethodInfo[] methods;

    private int attributeLength;
    private List<AttributeInfo> attributeInfos = new ArrayList<>();


    public Klass(KlassReader reader) {
        this.reader = reader;
        this.magic = reader.readU4();
        this.minorVersion = reader.readU2();
        this.majorVersion = reader.readU2();
        parseConstantPool();

        this.accessFlag = reader.readU2toSimple();

        this.thisClass = reader.readU2toSimple();
        this.superClass = reader.readU2toSimple();

        this.interfacesLength = reader.readU2toSimple();

        parseInterfaceInfo();

        this.fieldsLength = reader.readU2toSimple();

        parseFieldsInfo();

        this.methodLength = reader.readU2toSimple();

        parseMethodInfo();

        this.attributeLength = reader.readU2toSimple();

        parseAttributes();

//        log.info("class文件解析完毕{}", JSONUtil.toJsonStr(this));

    }

    private void parseAttributes() {
        for (int i = 0; i < this.attributeLength; i++) {
            AttributeInfo attributeInfo = new AttributeInfo();
            attributeInfo.setAttrNameIndex(reader.readU2toSimple());

            attributeInfo.setAttrLength(reader.readU2toSimple());

            attributeInfo.initContainer();

            byte[] content = new byte[attributeInfo.getAttrLength()];
            reader.readBytes(content.length, content);
            attributeInfo.getContainer().setCode(content);

            this.attributeInfos.add(attributeInfo);
        }
    }

    private void parseMethodInfo() {
        log.info("解析方法");
        if (this.methodLength > 0) {
            this.methods = new MethodInfo[this.methodLength];
            for (int i = 0; i < this.methodLength; i++) {
                MethodInfo methodInfo = new MethodInfo();
                methodInfo.setBelongKlass(this);
                methodInfo.setAccessFlags(reader.readU2toSimple());

                int nameIndex = reader.readU2toSimple();
                methodInfo.setNameIndex(nameIndex);
                methodInfo.setMethodName(((String) this.constantPoolInfo.getDataMap().get(nameIndex)));
                methodInfo.setDescriptorIndex(reader.readU2toSimple());

                methodInfo.setAttributesCount(reader.readU2toSimple());

                if (methodInfo.getAttributesCount() > 0) {
                    methodInfo.initAttributeContainer();
                    for (int aIndex = 0; aIndex < methodInfo.getAttributesCount(); aIndex++) {
                        CodeAttributeInfo codeAttributeInfo = new CodeAttributeInfo();
                        methodInfo.getAttributes()[aIndex] = codeAttributeInfo;

                        codeAttributeInfo.setAttrNameIndex(reader.readU2toSimple());

                        codeAttributeInfo.setAttrLength(reader.readU4toSimple());

                        codeAttributeInfo.setMaxStack(reader.readU2toSimple());

                        codeAttributeInfo.setMaxLocals(reader.readU2toSimple());

                        codeAttributeInfo.setCodeLength(reader.readU4toSimple());

                        byte[] code = new byte[codeAttributeInfo.getCodeLength()];
                        reader.readBytes(code.length, code);
                        codeAttributeInfo.getCode().setCode(code);

                        codeAttributeInfo.setExceptionTableLength(reader.readU2toSimple());

                        codeAttributeInfo.setAttributesCount(reader.readU2toSimple());

                        for (int j = 0; j < codeAttributeInfo.getAttributesCount(); j++) {
                            // nameAndIndex
                            int index = reader.readU2toSimple();
                            String attrName = (String) constantPoolInfo.getDataMap().get(index);
                            if ("LineNumberTable".equals(attrName)) {
                                parseLineNumberTable(attrName, codeAttributeInfo, index);
                            } else if ("LocalVariableTable".equals(attrName)) {
                                parseLocalVariableTable(attrName, codeAttributeInfo, index);
                            }
                        }
                    }
                }
                this.methods[i] = methodInfo;
            }

        }
    }

    private void parseLocalVariableTable(String attrName, CodeAttributeInfo codeAttributeInfo, int index) {
        log.info("解析本地变量表");
        LocalVariableTable localVariableTable = new LocalVariableTable();
        localVariableTable.setAttrNameIndex(index);

        localVariableTable.setAttrLength(reader.readU4toSimple());

        localVariableTable.setTableLength(reader.readU2toSimple());

        localVariableTable.initTable();

        for (int i = 0; i < localVariableTable.getTableLength(); i++) {
            LocalVariableTable.Item item = localVariableTable.new Item();
            item.setStartPc(reader.readU2toSimple());

            item.setLength(reader.readU2toSimple());

            item.setNameIndex(reader.readU2toSimple());

            item.setDescriptorIndex(reader.readU2toSimple());

            item.setIndex(reader.readU2toSimple());
            localVariableTable.getTable()[i] = item;
        }
        codeAttributeInfo.getAttributes().put(attrName, localVariableTable);

    }

    private void parseLineNumberTable(String attrName, CodeAttributeInfo codeAttributeInfo, int nameIndex) {
        log.info("解析行数表");
        LineNumberTable lineNumberTable = new LineNumberTable();
        lineNumberTable.setAttrNameIndex(nameIndex);

        int attrLength = reader.readU4toSimple();
        lineNumberTable.setAttrLength(attrLength);

        int lineNumberTableLen = reader.readU2toSimple();
        lineNumberTable.setTableLength(lineNumberTableLen);

        lineNumberTable.initTable();
        for (int i = 0; i < lineNumberTable.getTableLength(); i++) {
            LineNumberTable.Item item = lineNumberTable.new Item();
            lineNumberTable.getTable()[i] = item;
            int startPc = reader.readU2toSimple();
            item.setStartPc(startPc);
            int lineNumber = reader.readU2toSimple();
            item.setLineNumber(lineNumber);
        }

        codeAttributeInfo.getAttributes().put(attrName, lineNumberTable);
    }

    private void parseFieldsInfo() {
        log.info("解析属性");
        if (this.fieldsLength > 0) {
            for (int i = 0; i < this.fieldsLength; i++) {
                FieldInfo fieldInfo = new FieldInfo();
                fieldInfo.setAccessFlags(reader.readU2toSimple());
                fieldInfo.setNameIndex(reader.readU2toSimple());
                fieldInfo.setDescriptorIndex(reader.readU2toSimple());
                fieldInfo.setAttributesCount(reader.readU2toSimple());
//                if (fieldInfo.getAttributesCount() > 0) {
//                    fieldInfo.setAttributes(new CodeAttributeInfo[fieldInfo.getAttributesCount()]);
//                    for (int aIndex = 0; aIndex < fieldInfo.getAttributesCount(); aIndex++) {
//                        CodeAttributeInfo codeAttributeInfo = new CodeAttributeInfo();
//
//                        codeAttributeInfo.setAttrNameIndex(reader.readU2toSimple());
//
//                        codeAttributeInfo.setAttrLength(reader.readU4toSimple());
//
//                        codeAttributeInfo.setMaxStack(reader.readU2toSimple());
//
//                        codeAttributeInfo.setMaxLocals(reader.readU2toSimple());
//
//                        codeAttributeInfo.setCodeLength(reader.readU4toSimple());
//
//                        byte[] code = new byte[codeAttributeInfo.getCodeLength()];
//                        reader.readBytes(code.length, code);
//                        codeAttributeInfo.setCode(code);
//
//                        codeAttributeInfo.setExceptionTableLength(reader.readU2toSimple());
//
//                        codeAttributeInfo.setAttributesCount(reader.readU2toSimple());
//
//                        for (int j = 0; j < codeAttributeInfo.getAttributesCount(); j++) {
//                            // nameAndIndex
//                            int index = reader.readU2toSimple();
//                            String attrName = (String) constantPoolInfo.getDataMap().get(index);
//                            if (attrName.equals("LineNumberTable")) {
//                                parseLineNumberTable(attrName, codeAttributeInfo);
//                            } else if (attrName.equals("LocalVariableTable")) {
//                                parseLocalVariableTable(attrName, codeAttributeInfo);
//                            }
//                        }
//                    }
//                }
                fields.add(fieldInfo);

            }


        }
    }

    private void parseInterfaceInfo() {
        log.info("解析接口");
        if (this.interfacesLength > 0) {
            for (int i = 0; i < this.interfacesLength; i++) {
                this.interfaceInfos.add(new InterfaceInfo(reader.readU2toSimple(), String.valueOf(reader.readU2toSimple())));
            }
        }
    }


    public void parseConstantPool() {
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
                    this.constantPoolInfo.getTag()[i] = ConstantPool.JVM_CONSTANT_Utf8;
                    // 获取字符串的长度
                    int strLength = reader.readU2toSimple();
                    // 获取字符串
                    byte[] strBytes = new byte[strLength];
                    reader.readBytes(strLength, strBytes);
                    String str = new String(strBytes, StandardCharsets.UTF_8);
                    constantPoolInfo.getDataMap().put(i, str);
                    log.info("获取到的字符串长度[{}],内容[{}]", strLength, str);
                    break;
                case ConstantPool.JVM_CONSTANT_Integer:
                    this.constantPoolInfo.getTag()[i] = ConstantPool.JVM_CONSTANT_Integer;
                    // int值 高位在前存储的int
                    byte[] bytes = reader.readU4();
                    constantPoolInfo.getDataMap().put(i, bytes);
                    log.info("int[{}]", bytes);
                    break;
                case ConstantPool.JVM_CONSTANT_Class:
                    this.constantPoolInfo.getTag()[i] = ConstantPool.JVM_CONSTANT_Class;
                    int classInfo = reader.readU2toSimple();
                    constantPoolInfo.getDataMap().put(i, classInfo);
                    log.info("class[{}]", classInfo);
                    break;
                case ConstantPool.JVM_CONSTANT_Double:
//                    byte[] doubleInfo = new byte[8];
//                    reader.readBytes(8,doubleInfo);
                    this.constantPoolInfo.getTag()[i] = ConstantPool.JVM_CONSTANT_Double;
                    long doubleInfo = reader.readU8toSimple();
                    constantPoolInfo.getDataMap().put(i, doubleInfo);
                    log.info("double[{}]", doubleInfo);
                    break;
                case ConstantPool.JVM_CONSTANT_Float:
                    this.constantPoolInfo.getTag()[i] = ConstantPool.JVM_CONSTANT_Float;
                    byte[] floatInfo = reader.readU4();
                    constantPoolInfo.getDataMap().put(i, floatInfo);
                    log.info("float[{}]", floatInfo);
                    break;
                case ConstantPool.JVM_CONSTANT_Fieldref:
                    this.constantPoolInfo.getTag()[i] = ConstantPool.JVM_CONSTANT_Fieldref;
                    byte[] fieldInfo = new byte[2];
                    fieldInfo[0] = ((byte) reader.readU2toSimple());
                    fieldInfo[1] = ((byte) reader.readU2toSimple());
                    constantPoolInfo.getDataMap().put(i, fieldInfo);
                    log.info("field[{}]", fieldInfo);
                    break;
                case ConstantPool.JVM_CONSTANT_InterfaceMethodref:
                    this.constantPoolInfo.getTag()[i] = ConstantPool.JVM_CONSTANT_InterfaceMethodref;
                    byte[] interfaceInfo = new byte[2];
                    interfaceInfo[0] = ((byte) reader.readU2toSimple());
                    interfaceInfo[1] = ((byte) reader.readU2toSimple());
                    constantPoolInfo.getDataMap().put(i, interfaceInfo);
                    log.info("interface[{}]", interfaceInfo);
                    break;
                case ConstantPool.JVM_CONSTANT_Long:
//                    byte[] longInfo = new byte[8];
//                    reader.readBytes(8,longInfo);
                    this.constantPoolInfo.getTag()[i] = ConstantPool.JVM_CONSTANT_Long;
                    long constantLong = reader.readU8toSimple();
                    constantPoolInfo.getDataMap().put(i, constantLong);
                    log.info("long[{}]", constantLong);
                    break;
                case ConstantPool.JVM_CONSTANT_NameAndType:
                    this.constantPoolInfo.getTag()[i] = ConstantPool.JVM_CONSTANT_NameAndType;
                    byte[] nameAndType = new byte[2];
                    nameAndType[0] = ((byte) reader.readU2toSimple());
                    nameAndType[1] = ((byte) reader.readU2toSimple());
                    constantPoolInfo.getDataMap().put(i, nameAndType);
                    log.info("JVM_CONSTANT_NameAndType[{}]", nameAndType);
                    break;
                case ConstantPool.JVM_CONSTANT_Methodref:
                    this.constantPoolInfo.getTag()[i] = ConstantPool.JVM_CONSTANT_Methodref;
                    byte[] methodInfo = new byte[2];
                    methodInfo[0] = ((byte) reader.readU2toSimple());
                    methodInfo[1] = ((byte) reader.readU2toSimple());
                    constantPoolInfo.getDataMap().put(i, methodInfo);
                    log.info("method{}", methodInfo);
                    break;
                case ConstantPool.JVM_CONSTANT_String:
                    this.constantPoolInfo.getTag()[i] = ConstantPool.JVM_CONSTANT_String;
                    int strIndex = reader.readU2toSimple();
                    constantPoolInfo.getDataMap().put(i, strIndex);
                    log.info("str", strIndex);
                    break;
                default:
                    new RuntimeException("未适配的index+" + index);

            }
        }

        postParseConstantPool(constantPoolInfo.getDataMap());
        log.info("常量池解析完毕[{}]", JSONUtil.toJsonStr(constantPoolInfo.getDataMap()));
    }


    private void postParseConstantPool(Map<Integer, Object> dataMap) {
        Iterator<Map.Entry<Integer, Object>> iterator = dataMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Object> next = iterator.next();
            Object value = next.getValue();
            if (value instanceof String) {
                continue;
            }
            if (value instanceof Integer) {
                next.setValue(getConstantPoolStringval(value));
            }
            if (value instanceof byte[]) {
                String str = "";
                for (byte b : ((byte[]) value)) {
                    str += getConstantPoolStringval(0xff & b);
                }
                next.setValue(str);
            }
        }
    }


    private String getConstantPoolStringval(Object key) {
        Map<Integer, Object> dataMap = this.constantPoolInfo.getDataMap();
        Object val = dataMap.get(key);

        if (val instanceof byte[]) {
            log.info("递归解析 byte key[{}]===>val[{}]", key, val);
            val = dataMap.get(key);
            if (val instanceof byte[]) {
                StringBuilder strVal = new StringBuilder();
                for (byte b : ((byte[]) val)) {
                    strVal.append(getConstantPoolStringval(0xff & b));
                }
                return strVal.toString();
            }
        }
        if (val instanceof Integer) {
            log.info("递归解析 int key[{}]===>val[{}]", key, val);
            return getConstantPoolStringval(val);
        }

        return ((String) val);
    }

}
