package com.github.zxh.classpy.llvm.bitcode;

// https://llvm.org/docs/BitCodeFormat.html
public class BitCodeFile extends BitCodePart {

    @Override
    protected void readContent(BitCodeReader reader) {
        Wrapper wrapper = new Wrapper();
        wrapper.read(reader);
        add("Wrapper", wrapper);

        U32 magic = new U32();
        magic.read(reader);
        add("LLVM IR Magic", magic);

        while (reader.remaining() > 0) {
            Block block = new Block();
            add("Block", block);
            block.read(reader);
        }
    }

}
