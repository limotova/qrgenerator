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

import java.util.Arrays;
import java.util.List;

public class BitPlacement implements Step<ErrorCorrectionEncoding.DataWithErrorCorrection, BitPlacement.PlacedBits> {

    private static final byte RESERVED_WHITE = 0;   // don't change RESERVED_WHITE or RESERVED_BLACK
    private static final byte RESERVED_BLACK = 1;
    private static final byte RESERVED = 2;
    private static final byte EMPTY = 3;
    private static final byte WHITE = 4;
    private static final byte BLACK = 5;

    // rules for flipping bits in the QR code's drawing
    private static final List<Mask> MASKS = Arrays.asList(
            (Mask) (y, x) -> (y + x) % 2 == 0,  // flip the bit if (row + column) mod 2 == 0
            (Mask) (y, x) -> (y % 2) == 0,      // flip bit if (row) mod 2 == 0
            (Mask) (y, x) -> (x % 3) == 0,      // flip bit if (column) mod 3 == 0
            (Mask) (y, x) -> (y + x) % 3 == 0,  // flip bit if (row + column) mod 3 == 0
            (Mask) (y, x) -> ((y / 2) + (x / 3)) % 2 == 0, // flip bit if (floor (row / 2) + floor (column / 3)) mod 2 == 0
            (Mask) (y, x) -> (((y * x) % 2) + ((y * x) % 3)) == 0, // flip bit if (row * column mod 2 + row * column mod 3) == 0
            (Mask) (y, x) -> (((y * x) % 2) + ((y * x) % 3)) % 2 == 0, // flip bit if (row * column mod 2 + row * column mod 3) mod 2 == 0
            (Mask) (y, x) -> (((y + x) % 2) + ((y * x) % 3)) % 2 == 0 // flip bit if (row + column mod 2 + row * column mod 3) mod 2 == 0
    );

    // evaluation methods used to test how good a mask is
    private static final List<MaskEvaluator> MASK_EVALUATORS = Arrays.asList(
            new Evaluator1(), new Evaluator2(), new Evaluator3(), new Evaluator4()
    );

    /**
     * Uses the data and error correction words to create the final QR code
     *
     * @param input BitBuffer with words, version
     * @return PlacedBits: contains byte[][] with the complete QR code
     */
    @Override
    public PlacedBits execute(ErrorCorrectionEncoding.DataWithErrorCorrection input) {
        byte[][] image = makeTemplate(input.version);
        reserveAreas(image);
        drawInitialBits(image, input.bitBuffer);
        int mask = addMask(image);
        addFormatting(image, mask, input.errorCorrectionLevel);
        return new PlacedBits(image);
    }

    // Marks certain regions on the QR Code as reserved
    private void reserveAreas(byte[][] image) {
        byte[] reservedArray = new byte[15];
        Arrays.fill(reservedArray, RESERVED);
        fillReserveAreas(image, reservedArray);
    }

    // Fills certain regions with mask- and error-correction-specific data
    private void addFormatting(byte[][] image, int mask, ErrorCorrectionLevel errorCorrectionLevel) {
        byte[] formattingInfo = generateFormattingPolynomial(mask, errorCorrectionLevel);
        fillReserveAreas(image, formattingInfo);
    }

    // Fills a specific region around the finder patterns with the contents of the formattingInfo byte[]
    private void fillReserveAreas(byte[][] image, byte[] formattingInfo) {
        //formatting info around top left finder pattern
        for (int i = 0; i < 6; i++) {
            image[i][8] = formattingInfo[i];
            image[8][5 - i] = formattingInfo[i + 9];
        }
        image[7][8] = formattingInfo[6];
        image[8][8] = formattingInfo[7];
        image[8][7] = formattingInfo[8];

        //formatting info below top right finder pattern
        for (int i = 0; i < 8; i++) {
            image[8][image.length - i - 1] = formattingInfo[i];
        }

        //formatting info to right of bottom left finder pattern
        for (int i = 0; i < 7; i++) {
            image[image.length - i - 1][8] = formattingInfo[formattingInfo.length - 1 - i];
        }
    }

    // generates formatting information that goes around the finder patterns
    byte[] generateFormattingPolynomial(int mask, ErrorCorrectionLevel errorCorrectionLevel) {
        byte[] maskErrorInfo = new byte[5];
        int ecValue = errorCorrectionLevel.indicatorValue;
        for (int i = 1; i >= 0 && ecValue > 0; i--) {
            maskErrorInfo[i] = (byte) (ecValue % 2);
            ecValue /= 2;
        }
        for (int i = 4; i >= 2 && mask > 0; i--) {
            maskErrorInfo[i] = (byte) (mask % 2);
            mask /= 2;
        }

        final byte[] generatorPoly = {1, 0, 1, 0, 0, 1, 1, 0, 1, 1, 1};
        byte[] formattingInfo = generateInfoPolynomial(maskErrorInfo, 15, generatorPoly);
        final byte[] maskPolynomial = {0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1, 0, 1};
        dividePolynomials(formattingInfo, 0, maskPolynomial);

        return formattingInfo;
    }

    // fills a 3x6 area on the top right and a 6x3 area on the bottom left with version info
    private void fillVersionAreas(byte[][] image, int version) {
        byte[] reservedArray = generateVersionPolynomial(version);
        for (int i = 0; i < reservedArray.length; i++) {
            image[image.length - 11 + (i / 6)][i % 6] = reservedArray[i];   // bottom left
            image[i / 3][image.length - 11 + (i % 3)] = reservedArray[i];   // top right
        }
    }

    // generates version information for the 3x6 and 6x3 areas
    byte[] generateVersionPolynomial(int version) {
        byte[] versionInfo = new byte[6];
        for (int i = versionInfo.length - 1; i >= 0 && version > 0; i--) {
            versionInfo[i] = (byte) (version % 2);
            version /= 2;
        }

        final byte[] generatorPoly = {1, 1, 1, 1, 1, 0, 0, 1, 0, 0, 1, 0, 1};
        return generateInfoPolynomial(versionInfo, 18, generatorPoly);
    }

    // creates binary polynomials with error correction
    private byte[] generateInfoPolynomial(byte[] original, int size, byte[] generator) {
        byte[] infoPoly = new byte[size];
        System.arraycopy(original, 0, infoPoly, 0, original.length);
        int infoPolyLength = truePolyLength(infoPoly);
        while (infoPolyLength >= generator.length) {
            dividePolynomials(infoPoly, infoPoly.length - infoPolyLength, generator);
            infoPolyLength = truePolyLength(infoPoly);
        }
        System.arraycopy(original, 0, infoPoly, 0, original.length);
        QRUtils.reverseArray(infoPoly);
        return infoPoly;
    }

    private void dividePolynomials(byte[] dividend, int start, byte[] divisor) {
        for (int i = start; i < dividend.length && i - start < divisor.length; i++) {
            dividend[i] = (byte) (dividend[i] ^ divisor[i - start]);
        }
    }

    // gets the length of the polynomial (exponents are in decreasing order; there can be leading 0s on the left)
    private int truePolyLength(byte[] polynomial) {
        for (int i = 0; i < polynomial.length; i++) {
            if (polynomial[i] > 0) {
                return polynomial.length - i;
            }
        }
        return 0;
    }

    // draws the data/error correction words onto the image
    private void drawInitialBits(byte[][] image, BitBuffer bitBuffer) {
        int columns = (image.length - 1) / 2;   // total number of columns for words to be drawn in
        int currentPos = 0;
        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < image.length * 2; j++) {
                // setting up x
                int x = image.length - 1 - i * 2;
                if (j % 2 == 1) {   // so x goes back and forth (r, l, r, l)
                    x--;
                }
                if (x < 7) {
                    x--;   // done to skip the timing pattern on left side
                }

                // setting up y: direction travelling switches from up to down every-other column
                int y = (i % 2 == 0) ? (image.length - j / 2 - 1) : (j / 2);

                if (image[y][x] == EMPTY) {
                    image[y][x] = bitBuffer.getBit(currentPos++) ? BLACK : WHITE;
                }
            }
        }
    }

    // determines which mask to add and adds it permanently
    private int addMask(byte[][] image) {
        int bestMask = -1;
        int min = Integer.MAX_VALUE;

        for (int i = 0; i < MASKS.size(); i++) {
            Mask m = MASKS.get(i);
            applyMask(m, image);
            int score = scoreImage(image);
            if (min > score) {
                bestMask = i;
                min = score;
            }
            applyMask(m, image);
        }

        Mask m = MASKS.get(bestMask);
        applyMask(m, image);
        return bestMask;
    }

    // evaluate the image based on 4 criteria
    private int scoreImage(byte[][] image) {
        int runningTotal = 0;
        for (MaskEvaluator e : MASK_EVALUATORS) {
            runningTotal += e.evaluate(image);
        }
        return runningTotal;
    }

    // adds the specific mask to the image
    private void applyMask(Mask mask, byte[][] image) {
        for (int y = 0; y < image.length; y++) {
            for (int x = 0; x < image.length; x++) {
                if (mask.evaluate(y, x)) {
                    flipBit(y, x, image);
                }
            }
        }
    }

    // flips bits that aren't reserved
    private void flipBit(int y, int x, byte[][] image) {
        if (image[y][x] == WHITE) {
            image[y][x] = BLACK;
        } else if (image[y][x] == BLACK) {
            image[y][x] = WHITE;
        }
    }

    // creates the byte[][] to be used as the QR code and draws in all constant patterns
    private byte[][] makeTemplate(int version) {
        int size = ((version - 1) * 4) + 21;
        byte[][] image = new byte[size][size];
        for (byte[] row : image)
            Arrays.fill(row, EMPTY);

        drawFinderPattern(image, 0, 0);
        drawFinderPattern(image, size - 7, 0);
        drawFinderPattern(image, 0, size - 7);

        drawAlignmentPatterns(image, version);
        drawTimingPatterns(image);
        drawDarkModule(image, version);

        if(version > 6){
            fillVersionAreas(image, version);
        }
        return image;
    }

    private void drawFinderPattern(byte[][] image, int startX, int startY) {
        // separators around the finder pattern (these are cut off by the edges of the QR code)
        for (int i = 0; i < 9; i++) {
            int x = startX - 1 + i;
            if (x >= 0 && x < image.length) {
                for (int j = 0; j < 9; j++) {
                    int y = startY - 1 + j;
                    if (y >= 0 && y < image.length) {
                        image[x][y] = RESERVED_WHITE;
                    }
                }
            }
        }

        // fills in entire pattern black first
        for (int i = 0; i < 7; i++)
            for (int j = 0; j < 7; j++)
                image[startX + i][startY + j] = RESERVED_BLACK;

        // draws in the inner white square
        for (int i = 0; i < 5; i++) {
            image[startX + 1 + i][startY + 1] = RESERVED_WHITE;
            image[startX + 1][startY + 1 + i] = RESERVED_WHITE;
            image[startX + 1 + i][startY + 5] = RESERVED_WHITE;
            image[startX + 5][startY + 1 + i] = RESERVED_WHITE;
        }
    }

    private void drawAlignmentPatterns(byte[][] image, int version) {
        // TODO: larger versions have more alignment patterns with greatly varrying locations
        if (version < 2)
            return;
        int center = version * 4 + 10;
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++)
                image[center - 2 + i][center - 2 + j] = RESERVED_BLACK;
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                image[center - 1 + i][center - 1 + j] = RESERVED_WHITE;
        image[center][center] = RESERVED_BLACK;
    }

    private void drawTimingPatterns(byte[][] image) {
        for (int i = 8; i < image.length - 8; i++) {
            image[i][6] = i % 2 == 0 ? RESERVED_BLACK : RESERVED_WHITE;
            image[6][i] = i % 2 == 0 ? RESERVED_BLACK : RESERVED_WHITE;
        }
    }

    private void drawDarkModule(byte[][] image, int version) {
        image[4 * version + 9][8] = RESERVED_BLACK;
    }

    static boolean bitIsBlack(byte[][] image, int y, int x) {
        return image[y][x] == RESERVED_BLACK || image[y][x] == BLACK;
    }

    static boolean bitHasData(byte[][] image, int y, int x) {
        return image[y][x] != EMPTY && image[y][x] != RESERVED;
    }

    public static class PlacedBits {
        public final byte[][] image;

        PlacedBits(byte[][] image) {
            this.image = image;
        }
    }
}
