package com.ff.jvm.hotspot.src.share.vm.runtime;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Stack;

/**
 * Created By ziya
 * QQ: 3039277701
 * 2021/3/29
 */
@Data
@Slf4j
public class StackValueCollection {

    private Logger logger = LoggerFactory.getLogger(StackValueCollection.class);

    /*************************************
     * 模拟操作数栈
     */
    private Stack<StackValue> container = new Stack<>();

    public StackValueCollection() {
    }

    public void push(StackValue value) {
        log.info("push[{}}=====>size[{}]",  value,container.size());
        getContainer().push(value);
    }

    public StackValue pop() {
        StackValue val = getContainer().pop();
        log.info("pop[{}}=====>size[{}]",  val,container.size());
        return val;
    }

    public StackValue peek() {
        return getContainer().peek();
    }

    /****************************************
     *  模拟局部变量表
     */
    private int maxLocals;
    private int index;
    private StackValue[] locals;

    public StackValueCollection(int size) {
        maxLocals = size;

        locals = new StackValue[size];
    }

    public void add(int index, StackValue value) {
        getLocals()[index] = value;
    }

    public StackValue get(int index) {
        return getLocals()[index];
    }

}
