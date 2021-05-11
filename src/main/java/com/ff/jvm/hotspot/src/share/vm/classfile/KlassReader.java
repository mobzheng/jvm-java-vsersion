package com.ff.jvm.hotspot.src.share.vm.classfile;

import lombok.Data;

@Data
public class KlassReader {
    private byte[] bytes;

    public KlassReader(byte[] bytes) {
        this.bytes = bytes;
    }


    public void readBytes(int len, byte[] content) {
        System.arraycopy(bytes, 0, content, 0, len);
        System.arraycopy(bytes, len, bytes, 0, bytes.length - len);
    }


    public byte[] readU1() {
        byte[] u1 = new byte[1];
        readBytes(1, u1);
        return u1;
    }


    public byte[] readU2() {
        byte[] u2 = new byte[2];
        readBytes(2, u2);
        return u2;
    }

    public byte[] readU4() {
        byte[] u4 = new byte[4];
        readBytes(4, u4);
        return u4;
    }
}
