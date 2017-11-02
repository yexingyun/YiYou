package yiyou.heedu.com.yiyou.controler.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.widget.EaseAlertDialog;
import com.hyphenate.exceptions.HyphenateException;

import java.util.List;

import yiyou.heedu.com.yiyou.R;
import yiyou.heedu.com.yiyou.model.Model;
import yiyou.heedu.com.yiyou.model.dao.ContactTableDao;
import yiyou.heedu.com.yiyou.model.db.DBHelper;
import yiyou.heedu.com.yiyou.model.domain.UserInfo;

/**
 * Created by Administrator on 2016/12/12 0012.
 */
public class AddContactActivity extends Activity {
    private EditText editText;
    private RelativeLayout searchedUserLayout;
    private TextView nameText;
    private Button searchBtn;
    private String toAddUsername;
    private ProgressDialog progressDialog;
    private UserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        editText = (EditText) findViewById(R.id.edit_note);
        String strUserName = getResources().getString(R.string.user_name);
        editText.setHint(strUserName);
        searchedUserLayout = (RelativeLayout) findViewById(R.id.ll_user);
        nameText = (TextView) findViewById(R.id.name);
        searchBtn = (Button) findViewById(R.id.search);

    }

    /**
     * search contact 查找联系人
     *
     * @param v
     */
    public void searchContact(View v) {
        final String name = editText.getText().toString();
        toAddUsername = name;
        if (TextUtils.isEmpty(name)) {
            new EaseAlertDialog(this, R.string.Please_enter_a_username).show();
            return;
        }
//        // TODO you can search the user from your app server here.
        Model.getinstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                //去环信服务器判断当前用户是否存在，
                userInfo = new UserInfo(toAddUsername);
                //show the userame and add button if user exist
                AddContactActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        searchedUserLayout.setVisibility(View.VISIBLE);
                        nameText.setText(userInfo.getName());
                    }
                });
            }
        });
    }





    public void addContact(View view) {
        //不能添加自己
        if (EMClient.getInstance().getCurrentUser().equals(nameText.getText().toString())) {
            new EaseAlertDialog(this, R.string.not_add_myself).show();
            return;
        }
        //如果已经在本地联系人中，就查出来，比较并提示
        UserInfo contactByHx = Model.getinstance().getDbManager().getContactTableDao().getContactByHx(toAddUsername);
        if (contactByHx!=null){
            if (contactByHx.getName()==null){
            //如果等于空什么也不做
            } else if (contactByHx.getName().equals(toAddUsername)) {
                //let the user know the contact already in your contact list
                new EaseAlertDialog(this, R.string.This_user_is_already_your_friend).show();
                return;
            }
        }
            //如果在黑名单，就提示
        if (EMClient.getInstance().contactManager().getBlackListUsernames().contains(nameText.getText().toString())) {
            new EaseAlertDialog(this, R.string.user_already_in_contactlist).show();
            return;
        }

        progressDialog = new ProgressDialog(this);
        String stri = getResources().getString(R.string.Is_sending_a_request);
        progressDialog.setMessage(stri);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        Model.getinstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                //demo use a hardcode reason here, you need let user to input if you like
                String s = getResources().getString(R.string.Add_a_friend);
                try {
                    EMClient.getInstance().contactManager().addContact(userInfo.getName(), s);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            String s1 = getResources().getString(R.string.send_successful);
                            Toast.makeText(getApplicationContext(), s1, Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (final HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            String s2 = getResources().getString(R.string.Request_add_buddy_failure);
                            Toast.makeText(getApplicationContext(), s2 + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }


            }
        });
    }

    public void back(View v) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
