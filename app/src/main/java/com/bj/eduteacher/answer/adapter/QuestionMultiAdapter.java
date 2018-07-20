package com.bj.eduteacher.answer.adapter;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.SparseBooleanArray;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bj.eduteacher.R;
import com.bj.eduteacher.answer.model.Question;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2018/7/9 0009.
 */

public class QuestionMultiAdapter extends BaseQuickAdapter<Question.DataBean.ShitiXuanxiangBean, BaseViewHolder> {


    public static List<Question.DataBean.ShitiXuanxiangBean> data = new ArrayList<>();


    public QuestionMultiAdapter(int layoutResId, @Nullable List<Question.DataBean.ShitiXuanxiangBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Question.DataBean.ShitiXuanxiangBean item) {

        helper.setText(R.id.tv_answer, item.getTitle());
        TextView tv = (TextView) helper.getView(R.id.tv_status);
        LinearLayout layout = helper.getView(R.id.layout_root);
        if (item.isSelect()) {
            tv.setBackgroundResource(R.drawable.tv_shape_broder_square_orange);
            tv.setTextColor(Color.parseColor("#FFFFFF"));
            layout.setBackgroundColor(Color.parseColor("#F1F1F1"));
        } else {
            tv.setBackgroundResource(R.drawable.tv_shape_broder_square);
            tv.setTextColor(Color.parseColor("#000000"));
            layout.setBackgroundColor(Color.WHITE);
        }

    }


    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        TextView tv =holder.getView(R.id.tv_status);
        tv.setText(numberToLetter(position+1));
    }


    //数字转字母 1-26 ： A-Z
    private String numberToLetter(int num) {
        if (num <= 0) {
            return null;
        }
        String letter = "";
        num--;
        do {
            if (letter.length() > 0) {
                num--;
            }
            letter = ((char) (num % 26 + (int) 'A')) + letter;
            num = (int) ((num - num % 26) / 26);
        } while (num > 0);

        return letter;
    }
}
