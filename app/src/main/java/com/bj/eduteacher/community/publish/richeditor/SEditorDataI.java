package com.bj.eduteacher.community.publish.richeditor;

import android.content.Context;

import java.util.List;

/**
 * Created by hitomi on 2016/6/21.
 */
public interface SEditorDataI {

    /**
     * 根据绝对路径添加一张图片
     *
     * @param imagePath
     */
    public void addImage(Context context,String imagePath);

    /**
     * 根据图片绝对路径数组批量添加一组图片
     *
     * @param imagePaths
     */
    public void addImageArray(Context context,String[] imagePaths);

    /**
     * 根据图片绝对路径集合批量添加一组图片
     *
     * @param imageList
     */
    public void addImageList(Context context,List<String> imageList);

    /**
     * 获取标题
     *
     * @return
     */
    public String getTitleData();

    /**
     * 生成编辑数据
     */
    public List<SEditorData> buildEditData();

    /**
     * 获取当前编辑器中图片数量
     * @return
     */
    public int getImageCount();

    /**
     * 编辑器内容是否为空
     *
     * @return
     */
    public boolean isContentEmpty();

}
