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

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

public class BitPlacementTests {

    @Test
    public void testExecute() {
        String bitMessage = "01000000 00110110 11010111 00110110 01110000 11101100 00010001 11101100 00010001 11101100 00010001 11101100 00010001 11101100 00010001 11101100";
        String errorPortion = " 11101110 10111111 11100000 01100100 11001011 11101000 11100001 00100110 00011100 11011011";

        String finalMessage =
                "#######  # #  #######\n" +
                        "#     # ####  #     #\n" +
                        "# ### # # #   # ### #\n" +
                        "# ### # ## #  # ### #\n" +
                        "# ### #   # # # ### #\n" +
                        "#     #   ##  #     #\n" +
                        "####### # # # #######\n" +
                        "        #            \n" +
                        "#     # ## # ##  ### \n" +
                        " ## #  ####### ## ## \n" +
                        "  #  ##  ## # ## # # \n" +
                        "### #   ## ##### ### \n" +
                        "  ### #  #########   \n" +
                        "        ### #    ## #\n" +
                        "#######  # # #  ## # \n" +
                        "#     #       #   ## \n" +
                        "# ### #   ## #  # #  \n" +
                        "# ### #  ####### #   \n" +
                        "# ### #  # ### ### ##\n" +
                        "#     #   ###### #   \n" +
                        "####### ##  #  #  ## \n";

        BitPlacement bitPlacement = new BitPlacement();
        ErrorCorrectionEncoding.DataWithErrorCorrection dataWithErrorCorrection =
                new ErrorCorrectionEncoding.DataWithErrorCorrection(TestUtils.parseBitBuffer(bitMessage + errorPortion),
                        1, ErrorCorrectionLevel.MEDIUM);
        BitPlacement.PlacedBits placedBits = bitPlacement.execute(dataWithErrorCorrection);

        assertEquals(finalMessage, TestUtils.pictureToString(placedBits.image));
    }

    @Test
    public void testGenerateFormattingPolynomial(){
        // had all medium error correction polynomials at hand to begin with
        byte[] M0 = {0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1, 0, 1};  // M, mask 0
        byte[] M1 = {1, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 1};  // M, mask 1
        byte[] M2 = {0, 0, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 0, 1};  // M, mask 2
        byte[] M3 = {1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 1, 0, 1};  // M, mask 3
        byte[] M4 = {1, 0, 0, 1, 1, 1, 1, 1, 1, 0, 1, 0, 0, 0, 1};  // M, mask 4
        byte[] M5 = {0, 1, 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 1};  // M, mask 5
        byte[] M6 = {1, 1, 1, 0, 1, 0, 0, 1, 1, 1, 1, 1, 0, 0, 1};  // M, mask 6
        byte[] M7 = {0, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 1};  // M, mask 7
        byte[] L4 = {1, 1, 1, 1, 0, 1, 0, 0, 0, 1, 1, 0, 0, 1, 1};  // L, mask 4
        byte[] Q7 = {1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 0, 1, 0};
        byte[] H2 = {1, 1, 1, 0, 0, 1, 1, 1, 0, 0, 1, 1, 1, 0, 0};
        BitPlacement bitPlacement = new BitPlacement();
        assertArrayEquals(bitPlacement.generateFormattingPolynomial(0, ErrorCorrectionLevel.MEDIUM), M0);
        assertArrayEquals(bitPlacement.generateFormattingPolynomial(1, ErrorCorrectionLevel.MEDIUM), M1);
        assertArrayEquals(bitPlacement.generateFormattingPolynomial(2, ErrorCorrectionLevel.MEDIUM), M2);
        assertArrayEquals(bitPlacement.generateFormattingPolynomial(3, ErrorCorrectionLevel.MEDIUM), M3);
        assertArrayEquals(bitPlacement.generateFormattingPolynomial(4, ErrorCorrectionLevel.MEDIUM), M4);
        assertArrayEquals(bitPlacement.generateFormattingPolynomial(5, ErrorCorrectionLevel.MEDIUM), M5);
        assertArrayEquals(bitPlacement.generateFormattingPolynomial(6, ErrorCorrectionLevel.MEDIUM), M6);
        assertArrayEquals(bitPlacement.generateFormattingPolynomial(7, ErrorCorrectionLevel.MEDIUM), M7);
        assertArrayEquals(bitPlacement.generateFormattingPolynomial(4, ErrorCorrectionLevel.LOW), L4);
        assertArrayEquals(bitPlacement.generateFormattingPolynomial(7, ErrorCorrectionLevel.QUARTILE), Q7);
        assertArrayEquals(bitPlacement.generateFormattingPolynomial(2, ErrorCorrectionLevel.HIGH), H2);
    }

    @Test
    public void testGenerateVersionPolynomial(){
        byte[] v7 = {0, 0, 1, 0, 1, 0, 0, 1, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0};
        byte[] v17 = {1, 0, 1, 1, 1, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0};
        byte[] v33 = {0, 0, 0, 0, 1, 1, 1, 1, 0, 1, 1, 0, 1, 0, 0, 0, 0, 1};
        byte[] v40 = {1, 0, 0, 1, 0, 1, 1, 0, 0, 0, 1, 1, 0, 0, 0, 1, 0, 1};
        BitPlacement bitPlacement = new BitPlacement();
        assertArrayEquals(bitPlacement.generateVersionPolynomial(7), v7);
        assertArrayEquals(bitPlacement.generateVersionPolynomial(17), v17);
        assertArrayEquals(bitPlacement.generateVersionPolynomial(33), v33);
        assertArrayEquals(bitPlacement.generateVersionPolynomial(40), v40);
    }

}
