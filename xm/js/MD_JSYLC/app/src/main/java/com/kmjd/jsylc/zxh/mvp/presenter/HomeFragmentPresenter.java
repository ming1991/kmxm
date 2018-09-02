package com.kmjd.jsylc.zxh.mvp.presenter;



import com.kmjd.jsylc.zxh.mvp.contact.HomeFragmentContact;
import com.kmjd.jsylc.zxh.mvp.model.HomeFragmentModel;
import com.kmjd.jsylc.zxh.mvp.model.bean.IsOpenPlatfrom;
import com.kmjd.jsylc.zxh.mvp.model.bean.PlatfromIntoBean;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ufly on 2017-12-04.
 */

public class HomeFragmentPresenter implements HomeFragmentContact.Presenter {
    private HomeFragmentContact.View mView;
    private HomeFragmentModel homeFragmentModel;

    public HomeFragmentPresenter(HomeFragmentContact.View mView, HomeFragmentModel homeFragmentModel) {
        this.mView = mView;
        this.homeFragmentModel = homeFragmentModel;
        mView.setPresenter(this);
    }

    @Override

    public void onStart() {
        //doSomething
    }

    @Override
    public void setPlatfromApai9(final PlatfromIntoBean apai9, final List<String> mSports, final List<String> mPpersons, final List<String> mGames) {
        Observable.just(1)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .flatMap(new Function<Integer, ObservableSource<List<IsOpenPlatfrom>>>() {
                    @Override
                    public ObservableSource<List<IsOpenPlatfrom>> apply(Integer integer) throws Exception {
                        List<IsOpenPlatfrom> list = new ArrayList<>();
                        List<String> allM2W = new ArrayList<>();
                        allM2W.clear();
                        allM2W.addAll(apai9.getM());
                        allM2W.addAll(apai9.getW());
                        if (!allM2W.isEmpty()) {
                            if (mSports.size() > 0 && allM2W.containsAll(mSports)) {
                                list.add(new IsOpenPlatfrom(0, 0));
                            }
                            if (mPpersons.size() > 0 && allM2W.containsAll(mPpersons)) {
                                list.add(new IsOpenPlatfrom(1, 0));
                            }
                            if (allM2W.contains("f1")) {
                                list.add(new IsOpenPlatfrom(2, 0));
                            }
                            if (mGames.size() > 0 && allM2W.containsAll(mGames)) {
                                list.add(new IsOpenPlatfrom(3, 0));
                            }
                            if (allM2W.contains("g1")) {
                                list.add(new IsOpenPlatfrom(4, 0));
                            }

                        } else {
                            list.clear();
                        }
                        return Observable.just(list);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<IsOpenPlatfrom>>() {
                    @Override
                    public void accept(List<IsOpenPlatfrom> list) throws Exception {
                        mView.getPlatfromApai9(list);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.getPlatfromApai9(null);
                    }
                });
    }


    @Override
    public void destory() {

    }


}
