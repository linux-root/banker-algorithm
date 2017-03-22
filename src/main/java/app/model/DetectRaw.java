package app.model;

/**
 * Created by Khanhdb@eway.vn on 3/22/17.
 */
public class DetectRaw {

    public DetectRaw(int m) {
        this.request = new int[m];
        this.allocation = new int[m];
    }
    public int[] request;
    public int[] allocation;
    public boolean finish;
}
