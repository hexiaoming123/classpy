package com.github.zxh.classpy.llvm.bitcode;

import com.github.zxh.classpy.common.ParseException;
import com.github.zxh.classpy.llvm.bitcode.enums.BlockIDs;
import com.github.zxh.classpy.llvm.bitcode.enums.BuiltinAbbrevIDs;
import com.github.zxh.classpy.llvm.bitcode.types.U32Dec;

// https://llvm.org/docs/BitCodeFormat.html#enter-subblock
// [ENTER_SUBBLOCK, blockidvbr8, newabbrevlenvbr4, <align32bits>, blocklen_32]
public class Block extends BitCodePart {

    @Override
    protected void readContent(BitCodeReader reader) {
        long abbrevID = reader.readFixed(2);
        if (abbrevID != BuiltinAbbrevIDs.ENTER_SUBBLOCK) {
            throw new ParseException("invalid abbrevID, expected: 1, got: " + abbrevID);
        }
        long blockID = reader.readVBR(8);
        long newAbbrevLen = reader.readVBR(4);
        reader.align32bits();

        U32Dec blockLen = new U32Dec();
        add("blockLen", blockLen);
        blockLen.read(reader);

        setName(getBlockName(blockID));
        setDesc(String.format("id=%d, newAbbrevLen=%d, blockLen=%d",
                blockID, newAbbrevLen, blockLen.getValue()));
        readBlock1(reader, (int) newAbbrevLen, blockLen.getIntValue());
    }

    // TODO
    private void readBlock1(BitCodeReader reader, int abbrevLen, int blockLen) {
        int p1 = reader.getPosition();
        reader.setLimit(p1 + blockLen * 4);
        readBlock2(reader, abbrevLen);
        int p2 = reader.getPosition();
        reader.align32bits();
        reader.skipBytes(blockLen * 4 - (p2 - p1));
    }

    // TODO
    private void readBlock2(BitCodeReader reader, int abbrevLen) {
        while (reader.remaining() > 0) {
            long abbrevID = reader.readFixed(abbrevLen);
            if (abbrevID == BuiltinAbbrevIDs.ENTER_SUBBLOCK) {
                throw new ParseException("TODO: ENTER_SUBBLOCK");
            } else if (abbrevID == BuiltinAbbrevIDs.DEFINE_ABBREV) {
//                throw new ParseException("TODO: DEFINE_ABBREV");
                long numAbbrevOps = reader.readVBR(5);
                System.out.println("numAbbrevOp=" + numAbbrevOps);
                for (int i = 0; i < numAbbrevOps; i++) {
                    long b = reader.readFixed(1);
                    System.out.println("b=" + b);
                    if (b == 1) {
                        long litVal = reader.readVBR(8);
                        System.out.println("litVal=" + litVal);
                    } else {
                        long code = reader.readVBR(3);
                        if (code == 3) {
                            // array
                            long arrLen = reader.readVBR(6);
                            System.out.println("arr len = " + arrLen);
                            for (int j = 0; j < arrLen; j++) {
                                System.out.println(reader.readFixed(6));
                            }
                        } else {
                            throw new ParseException("TODO: code=" + code);
                        }
                    }
                }
            } else if (abbrevID == BuiltinAbbrevIDs.UNABBREV_RECORD) {
                long code = reader.readVBR(6);
                long numOps = reader.readVBR(6);
                System.out.printf("code=%d, numOps=%d\n", code, numOps);
                for (int i = 0; i < numOps; i++) {
                    long op = reader.readVBR(6);
                    System.out.printf("op%d=%d\n", i, op);
                }
            } else {
                throw new ParseException("TODO: abbrevID=" + abbrevID);
            }
        }
    }

    private static String getBlockName(long blockID) {
        for (var knownBlockID : BlockIDs.values()) {
            if (knownBlockID.getValue() == blockID) {
                return knownBlockID.name().replace("_ID", "");
            }
        }
        return "UNKNOWN_BLOCK";
    }

}
