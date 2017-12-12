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
}
