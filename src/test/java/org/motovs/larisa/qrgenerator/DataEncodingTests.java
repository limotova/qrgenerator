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

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DataEncodingTests {

    @Test
    public void testExecute() {
        String testString = "msg";
        String bitSeq = "01000000 00110110 11010111 00110110 01110000 11101100 00010001 11101100 00010001 11101100 00010001 11101100 00010001 11101100 00010001 11101100";
        DataAnalysis.AnalyzedString analyzedString = new DataAnalysis.AnalyzedString(testString, Mode.BYTE, 1, ErrorCorrectionLevel.MEDIUM);
        DataEncoding dataEncoding = new DataEncoding();
        DataEncoding.EncodedData encodedData = dataEncoding.execute(analyzedString);
        assertEquals(encodedData.bitBuffer.toString(), bitSeq);
        assertEquals(encodedData.codeWords, 16);
        assertEquals(encodedData.errorWords, 10);
        assertEquals(encodedData.version, 1);
        assertEquals(encodedData.errorCorrectionLevel, ErrorCorrectionLevel.MEDIUM);
    }
}
