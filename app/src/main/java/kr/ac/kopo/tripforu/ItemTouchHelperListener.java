package kr.ac.kopo.tripforu;

public interface ItemTouchHelperListener {
    boolean onItemMove(int from_Position, int to_Position);
    void onItemSwipe(int Position);
}
