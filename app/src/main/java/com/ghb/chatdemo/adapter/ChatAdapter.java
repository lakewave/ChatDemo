package com.ghb.chatdemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ghb.chatdemo.R;
import com.ghb.chatdemo.bean.Chat;
import com.ghb.chatdemo.util.EmojiParseUtil;

import java.util.ArrayList;

/**
 * TODO:
 */
public class ChatAdapter extends RecyclerView.Adapter {

    private Context context;
    private final static int LEFT_TYPE = 0;
    private final static int RIGHT_TYPE = 1;
    private ArrayList<Chat> list = new ArrayList<>();

    public ChatAdapter(Context context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType==LEFT_TYPE){
            view = LayoutInflater.from(context).inflate(R.layout.chat_left,parent,false);
        }else{
            view = LayoutInflater.from(context).inflate(R.layout.chat_right,parent,false);
        }
        return new ChatHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        Chat chat = list.get(position);
        ChatHolder chatHolder = (ChatHolder) holder;
        chatHolder.setData(viewType,chat);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position%2==0?LEFT_TYPE:RIGHT_TYPE;
    }

    public void addChat(Chat data){
        list.add(data);
        notifyDataSetChanged();
    }

    class ChatHolder extends RecyclerView.ViewHolder{

        private ImageView iv_user;
        private TextView tv_content;

        public ChatHolder(View itemView) {
            super(itemView);
            iv_user = (ImageView) itemView.findViewById(R.id.iv_user);
            tv_content = (TextView) itemView.findViewById(R.id.tv_content);
        }

        public void setData(int viewType, Chat chat) {
            SpannableString span = EmojiParseUtil.getInstance(context).checkEmojiString(chat.getText());
            tv_content.setText(span);
            iv_user.setImageResource(viewType==LEFT_TYPE?R.mipmap.soyeon:R.mipmap.meinv_02);
        }
    }
}
