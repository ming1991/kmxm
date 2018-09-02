package com.kmjd.wcqp.single.zxh.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;

import com.kmjd.wcqp.single.zxh.MainActivity;
import com.kmjd.wcqp.single.zxh.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HelpFragment extends Fragment {

    @BindView(R.id.help_html)
    WebView mHelpHtml;
    @BindView(R.id.toolbar_help)
    Toolbar mToolbarHelp;
    @BindView(R.id.image_menu)
    ImageButton mImageMenu;
    private View contentView;
    private Context mContext;

    public HelpFragment() {
        // Required empty public constructor
    }

    public static HelpFragment newInstance() {
        HelpFragment fragment = new HelpFragment();
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (contentView == null) {
            contentView = inflater.inflate(R.layout.fragment_help, container, false);
        }

        ButterKnife.bind(this, contentView);
        return contentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView();
    }

    private void initView() {
        //WebView加载显示本地html文件
        mHelpHtml.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        WebSettings webSettings = mHelpHtml.getSettings();
        webSettings.setLoadWithOverviewMode(true);//设置自适应屏幕
        webSettings.setJavaScriptEnabled(true);
        mHelpHtml.loadUrl("file:///android_asset/help.html");
    }

    @OnClick(R.id.image_menu)
    public void onClick() {
        ((MainActivity) getActivity()).openDrawerLayout();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        mContext = null;
        contentView = null;
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
