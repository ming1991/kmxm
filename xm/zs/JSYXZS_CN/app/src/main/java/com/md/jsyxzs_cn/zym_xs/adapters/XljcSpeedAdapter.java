package com.md.jsyxzs_cn.zym_xs.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.md.jsyxzs_cn.zym_xs.R;
import com.md.jsyxzs_cn.zym_xs.model.NetSpeedInfo;
import com.md.jsyxzs_cn.zym_xs.utils.CommonUtils;
import com.md.jsyxzs_cn.zym_xs.utils.NetWorkUtil;
import com.md.jsyxzs_cn.zym_xs.utils.PingTestSpeedUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by androidshuai on 2016/12/30.
 */

public class XljcSpeedAdapter extends RecyclerView.Adapter<XljcSpeedAdapter.XljcSpeedViewHolder>{
    private final itemSpeedHandler mHandler;
    private Context mContext;
    private List<NetSpeedInfo> mNetSpeedInfoList;
    private NetSpeedInfo mNetSpeedInfo;

    //是否正在检查链接
    private boolean isCheckupIng;

    public XljcSpeedAdapter(Context context, List<NetSpeedInfo> netSpeedInfoList) {
        mContext = context;
        this.mNetSpeedInfoList = netSpeedInfoList;
        mHandler = new itemSpeedHandler(this);
    }

    @Override
    public XljcSpeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.xljc_speed_item, parent, false);
        LinearLayout ll_xljc_item = (LinearLayout) view.findViewById(R.id.ll_xljc_item);
        TextView tv_back_netaddress = (TextView) view.findViewById(R.id.tv_back_netaddress);
        TextView tv_order = (TextView) view.findViewById(R.id.tv_order);

        ll_xljc_item.setBackgroundResource(viewType == 0 ? R.drawable.xljc_item_shape_0 : R.drawable.xljc_item_shape_1);
        tv_back_netaddress.setTextColor(mContext.getResources().getColor(viewType == 0 ? R.color.xljc_item_number_text_color_0 : R.color.xljc_item_number_text_color_1));
        tv_order.setTextColor(mContext.getResources().getColor(viewType == 0 ? R.color.xljc_item_number_text_color_0 : R.color.xljc_item_number_text_color_1));
        return new XljcSpeedViewHolder(view,mContext, mNetSpeedInfoList);
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2 == 0 ? 0 : 1 ;
    }

    @Override
    public void onBindViewHolder(XljcSpeedViewHolder holder, int position) {
        holder.tv_order.setText(position+1+"");
        mNetSpeedInfo = mNetSpeedInfoList.get(position);
        holder.setNetSpeedInfo(mNetSpeedInfo.getNet_speed_info(),isFastPosition(position));
        holder.setNetSpeedPicture(mNetSpeedInfo.getNet_speed_info(), mNetSpeedInfo.getOld_net_speed_info());
        //holder.tv_back_netaddress.setText(mNetSpeedInfo.getNet_website_name());

        holder.setTestState(mNetSpeedInfo.getNet_is_testing(), mNetSpeedInfo.getStatus());
        holder.iv_chongce.setTag(position);
        holder.iv_chongce.setOnClickListener(mOnClickListener);
        holder.ll_xljc_item.setTag(position);
        holder.ll_xljc_item.setOnClickListener(mOnClickListener);
        //holder.iv_go.setTag(position);
        //holder.iv_go.setOnClickListener(mOnClickListener);
    }

    //判断当前的速度是不是最快的速度
    private boolean isFastPosition(int position){
        float fastSpeed = 0.0f;
        for (int i = 0; i < mNetSpeedInfoList.size(); i++) {
            float speed = Float.valueOf(mNetSpeedInfoList.get(i).getNet_speed_info());
            if (speed > fastSpeed){
                fastSpeed = speed;
            }
        }
        if (fastSpeed == 0.0f) return false;
        return fastSpeed == Float.valueOf(mNetSpeedInfoList.get(position).getNet_speed_info());
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //判断是否正在检查其他站点
            if (isCheckupIng) return;

            //判断有无网络
            if (!NetWorkUtil.checkNetwork(mContext)){
                CommonUtils.showToast(mContext,mContext.getString(R.string.nonet_lianjie));
                return;
            }
            Object tag = v.getTag();
            if (tag != null && tag instanceof Integer){
                int position = (int) tag;
                switch (v.getId()){
                    case R.id.iv_chongce:
                        //判断是否正在测速
                        if (mNetSpeedInfoList.get(position).getNet_is_testing()){
                            CommonUtils.showToast(mContext,mContext.getString(R.string.xljc_item_website_istesting));
                            break;
                        }
                        //判断网络链接是否有效
                        switch (mNetSpeedInfoList.get(position).getWebsitIsVaild()){
                            case 0:
                                //检查是否有效
                                checkURL(position, 0);
                                break;
                            case 1:
                                //该链接有效
                                testSpeed(position);
                                break;
                            case 2:
                                //该链接无效
                                CommonUtils.showToast(mContext,mContext.getString(R.string.xljc_item_website_isvaild));
                                break;
                        }
                        break;
                    case R.id.ll_xljc_item:
                        //判断网络链接是否有效
                        switch (mNetSpeedInfoList.get(position).getWebsitIsVaild()){
                            case 0:
                                //检查是否有效
                                checkURL(position, 1);
                                break;
                            case 1:
                                //该链接有效
                                NetSpeedInfo netSpeed = mNetSpeedInfoList.get(position);
                                /*Intent intent = new Intent(mContext, NetAccessActivity.class);
                                intent.putExtra(NetContants.NET_ADDRESS,netSpeed.getNet_address().replace("http://", "").replace("www.", ""));
                                mContext.startActivity(intent);*/

                                String loadUrl = "http://" + netSpeed.getNet_address().replace("http://", "").replace("https://", "").replace("www.", "").replace("/","");
                                if (!TextUtils.isEmpty(loadUrl)) {
                                    Intent webIntent = new Intent(Intent.ACTION_VIEW);
                                    webIntent.setData(Uri.parse(loadUrl));
                                    mContext.startActivity(webIntent);
                                }
                                break;
                            case 2:
                                //该链接无效
                                CommonUtils.showToast(mContext,mContext.getString(R.string.xljc_item_website_isvaild));
                                break;
                        }
                        break;
                    /*case R.id.iv_go:
                        //判断网络状态
                        if (NetWorkUtil.checkNetwork(mContext)){
                            NetSpeedInfo netSpeed = mNetSpeedInfoList.get(position);
                            Intent intent = new Intent(mContext, NetAccessActivity.class);
                            intent.putExtra(NetContants.NET_ADDRESS,netSpeed.getNet_address());
                            mContext.startActivity(intent);
                        }else {
                            CommonUtils.showToast(mContext,mContext.getString(R.string.nonet_lianjie));
                        }
                        break;*/
                }
            }
        }
    };

    //判断网络链接是否有效
    public void checkURL(int position, int action){
        //接口回调显示加载的动画
        if (null != mOnCheckupWebistIsVaildListener){
            mOnCheckupWebistIsVaildListener.checkupStart();
            isCheckupIng = true;
        }
        CommonUtils.runInThread(new websitIsVaildRuable(this, position, action));
    }

    public boolean getIsCheckupIng() {
        return isCheckupIng;
    }

    static class websitIsVaildRuable implements Runnable{
        private final WeakReference<XljcSpeedAdapter> mXljcSpeedAdapterWeakReference;
        private final WeakReference<Integer> mPositionWeakReference;
        private final WeakReference<Integer> mACtionWeakReference;

        public websitIsVaildRuable(XljcSpeedAdapter xljcSpeedAdapter, int  position, int action) {
            mXljcSpeedAdapterWeakReference = new WeakReference<>(xljcSpeedAdapter);
            mPositionWeakReference = new WeakReference<>(position);
            mACtionWeakReference = new WeakReference<>(action);
        }

        @Override
        public void run() {
            boolean isVaild = false;
            try {
                String path = "http://" + mXljcSpeedAdapterWeakReference.get().mNetSpeedInfoList.get(mPositionWeakReference.get()).getNet_address().replace("http://", "").replace("https://", "").replace("www.", "").replace("/","");
                HttpURLConnection conn = (HttpURLConnection) new URL(path).openConnection();
                //设置连接超时时间
                conn.setConnectTimeout(2000);
                conn.setReadTimeout(2000);
                int responseCode = conn.getResponseCode();
                String code = responseCode + "";
                if (code.startsWith("2") || code.startsWith("3")){
                    isVaild = true;
                }else{
                    isVaild = false;
                }
            } catch (IOException e) {
                String strException = e.toString();
                if (strException.contains("SocketTimeoutException") || strException.contains("ProtocolException")){
                    isVaild = true;
                }else {
                    isVaild = false;
                }
            }finally {
                Message message = mXljcSpeedAdapterWeakReference.get().mHandler.obtainMessage();
                message.what = WEBSITISVALID;
                message.obj = isVaild;
                message.arg1 = mPositionWeakReference.get();
                message.arg2 = mACtionWeakReference.get();
                mXljcSpeedAdapterWeakReference.get().mHandler.sendMessage(message);
            }
        }
    }

    private void testSpeed(int position) {
        if (NetWorkUtil.checkNetwork(mContext)){
            mNetSpeedInfoList.get(position).setStatus(0);
            notifyDataSetChanged();
            testPing(position);
        }else{
            CommonUtils.showToast(mContext,mContext.getString(R.string.nonet_lianjie));
        }
    }

    private static final int PINGRESULT = 0;
    private static final int PINGCONTENT = 1;
    private static final int PINGSTATUS = 2;
    private static final int WEBSITISVALID = 3;
    private static final int START = 4;
    private static final int FINAL = 5;

    static class itemSpeedHandler extends Handler{

        private final WeakReference<XljcSpeedAdapter> mXljcSpeedAdapterWeakReference;

        public itemSpeedHandler(XljcSpeedAdapter xljcSpeedAdapter) {
            mXljcSpeedAdapterWeakReference = new WeakReference<>(xljcSpeedAdapter);
        }

        @Override
        public void handleMessage(Message msg) {
            if (null != mXljcSpeedAdapterWeakReference && null != mXljcSpeedAdapterWeakReference.get()){
                int position;
                switch (msg.what){
                    case START:
                        position = msg.arg1;
                        mXljcSpeedAdapterWeakReference.get().mNetSpeedInfoList.get(position).setNet_is_testing(true);
                        mXljcSpeedAdapterWeakReference.get().mNetSpeedInfoList.get(position).setStatus(1);
                        mXljcSpeedAdapterWeakReference.get().notifyDataSetChanged();
                        mXljcSpeedAdapterWeakReference.get().isCheckupIng = false;
                        break;
                    case FINAL:
                        position = msg.arg1;
                        mXljcSpeedAdapterWeakReference.get().mNetSpeedInfoList.get(position).setNet_is_testing(false);
                        mXljcSpeedAdapterWeakReference.get().mNetSpeedInfoList.get(position).setStatus(0);
                        mXljcSpeedAdapterWeakReference.get().notifyDataSetChanged();
                        break;
                    case PINGCONTENT:
                        position = msg.arg1;
                        String content= (String) msg.obj;
                        if(content.contains("bytes from")){
                            String[] split = content.split("bytes from");
                            String pingByte = split[0].trim();
                            int pbyte = Integer.valueOf(pingByte);

                            String[] split1 = content.split("time=");
                            int msIIndex = split1[1].indexOf("ms");
                            String msStr = split1[1].substring(0, msIIndex).trim();
                            float ms = Float.valueOf(msStr);

                            float v = (pbyte / ms) * 1000/1024;
                            float speed =  (float) (Math.round(v * 10) / 10.0);
                            mXljcSpeedAdapterWeakReference.get().mNetSpeedInfoList.get(position).setNet_speed_info(speed+"");
                            mXljcSpeedAdapterWeakReference.get().notifyDataSetChanged();
                        }
                        break;
                    case PINGRESULT:
                        position = msg.arg1;
                        String pingResult= (String) msg.obj;
                        int pbyte = PingTestSpeedUtil.pingResultToByte(pingResult);
                        if (pbyte == -1){
                            break;
                        }
                        List<String> pingList = PingTestSpeedUtil.pingResultToSpeed(pingResult);
                        mXljcSpeedAdapterWeakReference.get().parserResult(pbyte,pingList,position);
                        break;
                    case WEBSITISVALID:

                        //检查网址链接完成，结束动画
                        if (null != mXljcSpeedAdapterWeakReference.get().mOnCheckupWebistIsVaildListener){
                            mXljcSpeedAdapterWeakReference.get().mOnCheckupWebistIsVaildListener.checkupEnd();
                        }

                        position = msg.arg1;
                        int action = msg.arg2;
                        boolean isVaild = (boolean) msg.obj;
                        if (isVaild){
                            //该站点有效
                            switch (action){
                                case 0:
                                    //开始测速
                                    mXljcSpeedAdapterWeakReference.get().testSpeed(position);
                                    break;
                                case 1:
                                    mXljcSpeedAdapterWeakReference.get().isCheckupIng = false;
                                    //进入到该站点
                                    //判断网络状态
                                    if (NetWorkUtil.checkNetwork(mXljcSpeedAdapterWeakReference.get().mContext)){
                                        NetSpeedInfo netSpeed = mXljcSpeedAdapterWeakReference.get().mNetSpeedInfoList.get(position);
                                    /*Intent intent = new Intent(mXljcSpeedAdapterWeakReference.get().mContext, NetAccessActivity.class);
                                    intent.putExtra(NetContants.NET_ADDRESS,netSpeed.getNet_address().replace("http://", "").replace("www.", ""));
                                    mXljcSpeedAdapterWeakReference.get().mContext.startActivity(intent);*/


                                        String loadUrl = "http://" + netSpeed.getNet_address().replace("http://", "").replace("https://", "").replace("www.", "").replace("/","");
                                        if (!TextUtils.isEmpty(loadUrl)) {
                                            Intent webIntent = new Intent(Intent.ACTION_VIEW);
                                            webIntent.setData(Uri.parse(loadUrl));
                                            mXljcSpeedAdapterWeakReference.get().mContext.startActivity(webIntent);
                                        }
                                    }else {
                                        CommonUtils.showToast(mXljcSpeedAdapterWeakReference.get().mContext,mXljcSpeedAdapterWeakReference.get().mContext.getString(R.string.nonet_lianjie));
                                    }
                                    break;
                            }
                        }else {
                            mXljcSpeedAdapterWeakReference.get().isCheckupIng = false;
                            //该站点网址无效，提示用户
                            CommonUtils.showToast(mXljcSpeedAdapterWeakReference.get().mContext,mXljcSpeedAdapterWeakReference.get().mContext.getString(R.string.xljc_item_website_isvaild));
                        }
                        //保存是否有效的状态，减少测试的次数
                        mXljcSpeedAdapterWeakReference.get().mNetSpeedInfoList.get(position).setWebsitIsVaild(isVaild ? 1:2);
                        break;
                    default:
                        super.handleMessage(msg);
                }
            }
        }
    }

    private void testPing(int position){
        CommonUtils.runInThread(new itemSpeedRunable(this, position));
    }

    static class itemSpeedRunable implements Runnable{

        private final WeakReference<XljcSpeedAdapter> mXljcSpeedAdapterWeakReference;
        private final WeakReference<Integer> mPositionWeakReference;

        public itemSpeedRunable(XljcSpeedAdapter xljcSpeedAdapter, int position) {
            mXljcSpeedAdapterWeakReference = new WeakReference<>(xljcSpeedAdapter);
            mPositionWeakReference = new WeakReference<Integer>(position);
        }

        @Override
        public void run() {
            try {
                NetSpeedInfo netSpeedInfo = mXljcSpeedAdapterWeakReference.get().mNetSpeedInfoList.get(mPositionWeakReference.get());
                String ip = netSpeedInfo.getNet_address().replace("http://", "").replace("https://", "").replace("www.", "").replace("/","");
                Process p = Runtime.getRuntime().exec("ping -c "+5+" -w 5 " + ip);

                //开始测速发送消息
                Message startMessage = mXljcSpeedAdapterWeakReference.get().mHandler.obtainMessage();
                startMessage.what=START;
                startMessage.arg1=mPositionWeakReference.get();
                mXljcSpeedAdapterWeakReference.get().mHandler.sendMessage(startMessage);

                // 读取ping的内容
                InputStream input = p.getInputStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(input));
                StringBuffer stringBuffer = new StringBuffer();
                String content = "";
                while ((content = in.readLine()) != null) {
                    //ping一次发送一次消息
                    Message message = mXljcSpeedAdapterWeakReference.get().mHandler.obtainMessage();
                    message.what=PINGCONTENT;
                    message.obj=content;
                    message.arg1=mPositionWeakReference.get();
                    mXljcSpeedAdapterWeakReference.get().mHandler.sendMessage(message);

                    content = content + "\r\n";
                    stringBuffer.append(content);
                }

                //关闭流
                in.close();

                String pingResult = stringBuffer.toString();
                Message message = mXljcSpeedAdapterWeakReference.get().mHandler.obtainMessage();
                message.what=PINGRESULT;
                message.obj=pingResult;
                message.arg1=mPositionWeakReference.get();
                mXljcSpeedAdapterWeakReference.get().mHandler.sendMessage(message);
            } catch (Exception e) {
            } finally {
                //结束测速发送消息
                Message endMessage = mXljcSpeedAdapterWeakReference.get().mHandler.obtainMessage();
                endMessage.what=FINAL;
                endMessage.arg1=mPositionWeakReference.get();
                mXljcSpeedAdapterWeakReference.get().mHandler.sendMessage(endMessage);
            }
        }
    }

    /**
     * @param pingByte
     * @param pingList
     */
    private void parserResult(final int pingByte, final List<String> pingList,int position) {
        Float[] floats = new Float[pingList.size()];
        for (int i = 0; i < pingList.size(); i++) {
            Float ms = Float.valueOf(pingList.get(i));
            floats[i]=ms;
        }
        //获取平均速度
        float v = (pingByte / PingTestSpeedUtil.getAverageSpeed(floats)) * 1000/1024;
        float speed =  (float) (Math.round(v * 10) / 10.0);
        mNetSpeedInfoList.get(position).setNet_speed_info(speed+"");
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mNetSpeedInfoList == null ? 0 : mNetSpeedInfoList.size();
    }

    private OnCheckupWebistIsVaildListener mOnCheckupWebistIsVaildListener;

    public interface OnCheckupWebistIsVaildListener{
        void checkupStart();
        void checkupEnd();
    }

    public void setOnCheckupWebistIsVaildListener(OnCheckupWebistIsVaildListener onCheckupWebistIsVaildListener){
        mOnCheckupWebistIsVaildListener = onCheckupWebistIsVaildListener;
    }

    static class XljcSpeedViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.ll_xljc_item)
        LinearLayout ll_xljc_item;
        @BindView(R.id.tv_order)
        TextView tv_order;
        @BindView(R.id.progressbar1)
        ProgressBar progressbar1;
        @BindView(R.id.progressbar2)
        ProgressBar progressbar2;
        @BindView(R.id.progressbar3)
        ProgressBar progressbar3;
        @BindView(R.id.tv_unit)
        TextView tv_unit;
        @BindView(R.id.tv_speed_int)
        TextView tv_speed_int;
        @BindView(R.id.tv_speed_float)
        TextView tv_speed_float;
        @BindView(R.id.iv_chongce)
        ImageView iv_chongce;
        /*@BindView(R.id.iv_go)
        ImageView iv_go;*/
        @BindView(R.id.tv_test_state)
        TextView tv_test_state;
        @BindView(R.id.tv_back_netaddress)
        TextView tv_back_netaddress;

        private WeakReference<Context> mContext;
        private final List<NetSpeedInfo> mNetSpeedInfoList;

        public XljcSpeedViewHolder(View itemView, Context context, List<NetSpeedInfo> netSpeedInfoList) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            this.mContext = new WeakReference<Context>(context);
            this.mNetSpeedInfoList = netSpeedInfoList;
        }

        public void setNetSpeedInfo(String net_speed_info,boolean isFast) {
            String[] split = net_speed_info.split("\\.");
            tv_speed_int.setText(split[0]+".");
            tv_speed_float.setText(split[1]);
            tv_unit.setTextColor(mContext.get().getResources().getColor(isFast ? R.color.xljc_item_fast_text_color : R.color.xljc_item_netword_speed));
            tv_speed_int.setTextColor(mContext.get().getResources().getColor(isFast ? R.color.xljc_item_fast_text_color : R.color.xljc_item_netword_speed));
            tv_speed_float.setTextColor(mContext.get().getResources().getColor(isFast ? R.color.xljc_item_fast_text_color : R.color.xljc_item_netword_speed));
        }

        public void setNetSpeedPicture(String net_speed_info, String old_net_speed_info) {
//            float net_speed = Float.valueOf(net_speed_info);
//            float old_net_speed = Float.valueOf(old_net_speed_info);
//            for (int i = 1; i <= 10; i++) {
//                dealProgress(mValueutil.evaluate(i*0.1f,old_net_speed,net_speed)+"");
//            }
            //TODO 对值进行处理
            dealProgress(net_speed_info);
        }

        private void dealProgress(String net_speed_info) {
            progressbar1.setProgress(0);
            progressbar2.setProgress(0);
            progressbar3.setProgress(0);
            float speed = Float.valueOf(net_speed_info);
            int percent = 0 ;
            if (speed <= 1.0){
                percent = (int) (speed/1.0*100);
                progressbar1.setProgress(percent);
            }else if (speed > 1.0 && speed <= 2.0){
                progressbar1.setProgress(100);
                percent = (int) ((speed-1.0)/1.0*100);
                progressbar2.setProgress(percent);
            }else if (speed > 2.0 ){
                progressbar1.setProgress(100);
                progressbar2.setProgress(100);
                percent = (int) ((speed-2.0)/1.0*100);
                if (percent > 100) percent = 100;
                progressbar3.setProgress(percent);
            }
        }

        public void setTestState(boolean flag, int status) {
//            tv_test_state.setText(status == 0 ? (flag ? mContext.getString(R.string.xljc_item_shishu) : mContext.getString(R.string.xljc_item_junshu)) : mContext.getString(R.string.xljc_item_failure));
//            tv_test_state.setText(mContext.getString(R.string.xljc_item_junshu));
//            tv_test_state.setText(flag ? mContext.get().getString(R.string.xljc_item_shishu) : mContext.get().getString(R.string.xljc_item_junshu));
            tv_test_state.setText(mContext.get().getString(R.string.xljc_item_junshu));

            //测试是旋转的补间动画
            RotateAnimation ra = new RotateAnimation(360f, 0f, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
            ra.setDuration(500);
            ra.setRepeatMode(RotateAnimation.RESTART);
            ra.setRepeatCount(RotateAnimation.INFINITE);
            ra.setInterpolator(new LinearInterpolator());
            if (status ==1 ){
                iv_chongce.startAnimation(ra);
            } else {
                iv_chongce.clearAnimation();
            }

        }
    }
}
