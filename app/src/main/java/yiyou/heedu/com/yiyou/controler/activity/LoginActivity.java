package yiyou.heedu.com.yiyou.controler.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import yiyou.heedu.com.yiyou.R;
import yiyou.heedu.com.yiyou.model.Model;
import yiyou.heedu.com.yiyou.model.domain.UserInfo;

/**
 * Created by Administrator on 2016/12/6 0006.
 */
public class LoginActivity extends Activity implements View.OnClickListener {
    private ImageView ivUsername;
    private EditText edusername;
    private RelativeLayout rlPassword;
    private ImageView ivPassword;
    private EditText edpassword;
    private Button btnReg;
    private Button btnLogin;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2016-12-08 10:06:36 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        ivUsername = (ImageView) findViewById(R.id.iv_username);
        edusername = (EditText) findViewById(R.id.ed_username);
        rlPassword = (RelativeLayout) findViewById(R.id.rl_password);
        ivPassword = (ImageView) findViewById(R.id.iv_password);
        edpassword = (EditText) findViewById(R.id.ed_password);
        btnReg = (Button) findViewById(R.id.btn_reg);
        btnLogin = (Button) findViewById(R.id.btn_login);

        btnReg.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2016-12-08 10:06:36 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if (v == btnReg) {
            // Handle clicks for btnReg
            regist();
        } else if (v == btnLogin) {
            // Handle clicks for btnLogin
            dologin();
        }
    }

    //登录的业务逻辑
    private void dologin() {
        final String username = edusername.getText().toString().trim();
        final String pwd = edpassword.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
            edusername.requestFocus();
            return;
        } else if (TextUtils.isEmpty(pwd)) {
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
            edpassword.requestFocus();
            return;
        }


        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(pwd)) {
            final ProgressDialog pd = new ProgressDialog(this);
            pd.setMessage("正在登录...");
            pd.show();

            Model.getinstance().getGlobalThreadPool().execute(new Runnable() {
                public void run() {
                    //去换信服务器登录
                    EMClient.getInstance().login(username, pwd, new EMCallBack() {
                        @Override
                        public void onSuccess() {
                            Model.getinstance().loginSuccess(new UserInfo(username));
                            //保存数据账号到本地数据库
                            Model.getinstance().getAccountDAO().addAccount(new UserInfo(username));

                            runOnUiThread(new Runnable() {
                                public void run() {
                                    if (!LoginActivity.this.isFinishing())
                                        pd.dismiss();
                                    Toast.makeText(getApplicationContext(), "登录成功...", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        }

                        @Override
                        public void onError(int i, String s) {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        if (!LoginActivity.this.isFinishing())
                                            pd.dismiss();
                                        Toast.makeText(getApplicationContext(), "登录失败...", Toast.LENGTH_SHORT).show();
                                    }
                                });

                        }

                        @Override
                        public void onProgress(int i, String s) {
                            pd.setMessage("正在登录...");
                            pd.show();
                        }
                    });


                }
            });

        }
    }

    //跳转到登录页面
    private void regist() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViews();
    }
}
