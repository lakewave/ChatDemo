package com.ghb.chatdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ghb.chatdemo.adapter.ChatAdapter;
import com.ghb.chatdemo.bean.Chat;
import com.ghb.chatdemo.view.EmojiWidget;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView recycler;
    private ChatAdapter adapter;

    private EditText et_content;
    private ImageView iv_emoji;
    private TextView tv_send;
    private LinearLayout ll_emoji;// 表情面板
    private EmojiWidget emojiWidget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
    }

    private void initUI() {
        recycler = (RecyclerView) findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setHasFixedSize(true);

        et_content = (EditText) findViewById(R.id.et_content);
        iv_emoji = (ImageView) findViewById(R.id.iv_emoji);
        tv_send = (TextView) findViewById(R.id.tv_send);
        ll_emoji = (LinearLayout) findViewById(R.id.ll_emoji);
        emojiWidget = new EmojiWidget(this,et_content);

        adapter = new ChatAdapter(this);
        recycler.setAdapter(adapter);

        iv_emoji.setOnClickListener(this);
        tv_send.setOnClickListener(this);
        et_content.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.et_content:
                ll_emoji.setVisibility(View.GONE);
                break;
            case R.id.iv_emoji:
                hideKeybord();
                if (ll_emoji.getVisibility()== View.GONE){
                    ll_emoji.setVisibility(View.VISIBLE);
                }else{
                    ll_emoji.setVisibility(View.GONE);
                }
                break;
            case R.id.tv_send:
                String content = et_content.getText().toString().trim();
                if (TextUtils.isEmpty(content)){
                    Toast.makeText(this,"the msg is empty",Toast.LENGTH_SHORT).show();
                    return;
                }
                Chat chat = new Chat(content);
                adapter.addChat(chat);
                recycler.scrollToPosition(adapter.getItemCount()-1);
                et_content.setText("");
                break;
        }
    }


    private void hideKeybord(){
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et_content.getWindowToken(),0);
    }
}
