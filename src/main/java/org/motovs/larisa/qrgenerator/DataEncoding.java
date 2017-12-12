/*
 * Copyright 2017 Larisa Motova
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.motovs.larisa.qrgenerator;

import java.io.UnsupportedEncodingException;

public class DataEncoding implements Step<DataAnalysis.AnalyzedString, DataEncoding.EncodedData> {

    @Override
    public EncodedData execute(DataAnalysis.AnalyzedString input) {
        BitBuffer bitBuffer = new BitBuffer();
        addMode(bitBuffer, input.mode);
        addCharacterCount(bitBuffer, input.inputString.length());
        encodeData(bitBuffer, input.inputString, input.version);
        return new EncodedData(bitBuffer, input.version, getEncodedWordCount(input.version), getErrorWordCount(input.version));
    }

    // TODO: support other modes
    private void encodeData(BitBuffer bitBuffer, String string, int version){
        byte[] encodedChars;
        try {
            encodedChars = string.getBytes("ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            encodedChars = new byte[0];
        }

        for(byte c : encodedChars)
            bitBuffer.push(c, 8);

        // add terminator bits if needed
        int codeWordsNeeded = getEncodedWordCount(version);
        // TODO: this is different from the original
        if(bitBuffer.getBitSetLength() < codeWordsNeeded * 8 - 4){
            bitBuffer.push(0, 4);
        } else {
            bitBuffer.push(0, codeWordsNeeded * 8 - bitBuffer.getBitSetLength());
        }
        bitBuffer.push(0, bitBuffer.getBitSetLength() % 8);

        // more terminator bits
        // TODO: make this better
        while(bitBuffer.getBitSetLength() < codeWordsNeeded * 8){
            bitBuffer.push(236, 8);
            if(bitBuffer.getBitSetLength() < codeWordsNeeded * 8)
                bitBuffer.push(17, 8);
        }
    }

    // TODO: add multiple error words per block
    public static int getEncodedWordCount(int version){
        return totalCodeWords[version - 1] - errorWordsPerBlock[version - 1];
    }

    public static int getErrorWordCount(int version){
        return errorWordsPerBlock[version - 1];
    }

    // TODO: these numbers are different for version size + mode
    private void addCharacterCount(BitBuffer bitBuffer, int stringLength){
        bitBuffer.push(stringLength, 8);
    }
    private void addMode(BitBuffer bitBuffer, Mode mode){
        bitBuffer.push(mode.indicatorValue, 4);
    }

    public static class EncodedData{
        public BitBuffer bitBuffer;
        public int version;
        public int codeWords;
        public int errorWords;
//        public int numBlocks;

        public EncodedData(BitBuffer bitBuffer, int version, int codeWords, int errorWords){
            this.bitBuffer = bitBuffer;
            this.version = version;
            this.codeWords = codeWords;
            this.errorWords = errorWords;
//            this.numBlocks = numBlocks;
        }
    }

    // TODO: move somewhere else
    private static final int[] totalCodeWords = {26, 44, 70, 100, 134};
    private static final int[] errorWordsPerBlock = {10, 16, 26, 18, 24}; // only M level
}
