package yiyou.heedu.com.yiyou.model;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.hyphenate.EMContactListener;
import com.hyphenate.EMGroupChangeListener;
import com.hyphenate.chat.EMClient;

import yiyou.heedu.com.yiyou.model.domain.GroupInfo;
import yiyou.heedu.com.yiyou.model.domain.InvationInfo;
import yiyou.heedu.com.yiyou.model.domain.UserInfo;
import yiyou.heedu.com.yiyou.utils.Constant;
import yiyou.heedu.com.yiyou.utils.SpUtils;

/**
 * Created by Administrator on 2016/12/14 0014.
 */

public class EventListener {
    private final Context mContext;
    private final LocalBroadcastManager mLBM;



    public EventListener(Context context) {
        mContext = context;
        //注册一个联系人监听
        mLBM = LocalBroadcastManager.getInstance(mContext);
        EMClient.getInstance().contactManager().setContactListener(emContactListener);
        // 注册一个群信息变化的监听
        EMClient.getInstance().groupManager().addGroupChangeListener(eMGroupChangeListener);
    }

    private final EMGroupChangeListener eMGroupChangeListener = new EMGroupChangeListener() {
        //收到 群邀请
        @Override
        public void onInvitationReceived(String groupId, String groupName, String inviter, String reason) {
            // 数据更新
            InvationInfo invitationInfo = new InvationInfo();
            invitationInfo.setReason(reason);
            invitationInfo.setGroup(new GroupInfo(groupName, groupId, inviter));
            invitationInfo.setStatus(InvationInfo.InvitationStatus.NEW_GROUP_INVITE);
            Model.getinstance().getDbManager().getInviteTableDao().addInvitation(invitationInfo);

            // 红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);

            // 发送广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        //收到 群申请通知
        @Override
        public void onApplicationReceived(String groupId, String groupName, String applicant, String reason) {

            // 数据更新
            InvationInfo invitationInfo = new InvationInfo();
            invitationInfo.setReason(reason);
            invitationInfo.setGroup(new GroupInfo(groupName, groupId, applicant));
            invitationInfo.setStatus(InvationInfo.InvitationStatus.NEW_GROUP_APPLICATION);
            Model.getinstance().getDbManager().getInviteTableDao().addInvitation(invitationInfo);

            // 红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);

            // 发送广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        //收到 群申请被接受
        @Override
        public void onApplicationAccept(String groupId, String groupName, String accepter) {

            // 更新数据
            InvationInfo invitationInfo = new InvationInfo();
            invitationInfo.setGroup(new GroupInfo(groupName,groupId,accepter));
            invitationInfo.setStatus(InvationInfo.InvitationStatus.GROUP_APPLICATION_ACCEPTED);

            Model.getinstance().getDbManager().getInviteTableDao().addInvitation(invitationInfo);

            // 红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);

            // 发送广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        //收到 群申请被拒绝
        @Override
        public void onApplicationDeclined(String groupId, String groupName, String decliner, String reason) {
            // 更新数据
            InvationInfo invitationInfo = new InvationInfo();
            invitationInfo.setReason(reason);
            invitationInfo.setGroup(new GroupInfo(groupName, groupId, decliner));
            invitationInfo.setStatus(InvationInfo.InvitationStatus.GROUP_APPLICATION_DECLINED);

            Model.getinstance().getDbManager().getInviteTableDao().addInvitation(invitationInfo);

            // 红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);

            // 发送广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        //收到 群邀请被同意
        @Override
        public void onInvitationAccepted(String groupId, String inviter, String reason) {

            // 更新数据
            InvationInfo invitationInfo = new InvationInfo();
            invitationInfo.setReason(reason);
            invitationInfo.setGroup(new GroupInfo(groupId, groupId, inviter));
            invitationInfo.setStatus(InvationInfo.InvitationStatus.GROUP_INVITE_ACCEPTED);

            Model.getinstance().getDbManager().getInviteTableDao().addInvitation(invitationInfo);

            // 红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);

            // 发送广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        //收到 群邀请被拒绝
        @Override
        public void onInvitationDeclined(String groupId, String inviter, String reason) {
            // 更新数据
            InvationInfo invitationInfo = new InvationInfo();
            invitationInfo.setReason(reason);
            invitationInfo.setGroup(new GroupInfo(groupId, groupId, inviter));
            invitationInfo.setStatus(InvationInfo.InvitationStatus.GROUP_INVITE_DECLINED);

            Model.getinstance().getDbManager().getInviteTableDao().addInvitation(invitationInfo);

            // 红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);

            // 发送广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        //收到 群成员被删除
        @Override
        public void onUserRemoved(String groupId, String groupName) {

        }

        //收到 群被解散
        @Override
        public void onGroupDestroyed(String groupId, String groupName) {

        }

        //收到 群邀请被自动接受
        @Override
        public void onAutoAcceptInvitationFromGroup(String groupId, String inviter, String inviteMessage) {
            // 更新数据
            InvationInfo invitationInfo = new InvationInfo();
            invitationInfo.setReason(inviteMessage);
            invitationInfo.setGroup(new GroupInfo(groupId, groupId, inviter));
            invitationInfo.setStatus(InvationInfo.InvitationStatus.GROUP_INVITE_ACCEPTED);

            Model.getinstance().getDbManager().getInviteTableDao().addInvitation(invitationInfo);

            // 红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);

            // 发送广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }
    };

    private final EMContactListener emContactListener = new EMContactListener() {

        @Override
        public void onContactAdded(String s) {
            //数据更新
            Model.getinstance().getDbManager().getContactTableDao().saveContact(new UserInfo(s), true);
            //发送广播
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_CHANGED));
        }

        @Override
        public void onContactDeleted(String hxID) {
            Model.getinstance().getDbManager().getContactTableDao().deleteContactByHxId(hxID);
            Model.getinstance().getDbManager().getInviteTableDao().removeInvitation(hxID);
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_CHANGED));

        }

        @Override//接受到新的邀请
        public void onContactInvited(String hxID, String reason) {
            //数据更新
            InvationInfo invitationInfo=new InvationInfo();
            invitationInfo.setReason(reason);
            invitationInfo.setUser(new UserInfo(hxID));
            invitationInfo.setStatus(InvationInfo.InvitationStatus.NEW_INVITE);//新邀请
            Model.getinstance().getDbManager().getInviteTableDao().addInvitation(invitationInfo);

            //红点的处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,true);
            //发送广播
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_INVITE_CHANGED));
        }

        //别人同意了你的好友邀请
        @Override
        public void onContactAgreed(String hxID) {
            //数据更新
            InvationInfo invitationInfo=new InvationInfo();

            invitationInfo.setUser(new UserInfo(hxID));
            invitationInfo.setStatus(InvationInfo.InvitationStatus.INVITE_ACCEPT_BY_PEER);//别人同意了你的邀请
            Model.getinstance().getDbManager().getInviteTableDao().addInvitation(invitationInfo);

            //红点的处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,true);
            //发送广播
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_INVITE_CHANGED));
        }

        //别人拒绝了你的好友邀请
        @Override
        public void onContactRefused(String s) {
            //红点的处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,true);
            //发送广播
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_INVITE_CHANGED));
        }
    };




}
