package com.kmjd.wcqp.single.zxh.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.github.chrisbanes.photoview.PhotoView;
import com.kmjd.wcqp.single.zxh.R;
import com.kmjd.wcqp.single.zxh.util.EasyTransition;

public class PhotoActivity extends AppCompatActivity {
    private boolean finishEnter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        initView();

        long transitionDuration = 500;
        if (null != savedInstanceState){
            transitionDuration = 0;
        }

        //trsnsition enter
        finishEnter = false;
        EasyTransition.enter(this,
                transitionDuration,
                new DecelerateInterpolator(),
                new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        finishEnter = true;
                    }
                });
    }

    private void initView() {
        PhotoView photoView3 = (PhotoView) findViewById(R.id.photoview3);
        PhotoView photoView4 = (PhotoView) findViewById(R.id.photoview4);
        photoView3.setOnClickListener(mOnClickListener);
        photoView4.setOnClickListener(mOnClickListener);
        String guide = getIntent().getStringExtra("guide");
        switch (guide){
            case "guide3":
                photoView3.setVisibility(View.VISIBLE);
                photoView4.setVisibility(View.GONE);
                break;
            case "guide4":
                photoView3.setVisibility(View.GONE);
                photoView4.setVisibility(View.VISIBLE);
                break;
        }
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (finishEnter){
                finishEnter = false;
                EasyTransition.exit(PhotoActivity.this, 300, new DecelerateInterpolator());
            }
        }
    };

    @Override
    public void onBackPressed() {
        if (finishEnter){
            finishEnter = false;
            EasyTransition.exit(PhotoActivity.this, 300, new DecelerateInterpolator());
        }
    }
}
