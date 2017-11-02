package yiyou.heedu.com.yiyou;

import android.app.Application;
import android.content.Context;

import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.controller.EaseUI;

import cn.bmob.v3.Bmob;
import yiyou.heedu.com.yiyou.model.Model;

/**
 * Created by Administrator on 2016/12/6 0006.
 */

public class MyApplication extends Application {
    private static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();

        initBmob();
        EMOptions options = new EMOptions();
        options.setAcceptInvitationAlways(false);
        options.setAutoAcceptGroupInvitation(false);
        EaseUI.getInstance().init(this,options);


        //初始化数据模型层类
        Model.getinstance().inite(this);

        mContext =this;
    }

    private void initBmob() {
        //第一：默认初始化
        Bmob.initialize(this, "ce0a3e0275400c7c77728c8f280a7080");
    }



    // 获取全局上下文对象
    public static Context getGlobalApplication(){
        return mContext;
    }
}
