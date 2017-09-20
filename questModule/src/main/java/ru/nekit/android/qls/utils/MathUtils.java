package ru.nekit.android.qls.utils;


import java.util.List;
import java.util.Random;

public class MathUtils {

    public static int sum(int[] values) {
        int sum = 0;
        if (values != null) {
            for (int value : values) {
                sum += value;
            }
        }
        return sum;
    }

    public static int usum(int[] values) {
        int sum = 0;
        if (values != null) {
            for (int value : values) {
                sum += Math.abs(value);
            }
        }
        return sum;
    }

    public static int randInt(int min, int max) {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }

    public static int randUnsignedInt(int max) {
        Random rand = new Random();
        return rand.nextInt(max + 1);
    }

    public static <T> T randItem(List<T> list) {
        return list.get(randListLength(list));
    }

    public static <T> int randListLength(List<T> list) {
        return randUnsignedInt(list.size() - 1);
    }

    public static <T> T randItem(T[] array) {
        int length = array.length;
        return array[randUnsignedInt(length - 1)];
    }

    public static int randItem(int[] array) {
        int length = array.length;
        return array[randUnsignedInt(length - 1)];
    }

    public static int randPositiveInt(int max) {
        Random rand = new Random();
        return rand.nextInt(max) + 1;
    }

    public static <T> T[] shuffleArray(T[] array) {
        int length = array.length;
        Random random = new Random();
        random.nextInt();
        for (int i = 0; i < length; i++) {
            int change = i + random.nextInt(length - i);
            swap(array, i, change);
        }
        return array;
    }

    private static <T> void swap(T[] a, int i, int change) {
        T helper = a[i];
        a[i] = a[change];
        a[change] = helper;
    }

    public static boolean randBoolean() {
        return randUnsignedInt(1) != 0;
    }
}
