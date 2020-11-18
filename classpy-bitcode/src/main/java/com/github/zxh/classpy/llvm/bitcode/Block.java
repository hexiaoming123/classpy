package com.github.zxh.classpy.llvm.bitcode;

import com.github.zxh.classpy.common.ParseException;
import com.github.zxh.classpy.llvm.bitcode.blocks.ModuleBlock;
import com.github.zxh.classpy.llvm.bitcode.types.U32Dec;

// https://llvm.org/docs/BitCodeFormat.html#enter-subblock
// [ENTER_SUBBLOCK, blockidvbr8, newabbrevlenvbr4, <align32bits>, blocklen_32]
public class Block extends BitCodePart {

    // https://llvm.org/docs/BitCodeFormat.html#abbreviation-ids
    private static final int END_BLOCK       = 0; // This abbrev ID marks the end of the current block.
    private static final int ENTER_SUBBLOCK  = 1; // This abbrev ID marks the beginning of a new block.
    private static final int DEFINE_ABBREV   = 2; // This defines a new abbreviation.
    private static final int UNABBREV_RECORD = 3; // This ID specifies the definition of an unabbreviated record.

    @Override
    protected void readContent(BitCodeReader reader) {
        long abbrevID = reader.readFixed(2);
        if (abbrevID != ENTER_SUBBLOCK) {
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
        readBlock(reader, blockID, newAbbrevLen, blockLen.getIntValue());
    }

    // TODO
    private void readBlock(BitCodeReader reader, long blockID, long abbrevLen, int blockLen) {
        int p1 = reader.getPosition();
        if (blockID == BlockIDs.MODULE_BLOCK_ID.getValue()) {
            ModuleBlock.read(this, reader, (int) abbrevLen);
        }
        int p2 = reader.getPosition();
        reader.skipBytes(blockLen * 4 - (p2 - p1));
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
