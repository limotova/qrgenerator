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
 * org.motovs.larisa.qrgenerator.BitBuffer is used to easily add bits to a BitSet to incorporate leading 0s and the like
 */
public class BitBuffer {
    private BitSet bitSet;
    private int currentPos;

    /**
     * Creates a new org.motovs.larisa.qrgenerator.BitBuffer to store qr code information
     *
     */
    public BitBuffer(){
        bitSet = new BitSet();
        currentPos = 0;
    }

    /**
     * Adds numbers to bitsequence
     *
     * @param buffer value to be added to bitsequence
     * @param size  determines size/length of value to be added (can truncate number or add leading 0s)
     */
    public void push(int buffer, int size){
        for(int i = 0; i < size; i ++){
            if(buffer % 2 == 1)
                bitSet.set(currentPos + size - 1 - i);
            buffer = (buffer / 2);
        }
        currentPos += size;     //brings current position back to end of bitsequence
    }

    /**
     * Gets the bitset (was mostly used in testing) TODO: maybe delete this???
     *
     * @return  bitset with qr code
     */
    public BitSet getBitSet(){
        return bitSet;
    }

    /**
     * Gets the current length of the bitset
     *
     * @return  current position, because bitset.length gives you the last 1 and ignores the 0s
     */
    public int getBitSetLength(){
        return currentPos;
    }

    /**
     * Turns the bitset into a string of 0s and 1s, separated with a space every 8 bits/byte
     * Was primarily used in testing
     *
     * @return  0s and 1s corresponding to the bitset
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < currentPos; i++){
            if(i != 0 && i % 8 == 0)
                builder.append(" ");
            if(bitSet.get(i))
                builder.append(1);
            else builder.append(0);
        }
        return builder.toString();
    }

    /**
     * Gets each word (8 bits/1 byte) in the bitsequence
     *
     * @return  array with each value being 1 byte
     */
    public int[] getWords(){
        int[] buffer = new int[(currentPos + 7) / 8];
        for(int i = 0; i < currentPos; i ++){
            if(bitSet.get(i)){
                buffer[i / 8] |= (1 << (7 - i % 8));
            }
        }
        return buffer;
    }

    /**
     * Returns the bit at the specified position (used when drawing)
     *
     * @param position  position of requested bit
     * @return  true = 1; false = 0
     */
    public boolean getBit(int position){
        return bitSet.get(position);
    }
}

