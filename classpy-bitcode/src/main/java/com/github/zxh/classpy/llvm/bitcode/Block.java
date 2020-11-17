package com.github.zxh.classpy.llvm.bitcode;

// https://llvm.org/docs/BitCodeFormat.html#enter-subblock
// [ENTER_SUBBLOCK, blockidvbr8, newabbrevlenvbr4, <align32bits>, blocklen_32]
public class Block extends BitCodePart {

    @Override
    protected void readContent(BitCodeReader reader) {
        long abbrevID = reader.readFixed(2);
        System.out.println(abbrevID); // TODO
        long blockID = reader.readVBR(8);
        System.out.println(blockID); // TODO
    }

}
