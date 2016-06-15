package com.ghb.chatdemo.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.ghb.chatdemo.R;
import com.ghb.chatdemo.bean.EmojiBean;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: GridView适配器
 */
public class GVAdapter extends BaseAdapter {

    private Context context;
    private List<EmojiBean> list = new ArrayList<>();

    public GVAdapter(Context context, List<EmojiBean> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView==null){
            imageView = new ImageView(context);
            convertView = imageView;
        }else{
            imageView = (ImageView) convertView;
        }

        EmojiBean data = list.get(position);
        if (data.getId()== R.mipmap.emoji_item_delete){
            imageView.setImageResource(R.mipmap.emoji_item_delete);
        }else if (TextUtils.isEmpty(data.getDes())){
            imageView.setImageDrawable(null);
        }else{
            imageView.setImageResource(data.getId());
        }

        return imageView;
    }
}
