package com.bj.eduteacher.answer.view;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bj.eduteacher.BaseFragment;
import com.bj.eduteacher.R;
import com.bj.eduteacher.answer.adapter.QuestionMultiAdapter;
import com.bj.eduteacher.answer.adapter.QuestionSingleAdapter;
import com.bj.eduteacher.answer.dbhelper.AnswerDao;
import com.bj.eduteacher.answer.dbhelper.GreenDaoHelper;
import com.bj.eduteacher.answer.model.Answer;
import com.bj.eduteacher.answer.model.Question;
import com.bj.eduteacher.answer.presenter.AnswerDetailPresenter;
import com.bj.eduteacher.entity.MsgEvent;
import com.bj.eduteacher.utils.StringUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2018/7/9 0009.
 */

public class AnswerDetailFragment extends BaseFragment implements IViewAnswerDetail {


    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.tv_num)
    TextView tvNum;
    @BindView(R.id.tv_type)
    TextView tvType;
    @BindView(R.id.tv_content)
    TextView tvContent;
    Unbinder unbinder;

    private QuestionMultiAdapter multiAdapter;
    private QuestionSingleAdapter singleAdapter;

    private List<Question.DataBean.ShitiXuanxiangBean> mDataList = new ArrayList<>();

    private AnswerDetailPresenter presenter;
    private String examid;
    private int page;
    private String type;//单选还是多选
    public  HashSet<String> positionSet = new HashSet<>();
    private String shiti_id,shiti_ordernum;
    private List<String> answerPos = new ArrayList<>();

    private List<Answer> answerList = new ArrayList<>();
    private AnswerDao mAnswerDao;
    private String singleContent,multiContent;

    Bundle savedState;

    @Override
    protected View loadViewLayout(LayoutInflater inflater, ViewGroup container) {

        Log.e("fragment","loadViewLayout");

        View view = inflater.inflate(R.layout.fragment_question, container, false);
        unbinder = ButterKnife.bind(this, view);

        presenter = new AnswerDetailPresenter(getActivity(), this);

        examid = getActivity().getIntent().getStringExtra("examid");

        Bundle bundle = getArguments();
        page = bundle.getInt("page");
        presenter.getAnswerList(examid, String.valueOf(page));
        initViews();

        mAnswerDao = GreenDaoHelper.getDaoSession().getAnswerDao();

        return view;
    }

    private void initViews() {
        singleAdapter = new QuestionSingleAdapter(R.layout.recycler_item_question, mDataList);
        multiAdapter = new QuestionMultiAdapter(R.layout.recycler_item_question, mDataList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        singleAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter0, View view, int position) {
                singleAdapter.setSelection(position);
                singleContent = mDataList.get(position).getId();

                mAnswerDao.insertOrReplace(new Answer((long)Integer.valueOf(shiti_id),Integer.valueOf(shiti_id),singleContent,page));
            }
        });

        multiAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Question.DataBean.ShitiXuanxiangBean bean = mDataList.get(position);
                boolean isSelect = bean.isSelect();

                if(!isSelect){
                    bean.setSelect(true);
                    positionSet.add(bean.getId());

                }else {
                    bean.setSelect(false);
                    positionSet.remove(bean.getId());
                }

                answerPos.clear();
//                for(int i:positionSet){
//                    answerPos.add(positionSet.);
//                }

                Iterator<String> iterator=positionSet.iterator();
                while(iterator.hasNext()){
                    answerPos.add(iterator.next());
                }
                //去掉数组首尾的符号[]
                multiContent = StringUtils.trimFirstAndLastChar(constructNewsContent(answerPos));
                String string= multiContent.replace("\"", "");
               // multiContent = constructNewsContent(answerPos);
                Log.e("选中",string);

                mAnswerDao.insertOrReplace(new Answer((long)Integer.valueOf(shiti_id),Integer.valueOf(shiti_id),string,page));
                multiAdapter.notifyDataSetChanged();

            }
        });
    }

    public String constructNewsContent(List<String> content) {
        return JSON.toJSONString(content);
    }

    @Override
    protected void bindViews(View view) {

    }

    @Override
    protected void processLogic() {

    }

    @Override
    protected void setListener() {

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("fragment","onResume");
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        saveStateToArguments();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveStateToArguments();
    }

    private void saveStateToArguments() {
        savedState = saveState();
        if (savedState != null) {
            Bundle b = getArguments();
            b.putBundle("data", savedState);
        }
    }
    private boolean restoreStateFromArguments() {
        Bundle b = getArguments();
        savedState = b.getBundle("data");
        if (savedState != null) {
            restoreState();
            return true;
        }
        return false;
    }
    /////////////////////////////////
// 取出状态数据
/////////////////////////////////
    private void restoreState() {
        if (savedState != null) {
            //比如
            //tv1.setText(savedState.getString(“text”));
        }
    }
    //////////////////////////////
// 保存状态数据
//////////////////////////////
    private Bundle saveState() {
        Bundle state = new Bundle();
        // 比如
        //state.putString(“text”, tv1.getText().toString());
        return state;
    }


    @Override
    public void getAnswerList(Question question) {

        shiti_id = question.getData().getShiti_info().getId();
        shiti_ordernum = String.valueOf(page);

        type = question.getData().getShiti_info().getType();
        tvNum.setText(page + ".");
        tvType.setBackgroundResource(R.drawable.tv_shape_broder_orange);

//        tvType.setText("单选");
        tvContent.setText(question.getData().getShiti_info().getTitle());

        mDataList.clear();
        mDataList.addAll(question.getData().getShiti_xuanxiang());

        if (type.equals("1")) {
            tvType.setText("单选");
            mRecyclerView.setAdapter(singleAdapter);
            singleAdapter.notifyDataSetChanged();
        } else if (type.equals("2")) {
            tvType.setText("多选");
            mRecyclerView.setAdapter(multiAdapter);
            multiAdapter.notifyDataSetChanged();
        }else if (type.equals("3")) {
            tvType.setText("判断");
            mRecyclerView.setAdapter(singleAdapter);
            singleAdapter.notifyDataSetChanged();
        }

        SpannableStringBuilder span = new SpannableStringBuilder("缩进四个"+question.getData().getShiti_info().getTitle());
        span.setSpan(new ForegroundColorSpan(Color.TRANSPARENT), 0, 4,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        tvContent.setText(span);
    }
}
