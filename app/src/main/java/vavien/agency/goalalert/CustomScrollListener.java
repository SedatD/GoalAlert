package vavien.agency.goalalert;

import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;

import vavien.agency.goalalert.fragments.Fragment_liveScores;

/**
 * Created by SD on 30.01.2018.
 * dilmacsedat@gmail.com
 * :)
 */

public class CustomScrollListener extends RecyclerView.OnScrollListener {
    public LinearLayout mLinearLayout;

    public CustomScrollListener(LinearLayout linearLayout) {
        mLinearLayout = linearLayout;
    }

    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        switch (newState) {
            case RecyclerView.SCROLL_STATE_IDLE:
                System.out.println("The RecyclerView is not scrolling");
                break;
            case RecyclerView.SCROLL_STATE_DRAGGING:
                System.out.println("Scrolling now");
                break;
            case RecyclerView.SCROLL_STATE_SETTLING:
                System.out.println("Scroll Settling");
                break;
        }
    }

    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        if (dx > 0) {
            System.out.println("Scrolled Right");
        } else if (dx < 0) {
            System.out.println("Scrolled Left");
        } else {
            System.out.println("No Horizontal Scrolled");
        }

        if (dy > 0) {
            System.out.println("Scrolled Downwards");
            Fragment_liveScores.collapse(mLinearLayout);
        } else if (dy < 0) {
            System.out.println("Scrolled Upwards");
            Fragment_liveScores.expand(mLinearLayout);
        } else {
            System.out.println("No Vertical Scrolled");
        }
    }

}
