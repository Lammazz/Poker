package me.lammazz.poker.client;

public class Utils {

    public static boolean arrayContains(int[] arr, int x) {
        for (int i = 0; i < arr.length; i++) if (arr[i] == x) return true;
        return false;
    }

}
