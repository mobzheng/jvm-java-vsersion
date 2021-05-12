package com.ff.jvm.hotspot.src.share.vm.classfile;

import lombok.Data;

import java.io.BufferedReader;
import java.nio.ByteBuffer;

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

    public int readU2toSimple() {
        byte[] bytes = readU2();
        int high = bytes[0];
        int low = bytes[1];
        return (high << 8 & 0xffff) | (low & 0xff);
    }

    public int readU4toSimple() {
        byte[] content = readU4();
        int result = 0;
        for (int i = 0; i < content.length; i++) {
            result |= (content[i] & 0xff) << 8 * (content.length - 1 - i);
        }
        return result;
    }

    public long readU8toSimple() {
        byte[] content = new byte[8];
        readBytes(8, content);
        int result = 0;
        for (int i = 0; i < content.length; i++) {
            result |= (content[i] & 0xff) << 8 * (content.length - 1 - i);
        }
        return result;
    }


    public byte[] readU4() {
        byte[] u4 = new byte[4];
        readBytes(4, u4);
        return u4;
    }


}
