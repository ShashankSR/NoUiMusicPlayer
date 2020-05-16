package mediaplayer.example.com.test;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

/**
 * Created by shashank on 3/21/2015.
 */
public class MyTouchListener implements View.OnTouchListener {

    private Context mycontext = null;
    public MyTouchListener(Context context){
        this.mycontext=context;
    }
    private final GestureDetector gestureDetector = new GestureDetector(mycontext,new GestureListener());

    public boolean onTouch(final View v, final MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 50;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;
        private int volume = 0;


        public boolean onScroll(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY){

            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) < Math.abs(diffY)) {
                        if (diffY <  0) {
                            if(volume<100)
                                volume++;
                          //  Log.d("     Swiped top : Volume : "+volume,"mydebug");
                            result = onSwipeTop();
                        } else if(diffY> 0){
                            if(volume>0)
                                volume--;
                        //    Log.d(" Swiped bottom : Volume : "+volume,"mydebug");
                            result = onSwipeBottom();
                        }
                }else if (Math.abs(diffX) > Math.abs(diffY)) {

                        if (diffX > 0) {
                                result = onScrollRight();
                        } else {
                                result = onScrollLeft();
                        }

                }
            }catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }


        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            if(e1.getAction() == MotionEvent.ACTION_POINTER_DOWN){
                Log.d("Double swipe ","mydebug");
            }
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {

                                result = onSwipeRight();
                        } else {

                                result = onSwipeLeft();
                        }
                    }
                }
//                else {
//                    if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
//                        if (diffY > 0) {
//                            result = onSwipeBottom();
//                        } else {
//                            result = onSwipeTop();
//                        }
//                    }
//                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }

    }

    public boolean myLongPress() {
        return false;
    }
    public boolean onSwipeRight() {
        return false;
    }

    public boolean onSwipeLeft() {
        return false;
    }

    public boolean onSwipeTop() {
        return false;
    }

    public boolean onSwipeBottom() {
        return false;
    }

    public boolean onScrollRight(){
        Toast.makeText(mycontext, "Double swipe right",
                Toast.LENGTH_SHORT).show();
        return false;
    }

    public boolean onScrollLeft(){
        Toast.makeText(mycontext, "Double swipe left",
                Toast.LENGTH_SHORT).show();
        return false;
    }

}

