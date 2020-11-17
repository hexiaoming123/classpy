package com.github.zxh.classpy.llvm.bitcode;

import com.github.zxh.classpy.common.BytesReader;
import com.github.zxh.classpy.common.ParseException;

import java.nio.ByteOrder;

// BitStream
public class BitCodeReader extends BytesReader {

    private long curWord;
    private int bitsInCurWord;

    public BitCodeReader(byte[] data) {
        super(data, ByteOrder.LITTLE_ENDIAN);
    }

    private void readNextWord() {
        long nextWord = readFixedU32();
        curWord = (nextWord << bitsInCurWord) | curWord;
        bitsInCurWord += 32;
    }

    // https://llvm.org/docs/BitCodeFormat.html#fixed-width-value
    public long readFixed(int numBits) {
        if (numBits < 0 || numBits > 32) {
            throw new ParseException("no or too many bits to read: " + numBits);
        }
        if (bitsInCurWord < numBits) {
            readNextWord();
        }
        System.out.printf("readFixed, numBits=%d, curWord=%x, bitsInCurWord=%d\n",
                numBits, curWord, bitsInCurWord);
        long x = curWord & ~(-1 << numBits);
        curWord >>>= numBits;
        bitsInCurWord -= numBits;
        return x;
    }

    // https://llvm.org/docs/BitCodeFormat.html#variable-width-value
    public long readVBR(int numBits) {
        if (numBits < 0 || numBits > 8) {
            throw new ParseException("no or too many bits to read VBR: " + numBits);
        }

        long msb = 1 << (numBits - 1);
        long mask = ~(-1 << (numBits - 1));

        long result = 0;
        for (int shift = 0; shift < 64; shift += numBits - 1) {
            long n = readFixed(numBits);
            result |= ((n & mask) << shift);
            if ((n & msb) == 0) {
                return result;
            }
        }

        throw new ParseException("invalid VBR ?");
    }

//
//    public long readU32() {
//        return readUnsignedLEB128(32);
//    }
//
//    public long readU64() {
//        return readUnsignedLEB128(64);
//    }
//
//    public long readS32() {
//        return readSignedLEB128(32);
//    }
//
//    public long readS64() {
//        return readSignedLEB128(64);
//    }
//
//    private long readUnsignedLEB128(int nBits) {
//        long result = 0;
//        for (int shift = 0; shift < nBits; shift += 7) {
//            int b = readByte();
//            result |= ((b & 0b0111_1111) << shift);
//            if ((b & 0b1000_0000) == 0) {
//                return result;
//            }
//        }
//
//        throw new RuntimeException("can not decode unsigned LEB128");
//    }
//
//    private long readSignedLEB128(int size) {
//        long result = 0;
//        int shift = 0;
//        //size = number of bits in signed integer;
//        byte b;
//        do{
//            b = readByte();
//            result |= ((b & 0b0111_1111) << shift);
//            shift += 7;
//        } while ((b & 0b1000_0000) != 0);
//
//        /* sign bit of byte is second high order bit (0x40) */
//        if ((shift < size) && ((b & 0b0100_0000) != 0)) {
//            /* sign extend */
//            result |= (~0 << shift);
//        }
//
//        return result;
//    }

}
