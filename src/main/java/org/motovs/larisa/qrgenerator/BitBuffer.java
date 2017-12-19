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

import java.util.BitSet;

/**
 * Modification on the BitSet class to allow for trailing 0s and easily push larger numbers
 */
public class BitBuffer {
    private BitSet bitSet;
    private int currentPos;

    /**
     * Creates a new BitBuffer to store qr code information
     */
    BitBuffer() {
        bitSet = new BitSet();
        currentPos = 0;
    }

    /**
     * Adds numbers in binary form to bitset
     *
     * @param buffer value to be added to bitset
     * @param size   determines size/length of value to be added (will truncate if too short; add trailing 0s if too long)
     */
    void push(int buffer, int size) {
        for (int i = 0; i < size; i++) {
            if (buffer % 2 == 1)
                bitSet.set(currentPos + size - 1 - i);
            buffer = (buffer / 2);
        }
        currentPos += size;     //brings current position back to end of bitsequence
    }

    /**
     * Gets the current length of the bitset
     *
     * @return current length
     */
    int getBitSetLength() {
        return currentPos;  // bitset.length ignores 0s and only returns the position of the last 1
    }

    /**
     * Turns the bitset into a string of 0s and 1s, separated with a space every 8 bits
     *
     * @return 0s and 1s corresponding to the bitset
     */
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < currentPos; i++) {
            if (i != 0 && i % 8 == 0)
                stringBuilder.append(" ");
            if (bitSet.get(i))
                stringBuilder.append(1);
            else stringBuilder.append(0);
        }
        return stringBuilder.toString();
    }

    /**
     * Gets each word (8 bits per word) in the bitset
     *
     * @return array of integers with each value representing a word
     */
    int[] getWords() {
        int[] buffer = new int[(currentPos + 7) / 8];
        for (int i = 0; i < currentPos; i++) {
            if (bitSet.get(i)) {
                buffer[i / 8] |= (1 << (7 - i % 8));
            }
        }
        return buffer;
    }

    /**
     * Returns the bit at the specified position
     *
     * @param position position of requested bit
     * @return true = 1; false = 0
     */
    boolean getBit(int position) {
        return bitSet.get(position);
    }
}

