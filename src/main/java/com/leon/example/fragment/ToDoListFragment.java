package com.leon.example.fragment;

import android.graphics.drawable.ClipDrawable;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.leon.example.DemoApp;
import com.leon.example.R;

/**
 * Created by hualu on 13-11-19.
 */
public class ToDoListFragment extends ListFragment {
    // 实验：ClipDrawable
    private static final int METRIC_BORDER_PIXEL = 60;
    private static final int SPACING_PIXEL = 20;
    private float progress = 0;
    private float rawX = 0;
    private float rawY = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.todo_list_fragment, container, false);
        final ImageView imageView = (ImageView) rootView.findViewById(R.id.emptyImage);
        imageView.setTag(false);
        // 实验：TransitionDrawable
//        final TransitionDrawable drawable = (TransitionDrawable) imageView.getDrawable();
//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (v != null) {
//                    if (!(Boolean)imageView.getTag()) {
//                        drawable.startTransition(500);
//                        imageView.setTag(true);
//                    } else {
//                        drawable.reverseTransition(500);
//                        imageView.setTag(false);
//                    }
//                }
//            }
//        });
        // 实验：ClipDrawable
        DisplayMetrics dm = getResources().getDisplayMetrics();
        final int full = dm.widthPixels - METRIC_BORDER_PIXEL;
        final ClipDrawable drawable = (ClipDrawable) imageView.getDrawable();
        imageView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        onTouchDown(v, event);
                        break;
                    }
                    case MotionEvent.ACTION_POINTER_DOWN: {
                        onTouchPointerDown(v, event);
                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        onTouchMove(v, event);
                        break;
                    }
                    case MotionEvent.ACTION_POINTER_UP: {
                        onTouchPointerUp(v, event);
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        onTouchUp(v, event);
                        break;
                    }
                    default: {
                        break;
                    }
                }
                return true;
            }

            private void onTouchUp(View v, MotionEvent event) {
                Log.d(DemoApp.LOGTAG, "TouchEvent: Touch_Up[ x= " + event.getRawX() + ", y= " + event.getRawY() + " ]");
                progress = 0;
            }

            private void onTouchPointerUp(View v, MotionEvent event) {
                Log.d(DemoApp.LOGTAG, "TouchEvent: Touch_Pointer_Up[ x= " + event.getRawX() + ", y= " + event.getRawY() + " ]");
            }


            private void onTouchMove(View v, MotionEvent event) {
                Log.d(DemoApp.LOGTAG, "TouchEvent: Touch_Move[ x= " + event.getRawX() + ", y= " + event.getRawY() + " ]");
                float nowX = event.getRawX();
                float nowY = event.getRawY();
                int ratio = 2;
                if (Math.abs(nowX - rawX) > SPACING_PIXEL) {
                    Log.d(DemoApp.LOGTAG, "move: progress=" + progress + ", full=" + full + ", abs=" + Math.abs(nowX - rawX));
                    progress = ratio * (Math.abs(nowX - rawX) / full);
                    if (progress >= 1) {
                        drawable.setLevel(10000);
                    } else {
                        drawable.setLevel((int) (progress * 10000));
                    }
                }

            }

            private void onTouchPointerDown(View v, MotionEvent event) {
                Log.d(DemoApp.LOGTAG, "TouchEvent: Touch_Pointer_Down[ x= " + event.getRawX() + ", y= " + event.getRawY() + " ]");
            }

            private void onTouchDown(View v, MotionEvent event) {
                Log.d(DemoApp.LOGTAG, "TouchEvent: Touch_Down[ x= " + event.getRawX() + ", y= " + event.getRawY() + " ]");
                rawX = event.getRawX();
                rawY = event.getRawY();
            }


        });
        return rootView;
    }


}
