package yiyou.heedu.com.yiyou.controler.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.hyphenate.easeui.widget.chatrow.EaseCustomChatRowProvider;

import yiyou.heedu.com.yiyou.R;
import yiyou.heedu.com.yiyou.utils.Constant;

/**
 * Created by Administrator on 2016/12/19 0019.
 */
public class ChatActivity extends FragmentActivity {
    private String hxID;
    public static ChatActivity activityInstance;
    private EaseChatFragment easeChatFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityInstance = this;
        setContentView(R.layout.activity_chat);
        initdata();
        initListener();

    }



    private void initdata() {
         easeChatFragment = new EaseChatFragment();
         hxID = getIntent().getStringExtra(EaseConstant.EXTRA_USER_ID);
        easeChatFragment.setArguments(getIntent().getExtras());
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fl_chat,easeChatFragment).commit();
    }
    private void initListener() {
        easeChatFragment.setChatFragmentListener(new EaseChatFragment.EaseChatFragmentHelper() {
            @Override
            public void onSetMessageAttributes(EMMessage message) {

            }

            @Override
            public void onEnterToChatDetails() {
                Intent  intent = new Intent(ChatActivity.this,GroupDetailActivity.class);
                intent.putExtra(Constant.GROUP_ID,hxID);
                startActivity(intent);
            }

            @Override
            public void onAvatarClick(String username) {

            }

            @Override
            public void onAvatarLongClick(String username) {

            }

            @Override
            public boolean onMessageBubbleClick(EMMessage message) {
                return false;
            }

            @Override
            public void onMessageBubbleLongClick(EMMessage message) {

            }

            @Override
            public boolean onExtendMenuItemClick(int itemId, View view) {
                return false;
            }

            @Override
            public EaseCustomChatRowProvider onSetCustomChatRowProvider() {
                return null;
            }
        });
    }
}
