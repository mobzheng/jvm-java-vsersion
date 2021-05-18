package com.ff.jvm.hotspot.src.share.vm.runtime;

import lombok.Data;

/**
 * Created By ziya
 * QQ: 3039277701
 * 2021/3/29
 */
@Data
public class StackValue {

    private int type;

    /**
     * 数据
     */
    private int val;

    private Object object;

    public StackValue(int type, int val) {
        this.type = type;
        this.val = val;
    }

    public StackValue(int type, Object val) {
        this.type = type;
        this.object = val;
    }
}
