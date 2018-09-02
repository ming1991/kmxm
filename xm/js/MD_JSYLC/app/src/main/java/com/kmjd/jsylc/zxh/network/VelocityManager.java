package com.kmjd.jsylc.zxh.network;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.kmjd.jsylc.zxh.MainApplication;
import com.kmjd.jsylc.zxh.R;
import com.kmjd.jsylc.zxh.listener.CallListener;
import com.kmjd.jsylc.zxh.mvp.model.bean.GameVideoBean;
import com.kmjd.jsylc.zxh.mvp.model.bean.VelocityEntity;
import com.kmjd.jsylc.zxh.ui.activity.WelcomeActivity;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;


public class VelocityManager {

    private Retrofit retrofit;
    private static volatile List<String> mURLs;
    private volatile int time;
    private static VelocityManager instance;
    public static final int TYPE_ANALYZE = 0;
    public static final int TYPE_VELOCITY = 1;
    public static final int TYPE_NOT_PERMISSION = 2;
    private static GameVideoBean mBean;

    private VelocityManager() {
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .connectTimeout(10, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl("http://baidu.com")  //随便写的地址
                .client(httpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        mURLs = new ArrayList<>();
    }

    public static VelocityManager getInstance() {
        if (instance == null) {
            synchronized (VelocityManager.class) {
                if (instance == null) {
                    instance = new VelocityManager();
                }
            }
        }
        return instance;
    }

    /**
     * <p>parseGameXML</p>
     *
     * @Description 解析xml
     */
    public synchronized void parseGameXML(final CallListener.OnVelocityListener listener) {
        if(ContextCompat.checkSelfPermission(MainApplication.applicationContext,  Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            listener.onVelocityFailed(TYPE_NOT_PERMISSION, MainApplication.applicationContext.getResources().getString(R.string.insufficient_privilege));
            return;
        }
        retrofit.create(VelocityService.class)
                .parseXML(WelcomeActivity.DOMAIN.get(0).equals(AllAPI.DOMAIN_NAME_CN_REAL[0]) ? AllAPI.PARSEURL_REAL : AllAPI.PARSEURL_TEST)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.i("zyf", "onSubscribe");
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        Log.i("zyf", "onNext");
                        mBean = new GameVideoBean();
                        try {
                            mBean = parseXML(responseBody.byteStream());
                            //1.解析成功
                            listener.onAnalyzeSuccess(mBean);
                            //2.测速
                            velocityNetwork(mBean, listener);
                        } catch (Exception e) {
                            e.printStackTrace();
                            listener.onVelocityFailed(TYPE_ANALYZE, e.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i("zyf", "onError");
                        listener.onVelocityFailed(TYPE_ANALYZE, e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.i("zyf", "onComplete");
                    }
                });
    }

    /**
     * <p>parseXML</p>
     *
     * @param is
     * @return
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    private GameVideoBean parseXML(InputStream is) throws ParserConfigurationException, IOException, SAXException {
        GameVideoBean mBean = new GameVideoBean();
        //使用Pull解析
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        Document doc = builderFactory.newDocumentBuilder().parse(is);

        //  1.获取XcVideo节点
        NodeList xcVideos = doc.getElementsByTagName("XcVideo");
        Element xcVideo = (Element) xcVideos.item(0);

        //  2.解析OTHER_Line里面的节点
        NodeList node_other = xcVideo.getElementsByTagName("OTHER_Line");
        Element OTHER_Line = (Element) node_other.item(0);
        String otherType = OTHER_Line.getAttribute("type");
        String otherFolder = OTHER_Line.getAttribute("folder");
        NodeList other_v = OTHER_Line.getElementsByTagName("V");
        List<String> otherUrls = new ArrayList<>();
        for (int n = 0; other_v.getLength() > n; n++) {
            otherUrls.add(other_v.item(n).getTextContent());

        }
        GameVideoBean.OtherLineBean otherBean = mBean.new OtherLineBean(otherType, otherFolder, otherUrls);
        mBean.setOtherLineBean(otherBean);

        //  3.解析SPARE_Line里面的节点
        NodeList node_space = xcVideo.getElementsByTagName("SPARE_Line");
        Element SPACE_Line = (Element) node_space.item(0);
        String space_type = SPACE_Line.getAttribute("type");
        String space_folder = SPACE_Line.getAttribute("folder");
        NodeList space_v = SPACE_Line.getElementsByTagName("V");
        List<String> space_line_list = new ArrayList<>();
        for (int n = 0; n < space_v.getLength(); n++) {
            space_line_list.add(space_v.item(n).getTextContent());
        }
        GameVideoBean.SpareLineBean spareBean = mBean.new SpareLineBean(space_type, space_folder, space_line_list);
        mBean.setSpareLineBean(spareBean);

        // 3.获取GameTypePrames节点
        NodeList gameTypePrames = doc.getElementsByTagName("GameTypePrames");
        Element gameTypePrame = (Element) gameTypePrames.item(0);

        // 4.解析GameTypePrames里面的节点
        List<GameVideoBean.TypeBean> typeBeanList = new ArrayList<>();
        NodeList node_Type = gameTypePrame.getElementsByTagName("Type");
        for (int i = 0; i < node_Type.getLength(); i++) {
            Element item = (Element) node_Type.item(i);
            String name = item.getAttribute("name");
            String liveFQ = item.getAttribute("liveFQ");
            GameVideoBean.TypeBean typeBean = mBean.new TypeBean();
            typeBean.setName(name);
            List<GameVideoBean.TypeBean.FNbean> fNbeans = new ArrayList<>();
            String[] gameFlag = liveFQ.split("[|][|]");
            for (String aGameFlag : gameFlag) {
                String liveXc = aGameFlag.split(",")[1];//流名
                liveXc = liveXc.substring(0, liveXc.length() - 1) + "2";
                String folder = aGameFlag.split(",")[0];//folder
                GameVideoBean.TypeBean.FNbean fNbean = typeBean.new FNbean();
                fNbean.setLive(liveXc);
                fNbean.setFolder(folder);
                fNbeans.add(fNbean);
            }
            typeBean.setLiveFQ(fNbeans);
            typeBeanList.add(typeBean);
            Log.d("tast", fNbeans.toString());
        }
        mBean.setTypeBeanList(typeBeanList);
        return mBean;
    }

    /**
     * <p>velocityNetwork</p>
     *
     * @param bean
     * @param listener
     * @Description 网络测速
     */
    private void velocityNetwork(final GameVideoBean bean, final CallListener.OnVelocityListener listener) {
        mURLs.clear();
        String otherType = mBean.getOtherLineBean().getType();
        int size = bean.getSpareLineBean().getvBean().size();
        List<String> velocityURLS = bean.getSpareLineBean().getvBean();
        List<String> otherURLs = bean.getOtherLineBean().getV();
        int otherSize = otherURLs.size();
        //其他线路folder不等于CDN情况
        if (!otherType.equals("CDN")) {
            size += otherSize;
            velocityURLS.addAll(otherURLs);
        }
        time = size;
        final List<String> eUrls = new ArrayList<>();
        final List<VelocityEntity> mVelocityList = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            final int pos = i;
            retrofit.create(VelocityService.class)
                    .download("http://" + velocityURLS.get(i) + "/200k.txt")
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .doOnNext(new Consumer<ResponseBody>() {
                        @Override
                        public void accept(ResponseBody responseBody) throws Exception {
                            long startTime = System.currentTimeMillis();
                            if(writeToFile(responseBody.byteStream(), pos)){
                                long lastTime = System.currentTimeMillis() - startTime;
                                mVelocityList.add(new VelocityEntity(bean.getSpareLineBean().getvBean().get(pos), lastTime));
                                //用于最后结束比较
                                mURLs.add(bean.getSpareLineBean().getvBean().get(pos));
                            }else{
                                eUrls.add(bean.getSpareLineBean().getvBean().get(pos));
                                --time;
                            }
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ResponseBody>() {

                        @Override
                        public void onError(Throwable e) {
                            //此处还要往eUrls里面添加数据是为了做判断用
                            eUrls.add(bean.getSpareLineBean().getvBean().get(pos));
                            --time;

                            if (time != mURLs.size()) {
                                return;
                            }
                          /*  if (mURLs.size() == 0) {
                                listener.onVelocityFailed(TYPE_VELOCITY, e.getMessage());
                            } else {*/
                            sortByTime(mVelocityList);
                            mURLs.addAll(eUrls);
                            listener.onVelocitySuccess(mURLs);
                            // }
                        }

                        @Override
                        public void onComplete() {

                        }

                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(ResponseBody responseBody) {

                            if (mURLs.size() == time) {
                                sortByTime(mVelocityList);
                                mURLs.addAll(eUrls);
                                listener.onVelocitySuccess(mURLs);
                            }
                        }
                    });
        }
    }

    /**
     * <p>writeToFile</p>
     * @param iStream
     * @Description 将数据写入本地文件
     */
    private boolean writeToFile(InputStream iStream, int postition) {
        byte[] buffer = new byte[1024];
        int len = 0;
        FileOutputStream fos = null;
        File saveDir = new File(Environment.getExternalStorageDirectory().getPath() + "/kmjd/" + postition + "/");
        if (!saveDir.exists()) {
            saveDir.mkdirs(); //mkdirs创建多级文件夹， mkdir只创建一级
        }
        File file = new File(saveDir + File.separator + "200k.txt");
        try {
            if(!file.exists())
                file.createNewFile();
            fos = new FileOutputStream(file);
            while ((len = iStream.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }finally {
            try {
                if(iStream != null)
                    iStream.close();
                if(fos != null)
                    fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        file.delete();
        return true;
    }

    private void sortByTime(List<VelocityEntity> mList){
        mURLs.clear();
        Collections.sort(mList, new Comparator<VelocityEntity>() {
            @Override
            public int compare(VelocityEntity p1, VelocityEntity p2) {
                long p1time = p1.getLastTime();
                long p2Time = p2.getLastTime();
                if(p1time>p2Time){
                    return 1;
                } else if(p1time==p2Time){
                    return 0;
                }else{
                    return -1;
                }
            }
        });
        for(VelocityEntity entity: mList){
            mURLs.add(entity.getUrl());
        }
    }

    /**
     * <p>isOk</p>
     * @return
     */
    public boolean isOk(){
        if (mURLs == null || mBean == null) {
            return false;
        }
        return true;
    }

    /**
     * <p>getRTMPUrl</p>
     *
     * @param name
     * @param tableNum
     * @return 返回完整RTMP流地址
     */
    public  List<String> getRTMPUrl(String name, int tableNum) {
            --tableNum;
        //判断解析对象和测速排序集合是否为空
        if (mURLs == null || mBean == null) {
            return null;
        }

        List<String> rtmpList = new ArrayList<>();
        String spareFolder = mBean.getSpareLineBean().getFolder();
        String cdnFolder = mBean.getOtherLineBean().getFolder();
        String otherType = mBean.getOtherLineBean().getType();
        List<String> cdnUrls = mBean.getOtherLineBean().getV();


        GameVideoBean.TypeBean.FNbean fNbean;
        String liveXc = "";
        List<GameVideoBean.TypeBean> mTypeList = mBean.getTypeBeanList();
        //判断房间类型是否为空
        if (mTypeList == null) {
            return null;
        }
        for (GameVideoBean.TypeBean typeBean : mTypeList) {
            if (typeBean.getName().equals(name)) {
                int size = typeBean.getLiveFQ().size();
                if (0 <= tableNum && tableNum <= size) {
                    fNbean = typeBean.getLiveFQ().get(tableNum);
                } else {
                    fNbean = typeBean.getLiveFQ().get(0);
                }

                String folder = fNbean.getFolder();

                //备用线路folder为空
                /*if (TextUtils.isEmpty(spareFolder)) {
                    spareFolder = fNbean.getFolder();
                }*/
                //其他线路folder为空
                if (folder.equals("live")) {
                    if(!TextUtils.isEmpty(cdnFolder))
                        cdnFolder = folder;

                    if(!TextUtils.isEmpty(spareFolder))
                        spareFolder = folder;
                }
                liveXc = fNbean.getLive();
            }
        }
        //1.添加cdn完整rtmp路径
        //情况一：other线路不为空,folder是CDN
        int otherSize = cdnUrls.size();
        if (otherType.equals("CDN") && otherSize > 0) {
            for (String otherURL : cdnUrls) {
                if(TextUtils.isEmpty(cdnFolder)){
                    rtmpList.add("rtmp://" + otherURL + "/" + liveXc);
                }else{
                    rtmpList.add("rtmp://" + otherURL + "/" + cdnFolder + "/" + liveXc);
                }
            }
        }

        //2.循环添加测速后的url集合的完整rtmp路径
        for (int i = 0; i < mURLs.size(); i++) {
            String velUrl = mURLs.get(i);
            //情况二：other线路为空，测速集合只有备用线路
            if (otherSize <= 0) {
                if (TextUtils.isEmpty(spareFolder)){
                    rtmpList.add("rtmp://" + mURLs.get(i) + "/" + liveXc);
                }else{
                    rtmpList.add("rtmp://" + mURLs.get(i) + "/" + spareFolder + "/" + liveXc);
                }
                continue;
            }

            for (String url : cdnUrls) {
                if (velUrl.equals(url)) {
                    //情况三：other线路中的url出现在测速URL集合中
                    if(TextUtils.isEmpty(cdnFolder)){
                        rtmpList.add("rtmp://" + url + "/" + liveXc);
                    }else{
                        rtmpList.add("rtmp://" + url + "/" + cdnFolder + "/" + liveXc);
                    }
                } else {
                    //情况四：普通的备用线路url完整rtmp路径
                    if (TextUtils.isEmpty(spareFolder)){
                        rtmpList.add("rtmp://" + mURLs.get(i) + "/" + liveXc);
                    }else{
                        rtmpList.add("rtmp://" + mURLs.get(i) + "/" + spareFolder + "/" + liveXc);
                    }
                }
            }
        }
        return rtmpList;
    }
}
