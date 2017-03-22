package io.x10.vertx;

/**
 * Created by Khanhdb@eway.vn on 3/14/17.
 */
public class VectorCalculate {

    public static boolean notGreater (int[] need, int[] work) {
        int lenth = need.length;
        for(int i = 0; i < lenth; i++){
            if (need[i] > work[i]) return false;
        }
        return true;
    }

    public static int[] subtract(int[] a, int[] b) {
        int length = a.length;
        int[] result = new int[length];
        for (int i = 0; i < length; i++) {
            result[i] = a[i] - b[i];
        }
        return result;
    }

    public static int[] plus(int[] a, int[] b){
        int length = a.length;
        int[] result = new int[length];
        for(int i = 0; i < length; i++){
            result[i] = a[i] + b[i];
        }
        return result;
    }
    public static boolean isZero(int[] a) {
        int length = a.length;
        for (int i = 0; i < length; i++) {
            if (a[i] != 0) return false;
        }
        return true;
    }
}
