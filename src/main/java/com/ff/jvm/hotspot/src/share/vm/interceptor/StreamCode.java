package com.ff.jvm.hotspot.src.share.vm.interceptor;

import lombok.Data;

@Data
public class StreamCode {
    private byte[] code;
    private int length;

    public boolean hasNext() {
        return code.length > 0;
    }

    public int redU1() {
        if (!hasNext()) {
            throw new Error("Array out of bound");
        }
        byte[] bytes = new byte[1];
        readBytes(1, bytes);
        return Byte.toUnsignedInt(bytes[0]);
    }


    public void readBytes(int len, byte[] bytes) {
        byte[] temp = new byte[this.code.length - len];
        System.arraycopy(this.code, 0, bytes, 0, len);
        System.arraycopy(this.code, len, temp, 0, code.length - len);
        code = temp;
    }

    public int readU2Simple() {
        byte[] bytes = new byte[2];
        readBytes(2, bytes);
        return (bytes[0] & 0xff) << 16 | bytes[1] & 0xff;
    }

    /**
     * 针对sipush
     * @return
     */
    public int readU2toShortSimple(){
        byte[] bytes = new byte[2];
        readBytes(2, bytes);
        return (bytes[0] & 0xff) << 8 | bytes[1] & 0xff;
    }
}
