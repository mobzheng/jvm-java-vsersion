package com.ff.jvm.hotspot.src.share.vm.oops;

import com.ff.jvm.hotspot.src.share.vm.interceptor.StreamCode;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: ziya
 * @Date: 2021/3/6 17:55
 */
@Data
public class CodeAttributeInfo {

    private int attrNameIndex;
    private int attrLength;

    private int maxStack;
    private int maxLocals;

    private int codeLength;

    private StreamCode code = new StreamCode();


    private int exceptionTableLength;

    // 如局部变量表、操作数栈
    private int attributesCount;

    private Map<String, AttributeInfo> attributes = new HashMap<>();

}
