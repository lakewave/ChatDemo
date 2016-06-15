package com.ghb.chatdemo.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Log;

import com.ghb.chatdemo.R;
import com.ghb.chatdemo.bean.EmojiBean;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * TODO: Emoji表情解析工具
 */
public class EmojiParseUtil {

    private static EmojiParseUtil instance;
    private Context context;
    private HashMap<String,String> mapEmoji = new HashMap<>();

    private ArrayList<List<EmojiBean>> listPageEmoji = new ArrayList<>();
    private final String regex = "\\[[^\\]]+\\]";
    private final static int COUNT = 20;// emoji size per page except delete emoji

    private EmojiParseUtil(Context context) {
        this.context = context;
        init();
    }

    public static EmojiParseUtil getInstance(Context context){
        if (instance==null){
            synchronized (EmojiParseUtil.class){
                if (instance==null){
                    instance = new EmojiParseUtil(context);
                }
            }
        }
        return instance;
    }

    public ArrayList<List<EmojiBean>> getPerPageEmoji(){
        return listPageEmoji;
    }


    private synchronized void init(){
        if (listPageEmoji.size()>0){
            return;
        }
        ArrayList<String> list = getEmojiConfig();
        if (list==null||list.isEmpty()){
            Log.e("emoji","emoji config file is empty");
            return;
        }
        print("size=="+list.size());
        List<EmojiBean> listEmoji = new ArrayList<>();
        for (String str:list) {
            print("str=="+str);
            String[] text = str.split(",");
            String fileName = text[0].substring(0,text[0].lastIndexOf("."));
            mapEmoji.put(text[1],fileName);
            int resId = context.getResources().getIdentifier(fileName,"mipmap",context.getPackageName());
            if (resId==0){
                Log.e("emoji","resId == 0");
                break;
            }else{
                EmojiBean data = new EmojiBean(resId,text[1],fileName);
                listEmoji.add(data);
            }
        }
        int page = (int) Math.ceil(listEmoji.size()/COUNT);
        print("page=="+page);
        // Emoji表情分页
        for (int i = 0; i < page; i++) {
            int start = i * COUNT;
            int end = start + COUNT;
            List<EmojiBean> listSub = listEmoji.subList(start, end);
            // 新new个list，规避异常：ConcurrentModificationException
            List<EmojiBean> listPage = new ArrayList<>();
            listPage.addAll(listSub);
        /*
        不够一页怎么处理好？
        添加无效表情？？？
         */
            EmojiBean emoji = new EmojiBean(R.mipmap.emoji_item_delete);
            listPage.add(emoji);
            print("size_listPerPage=="+listPage.size());
            listPageEmoji.add(listPage);
        }
    }

    /**
     * 读取Emoji表情对照文件
     * @return
     */
    private ArrayList<String> getEmojiConfig(){
        ArrayList<String> list = new ArrayList<>();
        try {
            InputStream inputStream = context.getResources().getAssets().open("emoji");
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String str = null;
            while ((str = br.readLine())!=null){
                list.add(str);
            }
            return list;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public SpannableString getEmojiString(int resId,String emojiDes){
        if (TextUtils.isEmpty(emojiDes)){
            return null;
        }
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),resId);
        bitmap = Bitmap.createScaledBitmap(bitmap,50,50,true);// why is 50??
        ImageSpan imageSpan = new ImageSpan(context,bitmap);
        SpannableString spannableString = new SpannableString(emojiDes);
        spannableString.setSpan(imageSpan,0,emojiDes.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    public SpannableString checkEmojiString(String str){
        SpannableString spannableString = new SpannableString(str);
        Pattern pattern = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
        dealEmoji(spannableString,pattern,0);
        return spannableString;
    }

    private void dealEmoji(SpannableString spanStr,Pattern pattern,int start){
        Matcher matcher = pattern.matcher(spanStr);
        while (matcher.find()){
            String key = matcher.group();
            if (matcher.start()<start){
                continue;
            }
            String value = mapEmoji.get(key);
            if (TextUtils.isEmpty(value)){
                continue;
            }
            int resId = context.getResources().getIdentifier(value,"mipmap",context.getPackageName());
            /*
            try {
                Field field = R.mipmap.class.getDeclaredField(value);
                int resId = Integer.parseInt(field.get(null).toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            */
            if (resId!=0){
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),resId);
                // 适配缩放表情尺寸
                int size = getEmojiSize();
                bitmap = Bitmap.createScaledBitmap(bitmap,size,size,true);

                ImageSpan imageSpan = new ImageSpan(context,bitmap);
                // 计算该图片名字的长度，也就是要替换的字符串的长度
                int end = matcher.start()+key.length();
                // 将该图片替换到字符串指定位置上
                spanStr.setSpan(imageSpan,matcher.start(),end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                if (end<spanStr.length()){
                    // 没有匹配完，继续匹配
                    dealEmoji(spanStr,pattern,end);
                }
                break;// what to do
            }
        }
    }

    /**适配表情大小*/
    private int getEmojiSize(){
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int size = dm.heightPixels*80/1920;
        return size;
    }


    private void print(String msg){
        Log.e("emoji", msg);
    }


}
