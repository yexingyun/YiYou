/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package yiyou.heedu.com.yiyou.controler.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import yiyou.heedu.com.yiyou.R;
import yiyou.heedu.com.yiyou.model.domain.User;

/**
 * register screen
 * 
 */
public class RegisterActivity extends BaseActivity {
	private EditText userNameEditText;
	private EditText passwordEditText;
	private EditText confirmPwdEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		userNameEditText = (EditText) findViewById(R.id.username);
		passwordEditText = (EditText) findViewById(R.id.password);
		confirmPwdEditText = (EditText) findViewById(R.id.confirm_password);
	}

	public void register(View view) {
		final String username = userNameEditText.getText().toString().trim();
		final String pwd = passwordEditText.getText().toString().trim();
		String confirm_pwd = confirmPwdEditText.getText().toString().trim();
		if (TextUtils.isEmpty(username)) {
			Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
			userNameEditText.requestFocus();
			return;
		} else if (TextUtils.isEmpty(pwd)) {
			Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
			passwordEditText.requestFocus();
			return;
		} else if (TextUtils.isEmpty(confirm_pwd)) {
			Toast.makeText(this, "确认密码不能为空", Toast.LENGTH_SHORT).show();
			confirmPwdEditText.requestFocus();
			return;
		} else if (!pwd.equals(confirm_pwd)) {
			Toast.makeText(this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
			return;
		}

		if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(pwd)) {
			final ProgressDialog pd = new ProgressDialog(this);
			pd.setMessage("正在注册...");
			pd.show();

			new Thread(new Runnable() {
				public void run() {
					try {
						// call method in SDK
						EMClient.getInstance().createAccount(username, pwd);



						runOnUiThread(new Runnable() {
							public void run() {
								if (!RegisterActivity.this.isFinishing())
									pd.dismiss();
								final User myUser = new User();
								myUser.setUsername(username);
								myUser.setPassword(pwd);
								//myUser.setAge(18);
								addSubscription(myUser.signUp(new SaveListener<User>() {
									@Override
									public void done(User s, BmobException e) {
										if(e==null){
											toast("注册成功:" +s.toString()+",请登录");
										}else{
											loge(e);
											registefail(null,e);
										}
									}
								}));

//								DemoHelper.getInstance().setCurrentUserName(username);
								//Toast.makeText(getApplicationContext(), "注册成功！", Toast.LENGTH_SHORT).show();
								//注册成功后自动跳转到登录页面
								Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
								startActivity(intent);
								finish();
							}
						});
					} catch (final HyphenateException e) {
						runOnUiThread(new Runnable() {
							public void run() {
								if (!RegisterActivity.this.isFinishing())
									pd.dismiss();
								registefail(e,null);
							}
						});
					}
				}
			}).start();

		}
	}

	private void registefail(HyphenateException e,BmobException be) {
		int errorCode=e.getErrorCode();
		if(errorCode== EMError.NETWORK_ERROR){
            Toast.makeText(getApplicationContext(), "网络连接不可用！请检查连接...", Toast.LENGTH_SHORT).show();
        }else if(errorCode == EMError.USER_ALREADY_EXIST){
            Toast.makeText(getApplicationContext(),"用户已存在！", Toast.LENGTH_SHORT).show();
        }else if(errorCode == EMError.USER_AUTHENTICATION_FAILED){
            Toast.makeText(getApplicationContext(),"注册失败，无权限！", Toast.LENGTH_SHORT).show();
        }else if(errorCode == EMError.USER_ILLEGAL_ARGUMENT){
            Toast.makeText(getApplicationContext(), "用户名不合法...",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(), "注册失败...", Toast.LENGTH_SHORT).show();
        }


        if (be!=null){
			Toast.makeText(getApplicationContext(), "注册失败...", Toast.LENGTH_SHORT).show();
		}

	}

	public void back(View view) {
		finish();
	}
	public void toast(String msg){
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

}
