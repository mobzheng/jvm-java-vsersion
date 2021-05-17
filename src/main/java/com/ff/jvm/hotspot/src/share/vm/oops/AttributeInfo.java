package com.ff.jvm.hotspot.src.share.vm.oops;

import com.ff.jvm.hotspot.src.share.vm.interceptor.StreamCode;
import lombok.Data;

/**
 * @Author: ziya
 * @Date: 2021/3/6 17:55
 */
@Data
public class AttributeInfo {

    private int attrNameIndex;
    private int attrLength;

    // 用于存储klass的attribute
    private StreamCode container = new StreamCode();

    public void initContainer() {
        container.setCode(new byte[attrLength]);
        container.setLength(attrLength);
    }
}
