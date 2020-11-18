package com.github.zxh.classpy.llvm.bitcode.blocks;

import com.github.zxh.classpy.llvm.bitcode.BitCodeReader;
import com.github.zxh.classpy.llvm.bitcode.Block;

public class ModuleBlock {

    public static void read(Block block, BitCodeReader reader, int abbrevLen) {
        long abbrevID = reader.readFixed(abbrevLen);
        System.out.println("....." + abbrevID);
        // TODO
    }

}
