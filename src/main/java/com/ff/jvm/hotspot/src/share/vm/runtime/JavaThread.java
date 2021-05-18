package com.ff.jvm.hotspot.src.share.vm.runtime;

import lombok.Data;

import java.util.Stack;

/**
 * @Author: ziya
 * @Date: 2021/3/26 14:18
 */
@Data
public class JavaThread extends Thread {

    private Stack<VFrame> stack = new Stack<>();

}
