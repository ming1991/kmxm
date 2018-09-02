package com.kmjd.jsylc.zxh.mvp.contact;

import com.kmjd.jsylc.zxh.mvp.model.bean.IsOpenPlatfrom;
import com.kmjd.jsylc.zxh.mvp.model.bean.PlatfromIntoBean;
import com.kmjd.jsylc.zxh.mvp.presenter.BasePresenter;
import com.kmjd.jsylc.zxh.mvp.presenter.HomeFragmentPresenter;
import com.kmjd.jsylc.zxh.mvp.view.BaseView;

import java.util.List;


public interface HomeFragmentContact {
    interface View extends BaseView<HomeFragmentPresenter> {
        void showLoadingView();

        //设置平台图标、标题、描述
       // void setPlatformInfo(PlatfromIconTitleBean dataBeanList);



        void getPlatfromApai9(List<IsOpenPlatfrom> list);
    }

    interface Presenter extends BasePresenter {


        void destory();

        //获取平台图标、标题、描述接口
     //   void getPlatfromIconTitle(String v, String ios);


        void setPlatfromApai9(PlatfromIntoBean apai9, List<String> mList, List<String> pList, List<String> gList);
    }
}
