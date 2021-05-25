package com.ff.jvm.hotspot.src.share.vm.memory;

import com.ff.jvm.hotspot.src.share.vm.runtime.JavaVFrame;
import com.ff.jvm.hotspot.src.share.vm.utils.BasicType;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@Slf4j
public class MethodInvokeParser {


    private String methodDescription;

    private ParameterDesc[] parameterDescs;

    private String returnType;


    public MethodInvokeParser(String methodDescription) {
        this.methodDescription = methodDescription;
    }

    public void parseMethod() {
        int endIndex = methodDescription.indexOf(BasicType.JVM_SIGNATURE_ENDFUNC);
        parseParameters(methodDescription.substring(0, endIndex + 1));
        parseReturn(methodDescription.substring(endIndex + 1));
    }

    private void parseParameters(String parameters) {
        if (parameters.charAt(0) != BasicType.JVM_SIGNATURE_FUNC) {
            throw new Error("参数格式不支持");
        }
        List<ParameterDesc> parameterList = new ArrayList<>();
        for (int j = 0; j < parameters.length(); j++) {
            char c = parameters.charAt(j);
            switch (c) {
                case BasicType.JVM_SIGNATURE_FUNC:
                    break;
                case BasicType.JVM_SIGNATURE_ARRAY:
                    break;
                case BasicType.JVM_SIGNATURE_BYTE:
                    parameterList.add(ParameterDesc.builder().klass(byte.class).type(BasicType.JVM_SIGNATURE_CLASS).build());
                    break;
                case BasicType.JVM_SIGNATURE_CHAR:
                    parameterList.add(ParameterDesc.builder().klass(char.class).type(BasicType.JVM_SIGNATURE_CLASS).build());
                    break;
                case BasicType.JVM_SIGNATURE_CLASS:
                    StringBuilder sb = new StringBuilder();
                    while ((c = parameters.charAt(++j)) != BasicType.JVM_SIGNATURE_ENDCLASS) {
                        sb.append(c);
                    }
                    // 引用类型
                    String paramClass = sb.toString().replace("/", ".");
                    try {
                        Class aClass = Class.forName(paramClass);
                        parameterList.add(ParameterDesc.builder().klass(aClass).type(BasicType.JVM_SIGNATURE_CLASS).build());
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                case BasicType.JVM_SIGNATURE_ENDCLASS:
                    break;
                case BasicType.JVM_SIGNATURE_ENUM:
                    break;
                case BasicType.JVM_SIGNATURE_FLOAT:
                    parameterList.add(ParameterDesc.builder().klass(float.class).type(BasicType.JVM_SIGNATURE_CLASS).build());
                    break;
                case BasicType.JVM_SIGNATURE_DOUBLE:
                    parameterList.add(ParameterDesc.builder().klass(double.class).type(BasicType.JVM_SIGNATURE_CLASS).build());
                    break;
                case BasicType.JVM_SIGNATURE_INT:
                    parameterList.add(ParameterDesc.builder().klass(int.class).type(BasicType.JVM_SIGNATURE_CLASS).build());
                    break;
                case BasicType.JVM_SIGNATURE_LONG:
                    parameterList.add(ParameterDesc.builder().klass(long.class).type(BasicType.JVM_SIGNATURE_CLASS).build());
                    break;
                case BasicType.JVM_SIGNATURE_SHORT:
                    parameterList.add(ParameterDesc.builder().klass(short.class).type(BasicType.JVM_SIGNATURE_CLASS).build());
                    break;
                case BasicType.JVM_SIGNATURE_VOID:
                    parameterList.add(ParameterDesc.builder().klass(void.class).type(BasicType.JVM_SIGNATURE_CLASS).build());
                    break;
                case BasicType.JVM_SIGNATURE_BOOLEAN:
                    parameterList.add(ParameterDesc.builder().klass(boolean.class).type(BasicType.JVM_SIGNATURE_CLASS).build());
                    break;
                case BasicType.JVM_SIGNATURE_ENDFUNC:
                    break;
                default:
                    log.info("未解析的参数类型：{}", c);
                    throw new Error("未解析的参数类型");
            }
            parameterDescs = parameterList.toArray(new ParameterDesc[parameterList.size()]);
        }
    }


    public void parseParamsVal(JavaVFrame frame) {

        // 使用栈结构，压栈顺序是参数顺序，弹出是反方向的
        for (int i = parameterDescs.length - 1; i >= 0; i--) {
            ParameterDesc parameterDesc = parameterDescs[i];
            Object val = frame.getStack().pop().getObject();
            parameterDesc.setVal(val);
        }
    }

    private void parseReturn(String returnDescription) {
        returnType = returnDescription;
    }

    @Data
    @Builder
    public static class ParameterDesc {
        private Class klass;

        private Object val;

        private int type;

    }
}
