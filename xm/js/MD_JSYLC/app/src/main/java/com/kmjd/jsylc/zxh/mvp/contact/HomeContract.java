package com.kmjd.jsylc.zxh.mvp.contact;


import com.kmjd.jsylc.zxh.database.dbbean.MemberInformation;
import com.kmjd.jsylc.zxh.mvp.model.bean.FunctionIsOpenBean;
import com.kmjd.jsylc.zxh.mvp.model.bean.GiftBean;
import com.kmjd.jsylc.zxh.mvp.model.bean.MessageBean;
import com.kmjd.jsylc.zxh.mvp.model.bean.PlatfromIconTitleBean;
import com.kmjd.jsylc.zxh.mvp.model.bean.PlatfromIntoBean;
import com.kmjd.jsylc.zxh.mvp.model.bean.PrivilegeCodeBean;
import com.kmjd.jsylc.zxh.mvp.model.bean.QQCP_Bean_api10;
import com.kmjd.jsylc.zxh.mvp.model.bean.QQCP_Bean_api11;
import com.kmjd.jsylc.zxh.mvp.model.bean.UserMessageBean;
import com.kmjd.jsylc.zxh.mvp.presenter.BasePresenter;
import com.kmjd.jsylc.zxh.mvp.view.BaseView;



public interface HomeContract {
    interface HomeView extends BaseView<HomePresenter> {

        //设置会员相关功能是否开放接口
        void setFunctionIsOpenData(FunctionIsOpenBean functionIsOpenData);

        //设置礼品专区是否开启和生日礼金是否显示数据
        void setGiftData(GiftBean giftBean);

        //登出
        void logOutSuccess();

        //设置个人信息
        void setUserMemberInfo(MemberInformation information);

        //显示提交优惠代码后反馈信息
        void showPrivilegeCodeDialog(PrivilegeCodeBean privilegeCodeBean);

        //开始加载动画
        void onStartLoading();

        //停止加载动画
        void onStopLoading();

        void setPlatfromApi9(PlatfromIntoBean platfromApi9);

        //设置获取第三方带状体数据
        void setDZTData_api10(QQCP_Bean_api10[] qqcp_bean_api10s);

        //设置全球彩票额度互转的数据
        void setData_api11(QQCP_Bean_api11 qqcpData_api11);

        //帐号已经被登出
        void logoutSuccessful(String msg, String tip);

        //重新登录成功
        void loginSuccessAgain();

        //重新登录失败
        void loginFailureAgain(String failureMessage);
        void setMessageData(MessageBean messageData);
        //获取首页公告
        void setPublicNotice(UserMessageBean.ImptBean imptBean);

        //设置平台图标、标题、描述
        void setPlatformInfo(PlatfromIconTitleBean dataBeanList);

        void setMsgCount();
    }

    interface HomePresenter extends BasePresenter {
        //获取会员相关功能是否开发接口
        void getFunctionIsOpen(String verifyParm);

        //获取平台图标、标题、描述接口
        void getPlatfromIconTitle(String v, String ios);

        //获取礼品专区入口是否开启，生日礼金图表是否显示
        void getGiftIsOpen(String verifyParm);

        //登出
        void loginOut(String v);

        //查询并获取个人数据
        void getUserData();

        //获取个认讯息和公告信息
        void getMessage(String v);
        //邮件信息定时获取
        void atTime();

        //退出时取消网络
        void destory();

        //提交优惠代码
        void sendPrivilegeCode(String v, String code);

        //显示item是否维护获取期待中的数据
        void getPlatfromApi9();

        //获取第三方带状体接口数据
        void getDZTData_api10(String v);

        //获取全球彩票对应的额度互转数据
        void getData_api11(String verifyParm, String c);

        //重新登录
        void loginAgain();

        //获取数据库里的公共信息
        void getPublicMessage(UserMessageBean userMessageBean);

        //被挤下线
        void offline();
    }
}
