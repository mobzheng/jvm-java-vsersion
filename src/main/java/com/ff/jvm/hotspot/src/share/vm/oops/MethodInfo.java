package com.ff.jvm.hotspot.src.share.vm.oops;

import lombok.Data;

/**
 * @Author: ziya
 * @Date: 2021/3/6 16:57
 */
@Data
public class MethodInfo {

    private Klass belongKlass;

    private int accessFlags;

    private int nameIndex;
    private int descriptorIndex;
    private int attributesCount;

    private CodeAttributeInfo[] attributes;

    private String methodName;

    public void initAttributeContainer() {
        attributes = new CodeAttributeInfo[attributesCount];
    }

    @Override
    public String toString() {
        return "MethodInfo{ "
                + belongKlass.getConstantPoolInfo().getMethodName(nameIndex) + "#"
                + belongKlass.getConstantPoolInfo().getDescriptorName(descriptorIndex)
                + " }";
    }
}
