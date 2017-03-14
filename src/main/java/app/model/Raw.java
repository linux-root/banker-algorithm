package app.model;

/**
 * Created by Khanhdb@eway.vn on 3/14/17.
 */
public class Raw {

    public Raw(int m) {
        this.max = new int[m];
        this.allocation = new int[m];
    }
    public int[] max;
    public int[] allocation;
    public boolean finish;
}
