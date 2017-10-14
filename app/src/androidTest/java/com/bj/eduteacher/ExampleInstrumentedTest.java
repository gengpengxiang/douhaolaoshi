package com.bj.eduteacher;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.utils.AppUtils;
import com.bj.eduteacher.utils.LL;
import com.bj.eduteacher.utils.PreferencesUtils;

import org.junit.Test;
import org.junit.runner.RunWith;

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
}
