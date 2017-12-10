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

import java.nio.charset.Charset;
import java.util.Objects;

public class DataAnalysis implements Step<String, DataAnalysis.AnalyzedString> {
    // this class figures out: encoding Mode, error correction level (kinda), version number

    @Override
    public AnalyzedString execute(String input) {
        Mode mode = determineMode(input);
        ErrorCorrectionLevel ec = ErrorCorrectionLevel.MEDIUM;
        int version = determineVersion(input.length(), mode, ec);

        return new AnalyzedString(input, mode, version);
    }

    // TODO: other modes
    private Mode determineMode(String input){
        // NUMERIC:         0-9
        // ALPHANUMERIC:    0-9,
        // BYTE:            ISO_8859_1
        // KANJI:           SHIFT-JIS
        Mode mode = Mode.BYTE;
        if(Charset.forName("ISO_8859_1").newEncoder().canEncode(input))
            mode = Mode.BYTE;

        return mode;
    }

    // TODO: other EC/modes
    private int determineVersion(int inputSize, Mode mode, ErrorCorrectionLevel ec){
        // only Byte mode so far, on medium EC:
        int version;

        if(inputSize < 15)
            version = 1;
        else if (inputSize < 26)
            version = 2;
        else if (inputSize < 43)
            version = 3;
//        else if (inputSize < 63)
//            version = 4;
//        else if(inputSize < 84)
//            version = 5;
        else version = -1;

        return version;
    }

    public static class AnalyzedString{
        public final String inputString;
        public final Mode mode;
        public final int version;

        public AnalyzedString(String inputString, Mode mode, int version){
            this.inputString = inputString;
            this.mode = mode;
            this.version = version;
        }

    }
}
