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

public final class TestUtils {

    private TestUtils() {
    }

    public static BitBuffer parseBitBuffer(String string) {
        BitBuffer bitBuffer = new BitBuffer();
        for (int i = 0; i < string.length(); i++) {
            switch (string.charAt(i)) {
                case '1':
                    bitBuffer.push(1, 1);
                    break;
                case '0':
                    bitBuffer.push(0, 1);
                    break;
                default:
                    break;
            }
        }
        return bitBuffer;
    }

    public static String pictureToString(byte[][] picture) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < picture.length; i++) {
            for (int j = 0; j < picture[i].length; j++) {
                if (picture[i][j] == BitPlacement.EMPTY || picture[i][j] == BitPlacement.RESERVED)
                    stringBuilder.append('?');
                else
                    stringBuilder.append(picture[i][j] % 2 == 0 ? ' ' : '#');
            }
            stringBuilder.append('\n');
        }
        return stringBuilder.toString();
    }

    public static byte[][] stringToPicture(String picture) {
        String[] strings = picture.split("\n");
        byte[][] image = new byte[strings.length][strings.length];
        for (int i = 0; i < strings.length; i++) {
            String s = strings[i];
            for (int j = 0; j < s.length(); j++) {
                if (s.charAt(j) == '#') {
                    image[i][j] = BitPlacement.BLACK;
                } else {
                    image[i][j] = BitPlacement.WHITE;
                }
            }
        }
        return image;
    }

}
