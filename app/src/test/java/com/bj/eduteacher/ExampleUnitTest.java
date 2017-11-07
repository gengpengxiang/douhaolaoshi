package com.bj.eduteacher;

import com.bj.eduteacher.tool.ShowNameUtil;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void add_isequal() throws Exception {
        System.out.println(4 == 2 + 2);
    }

    @Test
    public void add_threeString() throws Exception {
        String result = ShowNameUtil.getFirstNotNullParams("", "", "null", "zhangheng", "haode", "191919191919");
        System.out.println("运行结果: " + result);
    }

    public void add_testBase64() throws Exception {
        String s = "";
    }
}