package com.ff.jvm.hotspot.src.share.vm.oops;

import com.ff.jvm.hotspot.src.share.vm.classfile.KlassReader;
import lombok.Data;

@Data
public class Klass {

    private byte[] magic = new byte[4];
    private byte[] minorVersion = new byte[2];
    private byte[] majorVersion = new byte[2];


    public Klass(KlassReader reader) {
        this.magic = reader.readU4();
        this.minorVersion = reader.readU2();
        this.majorVersion = reader.readU2();
    }
}
