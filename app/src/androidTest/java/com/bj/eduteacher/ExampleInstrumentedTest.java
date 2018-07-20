package com.bj.eduteacher;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.bj.eduteacher.api.LmsDataService;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.entity.ArticleInfo;
import com.bj.eduteacher.utils.AppUtils;
import com.bj.eduteacher.utils.LL;
import com.bj.eduteacher.utils.PreferencesUtils;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function7;
import io.reactivex.schedulers.Schedulers;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        LL.i(appContext.getPackageName());
        PreferencesUtils.putString(appContext, MLProperties.PREFER_KEY_VERSION_CODE, AppUtils.getVersionName(appContext));
        LL.i("当前版本号：" + PreferencesUtils.getString(appContext, MLProperties.PREFER_KEY_VERSION_CODE));
        LL.i("删除所有Preferences数据");
        PreferencesUtils.cleanAllData(appContext);
        LL.i("测试删除版本号：" + PreferencesUtils.getString(appContext, MLProperties.PREFER_KEY_VERSION_CODE));
    }

    @Test
    public void testAPI() {
        final LmsDataService mService = new LmsDataService();
        // 获取专家卡片列表
        Observable<List<ArticleInfo>> observable1 = Observable.create(new ObservableOnSubscribe<List<ArticleInfo>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<ArticleInfo>> e) throws Exception {
                try {
                    List<ArticleInfo> dataList = mService.getMasterCardsFromAPI(1, 5);
                    if (dataList.size() >= 5) {
                        ArticleInfo articleInfo = mService.getMasterCountFromAPI();
                        int count = Integer.parseInt(articleInfo.getReplyCount());
                        if (count > 5) {    // 超过5位专家，显示查看全部
                            dataList.add(new ArticleInfo("查看全部" + count + "位专家", ArticleInfo.SHOW_TYPE_ZHUANJIA_ALL));
                        }
                    }
                    if (!e.isDisposed()) {
                        e.onNext(dataList);
                        e.onComplete();
                    }
                } catch (InterruptedIOException ex) {
                    if (!e.isDisposed()) {
                        e.onError(ex);
                        return;
                    }
                } catch (InterruptedException ex) {
                    if (!e.isDisposed()) {
                        e.onError(ex);
                        return;
                    }
                }
            }
        }).subscribeOn(Schedulers.io());

        // 获取逗课列表
        Observable<List<ArticleInfo>> observable2 = Observable.create(new ObservableOnSubscribe<List<ArticleInfo>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<ArticleInfo>> e) throws Exception {
                try {
                    List<ArticleInfo> dataList = mService.getDouKeListFromAPI(1, "18988888888");
                    if (!e.isDisposed()) {
                        e.onNext(dataList);
                        e.onComplete();
                    }
                } catch (InterruptedIOException ex) {
                    if (!e.isDisposed()) {
                        e.onError(ex);
                        return;
                    }
                } catch (InterruptedException ex) {
                    if (!e.isDisposed()) {
                        e.onError(ex);
                        return;
                    }
                }
            }
        }).subscribeOn(Schedulers.io());

        // 获取专家日课
        Observable<List<ArticleInfo>> observable3 = Observable.create(new ObservableOnSubscribe<List<ArticleInfo>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<ArticleInfo>> e) throws Exception {
                try {
                    List<ArticleInfo> dataList = mService.getMasterRikeFromAPI("18988888888", 9);
                    if (!e.isDisposed()) {
                        e.onNext(dataList);
                        e.onComplete();
                    }
                } catch (InterruptedIOException ex) {
                    if (!e.isDisposed()) {
                        e.onError(ex);
                        return;
                    }
                } catch (InterruptedException ex) {
                    if (!e.isDisposed()) {
                        e.onError(ex);
                        return;
                    }
                }
            }
        }).subscribeOn(Schedulers.io());

        // 获取每日一课列表
        Observable<List<ArticleInfo>> observable4 = Observable.create(new ObservableOnSubscribe<List<ArticleInfo>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<ArticleInfo>> e) throws Exception {
                try {
                    List<ArticleInfo> dataList = mService.getHomePageLatestRes("18988888888");
                    if (!e.isDisposed()) {
                        e.onNext(dataList);
                        e.onComplete();
                    }
                } catch (InterruptedIOException ex) {
                    if (!e.isDisposed()) {
                        e.onError(ex);
                        return;
                    }
                } catch (InterruptedException ex) {
                    if (!e.isDisposed()) {
                        e.onError(ex);
                        return;
                    }
                }
            }
        }).subscribeOn(Schedulers.io());

        // 获取名师卡片列表
        Observable<List<ArticleInfo>> observable5 = Observable.create(new ObservableOnSubscribe<List<ArticleInfo>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<ArticleInfo>> e) throws Exception {
                try {
                    List<ArticleInfo> dataList = mService.getFamousTeacherCardsFromAPI(1, 10);
                    if (dataList.size() >= 10) {
                        ArticleInfo articleInfo = mService.getFamousTeacherCountFromAPI();
                        int count = Integer.parseInt(articleInfo.getReplyCount());
                        if (count > 10) {    // 超过8位名师，显示查看全部
                            dataList.add(new ArticleInfo("查看全部" + count + "位名师", ArticleInfo.SHOW_TYPE_ZHUANJIA_ALL));
                        }
                    }
                    if (!e.isDisposed()) {
                        e.onNext(dataList);
                        e.onComplete();
                    }
                } catch (InterruptedIOException ex) {
                    if (!e.isDisposed()) {
                        e.onError(ex);
                        return;
                    }
                } catch (InterruptedException ex) {
                    if (!e.isDisposed()) {
                        e.onError(ex);
                        return;
                    }
                }
            }
        }).subscribeOn(Schedulers.io());

        // 获取名师成长课程列表
        Observable<List<ArticleInfo>> observable6 = Observable.create(new ObservableOnSubscribe<List<ArticleInfo>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<ArticleInfo>> e) throws Exception {
                try {
                    List<ArticleInfo> dataList = mService.getHomePageCourseList("18988888888");
                    if (!e.isDisposed()) {
                        e.onNext(dataList);
                        e.onComplete();
                    }
                } catch (InterruptedIOException ex) {
                    if (!e.isDisposed()) {
                        e.onError(ex);
                        return;
                    }
                } catch (InterruptedException ex) {
                    if (!e.isDisposed()) {
                        e.onError(ex);
                        return;
                    }
                }
            }
        }).subscribeOn(Schedulers.io());

        // 获取正在直播的列表
        Observable<List<ArticleInfo>> observable7 = Observable.create(new ObservableOnSubscribe<List<ArticleInfo>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<ArticleInfo>> e) throws Exception {
                try {
                    List<ArticleInfo> liveList = mService.getLiveListFromAPI("18988888888", "1", 2, 0);
                    if (!e.isDisposed()) {
                        e.onNext(liveList);
                        e.onComplete();
                    }
                } catch (InterruptedIOException ex) {
                    if (!e.isDisposed()) {
                        e.onError(ex);
                        return;
                    }
                } catch (InterruptedException ex) {
                    if (!e.isDisposed()) {
                        e.onError(ex);
                        return;
                    }
                }
            }
        }).subscribeOn(Schedulers.io());

        Observable.zip(observable1, observable2, observable3, observable4, observable5, observable6, observable7
                , new Function7<List<ArticleInfo>, List<ArticleInfo>, List<ArticleInfo>, List<ArticleInfo>, List<ArticleInfo>, List<ArticleInfo>, List<ArticleInfo>, List<ArticleInfo>>() {
                    @Override
                    public List<ArticleInfo> apply(@NonNull List<ArticleInfo> data1, @NonNull List<ArticleInfo> data2, @NonNull List<ArticleInfo> data3, @NonNull List<ArticleInfo> data4, @NonNull List<ArticleInfo> data5, @NonNull List<ArticleInfo> data6, @NonNull List<ArticleInfo> data7) throws Exception {
                        List<ArticleInfo> dataList = new ArrayList<>();
                        if (data4.size() > 0) {
                            dataList.add(new ArticleInfo("每日一课", ArticleInfo.SHOW_TYPE_DECORATION));
                            dataList.addAll(data4);
                        }
                        if (data7.size() > 0) {
                            dataList.add(new ArticleInfo("正在直播", ArticleInfo.SHOW_TYPE_DECORATION));
                            dataList.addAll(data7);
                            dataList.add(new ArticleInfo("查看全部直播", ArticleInfo.SHOW_TYPE_ZHUANJIA_ALL));
                        }
                        if (data6.size() > 0) {
                            dataList.add(new ArticleInfo("名师成长课程", ArticleInfo.SHOW_TYPE_DECORATION));
                            dataList.addAll(data6);
                        }
                        if (data3.size() > 0) {
                            dataList.add(new ArticleInfo("优课精选", ArticleInfo.SHOW_TYPE_DECORATION));
                            dataList.addAll(data3);
                            dataList.add(new ArticleInfo("查看全部", ArticleInfo.SHOW_TYPE_ZHUANJIA_ALL));
                        }
                        if (data1.size() > 0) {
                            dataList.add(new ArticleInfo("专家驻场", ArticleInfo.SHOW_TYPE_DECORATION));
                            dataList.addAll(data1);
                        }
                        if (data5.size() > 0) {
                            dataList.add(new ArticleInfo("名师堂", ArticleInfo.SHOW_TYPE_DECORATION));
                            dataList.addAll(data5);
                        }
                        if (data2.size() > 0) {
                            dataList.add(new ArticleInfo("干货精选", ArticleInfo.SHOW_TYPE_DECORATION));
                            dataList.addAll(data2);
                        }
                        return dataList;
                    }
                })
                .observeOn(Schedulers.io())
                .subscribe(new Observer<List<ArticleInfo>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull List<ArticleInfo> result) {
                        System.out.println("获取数据成功");
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        System.out.println(e.getMessage());
                        System.out.println("获取数据失败");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
