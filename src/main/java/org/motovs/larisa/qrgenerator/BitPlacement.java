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

    public static final byte RESERVED_WHITE = 0;
    public static final byte RESERVED_BLACK = 1;
    public static final byte RESERVED = 2;
    public static final byte EMPTY = 3;
    public static final byte WHITE = 4;
    public static final byte BLACK = 5;

    public static final List<Mask> MASKS = Arrays.asList(
            (Mask) (y, x) -> (y + x) % 2 == 0,  // flip the bit if (row + column) mod 2 == 0
            (Mask) (y, x) -> (y % 2) == 0,      // flip bit if (row) mod 2 == 0
            (Mask) (y, x) -> (x % 3) == 0,      // flip bit if (column) mod 3 == 0
            (Mask) (y, x) -> (y + x) % 3 == 0,  // flip bit if (row + column) mod 3 == 0
            (Mask) (y, x) -> ((y / 2) + (x / 3)) % 2 == 0, // flip bit if (floor (row / 2) + floor (column / 3)) mod 2 == 0
            (Mask) (y, x) -> (((y * x) % 2) + ((y * x) % 3)) == 0, // flip bit if (row * column mod 2 + row * column mod 3) == 0
            (Mask) (y, x) -> (((y * x) % 2) + ((y * x) % 3)) % 2 == 0, // flip bit if (row * column mod 2 + row * column mod 3) mod 2 == 0
            (Mask) (y, x) -> (((y + x) % 2) + ((y * x) % 3)) % 2 == 0 // flip bit if (row + column mod 2 + row * column mod 3) mod 2 == 0
    );

    public static final List<MaskEvaluator> MASK_EVALUATORS = Arrays.asList(
            new Evaluator1(), new Evaluator2(), new Evaluator3(), new Evaluator4()
    );

    @Override
    public PlacedBits execute(ErrorCorrectionEncoding.DataWithErrorCorrection input) {
        byte[][] image = makeTemplate(input.version);
        reserveAreas(image, input.version);
        drawInitialBits(image, input.bitBuffer);
        int mask = addMask(image);
        addFormatting(image, mask);
        return new PlacedBits(image);
    }

    private void reserveAreas(byte[][] image, int version) {
        byte[] reservedArray = new byte[15];
        Arrays.fill(reservedArray, RESERVED);
        fillReserveAreas(image, reservedArray);
        // TODO: for versions larger than 7, reserve version information area
    }

    private void addFormatting(byte[][] image, int mask) {
        // TODO: add more mask/error correction information and do this with math
        byte[] formattingInfo;
        switch (mask) {
            case 0:
                formattingInfo = new byte[]{0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1, 0, 1};  // M, mask 0
                break;
            case 1:
                formattingInfo = new byte[]{1, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 1};  // M, mask 1
                break;
            case 2:
                formattingInfo = new byte[]{0, 0, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 0, 1};  // M, mask 2
                break;
            case 3:
                formattingInfo = new byte[]{1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 1, 0, 1};  // M, mask 3
                break;
            case 4:
                formattingInfo = new byte[]{1, 0, 0, 1, 1, 1, 1, 1, 1, 0, 1, 0, 0, 0, 1};  // M, mask 4
                break;
            case 5:
                formattingInfo = new byte[]{0, 1, 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 1};  // M, mask 5
                break;
            case 6:
                formattingInfo = new byte[]{1, 1, 1, 0, 1, 0, 0, 1, 1, 1, 1, 1, 0, 0, 1};  // M, mask 6
                break;
            case 7:
                formattingInfo = new byte[]{0, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 1};  // M, mask 7
                break;
            default:
                formattingInfo = new byte[15];
                break;
        }

        fillReserveAreas(image, formattingInfo);
    }

    private void fillReserveAreas(byte[][] image, byte[] formattingInfo) {
        for (int i = 0; i < 6; i++) {        //formatting info around top left finder pattern
            image[i][8] = formattingInfo[i];
            image[8][5 - i] = formattingInfo[i + 9];
        }
        image[7][8] = formattingInfo[6];
        image[8][8] = formattingInfo[7];
        image[8][7] = formattingInfo[8];

        for (int i = 0; i < 8; i++)         //formatting info below top right finder pattern
            image[8][image.length - i - 1] = formattingInfo[i];

        for (int i = 0; i < 7; i++)         //formatting info to right of bottom left finder pattern
            image[image.length - i - 1][8] = formattingInfo[14 - i];
    }

    private void drawInitialBits(byte[][] image, BitBuffer bitBuffer) {

        int columns = (image.length - 1) / 2;       //total up/down columns for words to travel in
        int currentPos = 0;
        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < image.length * 2; j++) {
                int x;
                int y;
                if (i % 2 == 0) {
                    y = image.length - j / 2 - 1;  //so y doesn't go up/down every single turn
                } else {
                    y = j / 2;
                }

                if (j % 2 == 0) {
                    x = image.length - 1 - i * 2;   //so x goes back and forth (r, l, r, l)
                } else {
                    x = image.length - 2 - i * 2;
                }

                if (x < 7) {
                    x--;   //done to skip the timing pattern on left side
                }
                if (image[y][x] == EMPTY) {
                    image[y][x] = bitBuffer.getBit(currentPos++) ? BLACK : WHITE;
                }
            }
        }
    }

    // determines which mask to add and adds it
    int addMask(byte[][] image) {
        // this will  include designing all masks, getting all formatting information, figuring out which is the best
        int bestMask = -1;
        int min = Integer.MAX_VALUE;

        for (int i = 0; i < MASKS.size(); i++) {
            Mask m = MASKS.get(i);
            applyMask(m, image);
            int score = scoreImage(image);
//            System.out.println(score);
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

    // evaluate the image based on 4 criterias
    private int scoreImage(byte[][] image) {
        int runningTotal = 0;
        for (MaskEvaluator e : MASK_EVALUATORS) {
            runningTotal += e.evaluate(image);
        }
        return runningTotal;
    }


    public static boolean moduleIsBlack(byte[][] image, int y, int x) {
        return image[y][x] == RESERVED_BLACK || image[y][x] == BLACK;
    }

    private void applyMask(Mask mask, byte[][] image) {
        for (int y = 0; y < image.length; y++) {
            for (int x = 0; x < image.length; x++) {
                if (mask.evaluate(y, x)) {
                    flipBit(y, x, image);
                }
            }
        }
    }

    private void flipBit(int y, int x, byte[][] image) {
        if (image[y][x] == WHITE) {
            image[y][x] = BLACK;
        } else if (image[y][x] == BLACK) {
            image[y][x] = WHITE;
        }
    }

    private byte[][] makeTemplate(int version) {
        int size = ((version - 1) * 4) + 21;
        byte[][] image = new byte[size][size];
        for (byte[] row : image)
            Arrays.fill(row, EMPTY);

        finderPattern(image, 0, 0);
        finderPattern(image, size - 7, 0);
        finderPattern(image, 0, size - 7);

        alignmentPatterns(image, version);
        timingPatterns(image);
        darkModule(image, version);
        return image;
    }


    private void finderPattern(byte[][] image, int startX, int startY) {
        // TODO: is there a way to make this better
        // separators around the finder pattern
        for (int i = 0; i < 9; i++) {
            int x = startX - 1 + i;
            if (x >= 0 && x < image.length) {
                for (int j = 0; j < 9; j++) {
                    int y = startY - 1 + j;
                    if (y >= 0 && y < image.length)
                        image[x][y] = RESERVED_WHITE;
                }
            }
        }

        for (int i = 0; i < 7; i++)     // fills in entire pattern black first
            for (int j = 0; j < 7; j++)
                image[startX + i][startY + j] = RESERVED_BLACK;
        for (int i = 0; i < 5; i++) {
            image[startX + 1 + i][startY + 1] = RESERVED_WHITE;      //fills in the inner white square
            image[startX + 1][startY + 1 + i] = RESERVED_WHITE;
            image[startX + 1 + i][startY + 5] = RESERVED_WHITE;
            image[startX + 5][startY + 1 + i] = RESERVED_WHITE;
        }
    }

    private void alignmentPatterns(byte[][] image, int version) {
        // TODO: also larger versions have more numbers
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

    private void timingPatterns(byte[][] image) {
        for (int i = 8; i < image.length - 8; i++) {
            image[i][6] = i % 2 == 0 ? RESERVED_BLACK : RESERVED_WHITE;
            image[6][i] = i % 2 == 0 ? RESERVED_BLACK : RESERVED_WHITE;
        }
    }

    private void darkModule(byte[][] image, int version) {
        image[4 * version + 9][8] = RESERVED_BLACK;
    }

    public static class PlacedBits {
        public byte[][] image;

        public PlacedBits(byte[][] image) {
            this.image = image;
        }
    }
}
