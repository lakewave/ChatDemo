package com.ghb.chatdemo.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ghb.chatdemo.R;
import com.ghb.chatdemo.adapter.GVAdapter;
import com.ghb.chatdemo.adapter.VPAdapter;
import com.ghb.chatdemo.bean.EmojiBean;
import com.ghb.chatdemo.util.EmojiParseUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinTask;

/**
 * TODO:
 */
public class EmojiWidget implements AdapterView.OnItemClickListener {

    private Context context;
    private Activity activity;

    private ViewPager vp_emoji;
    private LinearLayout ll_point;
    private EditText et_content;

    private ArrayList<View> views = new ArrayList<>();
    private ArrayList<ImageView> points = new ArrayList<>();
    private ArrayList<List<EmojiBean>> listPageEmoji;
    private EmojiParseUtil emojiParseUtil;

    private int index;

    public EmojiWidget(Activity activity, EditText et_content) {
        this.activity = activity;
        this.et_content = et_content;
        context = activity;
        emojiParseUtil = EmojiParseUtil.getInstance(activity);
        initViewPager();
    }

    private void initViewPager(){
        vp_emoji = (ViewPager) activity.findViewById(R.id.vp_emoji);
        View view = new View(context);
        view.setBackgroundColor(Color.TRANSPARENT);
        views.add(view);
        listPageEmoji = emojiParseUtil.getPerPageEmoji();
        for (int i = 0; i < listPageEmoji.size(); i++) {
            GridView gridView = getGridView();
            gridView.setAdapter(new GVAdapter(context,listPageEmoji.get(i)));
            views.add(gridView);
        }
        views.add(view);

        vp_emoji.setAdapter(new VPAdapter(views));
        vp_emoji.setCurrentItem(1);
        index = 0;
        vp_emoji.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                runOnUIThread(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        initPoints(listPageEmoji.size());
    }

    private void initPoints(int size){
        ll_point = (LinearLayout) activity.findViewById(R.id.ll_point);
        for (int i = 0; i < size; i++) {
            ImageView iv = getPoint();
            if (i==0||i==size-1){
                iv.setVisibility(View.GONE);
            }
            ll_point.addView(iv,getLayoutParams());
            points.add(iv);
        }
    }

    private void runOnUIThread(final int position) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                index = position - 1;
                refreshPoint(position);
                if (position==0){
                    vp_emoji.setCurrentItem(1);
                    points.get(1).setBackgroundResource(R.mipmap.emoji_cursor_2);
                }else if (position==points.size()-1){
                    vp_emoji.setCurrentItem(position-1);
                    points.get(position-1).setBackgroundResource(R.mipmap.emoji_cursor_2);
                }
            }
        });
    }

    private void refreshPoint(int position) {
        for (int i = 0; i < points.size(); i++) {
            ImageView iv = points.get(i);
            iv.setBackgroundResource(position==i?R.mipmap.emoji_cursor_2:R.mipmap.emoji_cursor_1);
        }
    }

    private GridView getGridView(){
        GridView gridView = new GridView(context);
        gridView.setNumColumns(7);
        gridView.setBackgroundColor(Color.TRANSPARENT);
//        gridView.setHorizontalSpacing(1);
        gridView.setVerticalSpacing(10);
        gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        gridView.setCacheColorHint(0);
        gridView.setPadding(5, 0, 5, 0);
        gridView.setOnItemClickListener(this);
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gridView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));
        return gridView;

    }

    private ImageView getPoint(){
        ImageView iv = new ImageView(context);
        iv.setBackgroundResource(R.mipmap.emoji_cursor_1);
        getLayoutParams();
        return iv;
    }

    private LinearLayout.LayoutParams getLayoutParams() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                new ViewGroup.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT));
        layoutParams.leftMargin = 10;
        layoutParams.rightMargin = 10;
        layoutParams.width = 8;
        layoutParams.height = 8;
        return layoutParams;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        EmojiBean data = listPageEmoji.get(index).get(position);
        if (data.getId()==R.mipmap.emoji_item_delete){
            // 表情删除功能
            int selection = et_content.getSelectionStart();
            String text = et_content.getText().toString();
            if (selection>0){
                String str = text.substring(selection-1);
                if ("]".equals(str)){
                    int start = text.lastIndexOf("[");
                    et_content.getText().delete(start,selection);
                    return;
                }
                et_content.getText().delete(selection-1,selection);
            }
        }else if (!TextUtils.isEmpty(data.getDes())){
            SpannableString span = EmojiParseUtil.getInstance(context).getEmojiString(data.getId(),data.getDes());
            et_content.append(span);
        }
    }
}
