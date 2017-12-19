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

import org.apache.hadoop.raid.GaloisField;

public class ErrorCorrectionEncoding implements Step<DataEncoding.EncodedData, ErrorCorrectionEncoding.DataWithErrorCorrection> {

    /**
     * Adds in error correction to the bitbuffer
     *
     * @param input current bitBuffer (with data words), number of error words, version
     * @return DataWithErrorCorrection: data + error words (BitBuffer), version (int)
     */
    @Override
    public DataWithErrorCorrection execute(DataEncoding.EncodedData input) {
        makeErrorCorrectionWords(input.bitBuffer, input.errorWords);
        addRemainderBits(input.bitBuffer, input.version);
        return new DataWithErrorCorrection(input.bitBuffer, input.version);
    }

    private void addRemainderBits(BitBuffer bitBuffer, int version) {
        // TODO: add in larger versions
        int remainderSize = 0;
        if (version > 1 && version < 7)
            remainderSize = 7;
        bitBuffer.push(0, remainderSize);
    }

    private void makeErrorCorrectionWords(BitBuffer bitBuffer, int errorWords) {
        // setup polynomials
        int[] codeWords = bitBuffer.getWords();
        reverseArray(codeWords);    // reverse for the sake of polynomial multiplication
        // TODO: support multiple EC blocks
        GaloisField galoisField = GaloisField.getInstance();
        int[] messagePoly = new int[codeWords.length + errorWords];
        System.arraycopy(codeWords, 0, messagePoly, errorWords, codeWords.length);

        // generate error correction words
        int[] generatorPoly = {1, 1};
        for (int n = 1; n < errorWords; n++) {
            int[] nextPoly = {galoisField.power(2, n), 1};
            generatorPoly = galoisField.multiply(generatorPoly, nextPoly);
        }

        // push error correction words
        int[] remainder = galoisField.remainder(messagePoly, generatorPoly);
        for (int i = errorWords - 1; i >= 0; i--)
            bitBuffer.push(remainder[i], 8);

    }

    private static void reverseArray(int[] a) {
        for (int i = 0; i < a.length / 2; i++) {
            int temp = a[i];
            a[i] = a[a.length - 1 - i];
            a[a.length - 1 - i] = temp;
        }
    }

    public static class DataWithErrorCorrection {
        public BitBuffer bitBuffer;
        int version;

        public DataWithErrorCorrection(BitBuffer bitBuffer, int version) {
            this.bitBuffer = bitBuffer;
            this.version = version;
        }
    }
}
