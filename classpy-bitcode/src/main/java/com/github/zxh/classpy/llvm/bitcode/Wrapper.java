package com.github.zxh.classpy.llvm.bitcode;

// https://llvm.org/docs/BitCodeFormat.html#bitcode-wrapper-format
public class Wrapper extends BitCodePart {

    {
        add("Magic",   new U32());
        add("Version", new U32());
        add("Offset",  new U32());
        add("Size",    new U32());
        add("CPUType", new U32());
    }

}
