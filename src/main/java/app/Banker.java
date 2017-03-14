package app;

import app.model.Raw;
import io.x10.vertx.VectorCalculate;
import java.util.ArrayList;

/**
 * Created by Khanhdb@eway.vn on 3/14/17.
 */
public class Banker {

    private static ArrayList result = new ArrayList<int[]>(); // lưu các kết quả
    public static ArrayList<int[]> checkSafeStatus (int m, int n, int[] allocation, int[] max, int[] available) {

        int[] series = new int[n]; // lưu 1 chuỗi tiến trình
        Raw[] matrixes = new Raw[n];

        int k = 0;
        for (int i = 0; i < n; i++){
            matrixes[i] = new Raw(m);
            for (int j = 0; j < m; j++){
                matrixes[i].allocation[j] = allocation[k];
                matrixes[i].max[j] = max[k];
                k++;
            }
        }

        int[] work = available;
        step2(matrixes, work, 0, series, n);
        return result;
    }

    static private void step2 (Raw[] matrixes, int[] work, int h, int[] series, int n) {
        if (h == n) {
            int[] seriesClone = new int[n];
            for (int i = 0;i < n; i++) {
                seriesClone[i] = series[i];
            }
            result.add(seriesClone);
            matrixes[series[h - 1]].finish = false;
            return;
        }
        for (int i = 0; i < n; i++) {
            int[] need = VectorCalculate.subtract(matrixes[i].max, matrixes[i].allocation);
            if (VectorCalculate.notGreater(need, work) && !matrixes[i].finish) {
                matrixes[i].finish = true;
                series[h] = i;
                step2(matrixes, VectorCalculate.plus(work, matrixes[i].allocation), h + 1, series, n);
            }
        }
        if (h >= 1) {
            matrixes[series[h - 1]].finish = false;
        }
        return;
    }
}
