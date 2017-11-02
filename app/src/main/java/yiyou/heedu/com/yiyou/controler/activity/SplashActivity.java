package yiyou.heedu.com.yiyou.controler.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;

import com.hyphenate.chat.EMClient;

import yiyou.heedu.com.yiyou.R;
import yiyou.heedu.com.yiyou.model.Model;
import yiyou.heedu.com.yiyou.model.domain.UserInfo;


public class SplashActivity extends Activity {
    private static final int SHOW = 1;
    private static final String TAG = "SplashActivity";
    private UserInfo account;
    Handler handle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isLogin();

        }

        private void isLogin() {
            //如果页面关闭了就不作处理
            if (isFinishing()) {
                return;
            }
            Model.getinstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    // 判断当前账号是否已经登录过
                    if(EMClient.getInstance().isLoggedInBefore()) {// 登录过

                        // 获取到当前登录用户的信息
                        UserInfo account = Model.getinstance().getAccountDAO().getAccount(EMClient.getInstance().getCurrentUser());

                        if(account == null) {
                            // 跳转到登录页面
                            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }else {
                            // 登录成功后的方法
                            Model.getinstance().loginSuccess(account);

                            // 跳转到主页面
                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    }else {// 没登录过
                        // 跳转到登录页面
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }

                    // 结束当前页面
                    finish();
                }
            });

            finish();
        }
    };

    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        handle.sendEmptyMessageDelayed(SHOW, 1000);

    }

    /**
     * 跳转主页面关闭当前页
     */

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        startMainActivity();
        Log.e(TAG, "onTouchEvent: " + event.getAction());
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        //把所有的消息和任务移除
        handle.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
