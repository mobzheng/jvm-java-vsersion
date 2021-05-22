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
        byte[] temp = new byte[this.bytes.length - len];
        System.arraycopy(this.bytes, 0, content, 0, len);
        System.arraycopy(this.bytes, len, temp, 0, this.bytes.length - len);
        bytes = temp;
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
        return (high & 0xff << 8) | (low & 0xff);
    }

    public static int byteArrayToInt(byte[] bytes) {
        int value = 0;

        for (int i = 0; i < 4; i++) {
            int shift = (3 - i) * 8;
            value += (bytes[i] & 0xFF) << shift;
        }

        return value;
    }


    public int readU4toSimple() {
        byte[] content = readU4();
        int result = 0;
        for (int i = 0; i < content.length; i++) {
            result |= (content[i] & 0xff) << (8 * (content.length - 1 - i));
        }
        return result;
    }

    public double readU8toDouble() {
        byte[] content = new byte[8];
        readBytes(8, content);
        ByteBuffer buffer = ByteBuffer.wrap(content, 0, 8);

        return buffer.getDouble();
    }

    public long readU8toLong() {
        byte[] content = new byte[8];
        readBytes(8, content);
        ByteBuffer buffer = ByteBuffer.wrap(content, 0, 8);

        return buffer.getLong();
    }


    public byte[] readU4() {
        byte[] u4 = new byte[4];
        readBytes(4, u4);
        return u4;
    }


}
