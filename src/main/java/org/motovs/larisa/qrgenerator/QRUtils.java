package org.motovs.larisa.qrgenerator;

public class QRUtils {

    static void reverseArray(int[] a) {
        for (int i = 0; i < a.length / 2; i++) {
            int temp = a[i];
            a[i] = a[a.length - 1 - i];
            a[a.length - 1 - i] = temp;
        }
    }

    static void reverseArray(boolean[] a) {
        for (int i = 0; i < a.length / 2; i++) {
            boolean temp = a[i];
            a[i] = a[a.length - 1 - i];
            a[a.length - 1 - i] = temp;
        }
    }

    static void reverseArray(byte[] a) {
        for (int i = 0; i < a.length / 2; i++) {
            byte temp = a[i];
            a[i] = a[a.length - 1 - i];
            a[a.length - 1 - i] = temp;
        }
    }
}
