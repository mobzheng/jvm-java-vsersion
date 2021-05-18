package com.ff.jvm.hotspot.src.share.vm.memory;

import com.ff.jvm.hotspot.src.share.vm.runtime.JavaVFrame;
import com.ff.jvm.hotspot.src.share.vm.utils.BasicType;
import lombok.Builder;
import lombok.Data;
@Data
public class MethodInvokeParser {


    private String methodDescription;

    private ParameterDesc[] parameterDescs;


    public MethodInvokeParser(String methodDescription) {
        this.methodDescription = methodDescription;
    }

    public void parseMethod() {
        methodDescription = methodDescription.substring(1);
        String[] des = methodDescription.split("\\)");
        parseParameters(des[0]);
        parseReturn(des[1]);
    }

    private void parseParameters(String parameters) {
        String[] split = parameters.split(";");
        this.parameterDescs = new ParameterDesc[split.length];
        for (int i = 0; i < split.length; i++) {
            String str = split[i];

            char preFix = str.charAt(0);

            switch (preFix) {
                case BasicType.JVM_SIGNATURE_CLASS:
                    // 引用类型
                    String paramClass = str.substring(1).replace("/", ".");
                    try {
                        Class aClass = Class.forName(paramClass);
                        this.parameterDescs[i] = ParameterDesc.builder().klass(aClass).type(BasicType.JVM_SIGNATURE_CLASS).build();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
            }

        }
    }


    public void parseParamsVal(JavaVFrame frame) {
        for (int i = 0; i < parameterDescs.length; i++) {
            ParameterDesc parameterDesc = parameterDescs[i];
            Object val = frame.getStack().pop().getObject();
            parameterDesc.setVal(val);
        }
    }

    private void parseReturn(String returnDescription) {

    }

    @Data
    @Builder
    public static class ParameterDesc {
        private Class klass;

        private Object val;

        private int type;

    }
}
