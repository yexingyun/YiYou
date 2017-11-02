package yiyou.heedu.com.yiyou.pager;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.ui.EaseConversationListFragment;

import java.util.List;


import yiyou.heedu.com.yiyou.controler.activity.ChatActivity;

public class MesgFragment_mesg extends EaseConversationListFragment {
    @Override
    protected void initView() {
        super.initView();
        //跳转到会话详情页面
        setConversationListItemClickListener(new EaseConversationListItemClickListener() {
            @Override
            public void onListItemClicked(EMConversation conversation) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra(EaseConstant.EXTRA_USER_ID,conversation.conversationId());
                if (conversation.getType()==EMConversation.EMConversationType.GroupChat){
intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE,EaseConstant.CHATTYPE_GROUP);
                }
                startActivity(intent);
            }
        });
        conversationList.clear();
        EMClient.getInstance().chatManager().addMessageListener(emMessageListner);
    }

    private EMMessageListener emMessageListner = new EMMessageListener() {
        @Override
        public void onMessageReceived(List<EMMessage> list) {
            //设置数据。
            EaseUI.getInstance().getNotifier().onNewMesg(list);
            //刷新页面
            refresh();
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> list) {

        }

        @Override
        public void onMessageReadAckReceived(List<EMMessage> list) {

        }

        @Override
        public void onMessageDeliveryAckReceived(List<EMMessage> list) {

        }

        @Override
        public void onMessageChanged(EMMessage emMessage, Object o) {
//刷新页面
            refresh();
        }
    };
}
