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

public class BitBufferTests {

    @Test
    public void testPush() {
        BitBuffer bitBuffer = new BitBuffer();
        bitBuffer.push(7, 4);
        assertEquals("0111", bitBuffer.toString());
        bitBuffer.push(250, 8);
        assertEquals("01111111 1010", bitBuffer.toString());
    }


}
