package com.github.zxh.classpy.llvm.bitcode;

import com.github.zxh.classpy.common.FixedInt;

public class U32 extends FixedInt<BitCodeReader> {

    public U32() {
        super(IntType.U32, IntDesc.Hex);
    }

}
