package yiyou.heedu.com.yiyou.controler.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMGroupChangeListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.widget.EaseAlertDialog;
import com.hyphenate.easeui.widget.EaseExpandGridView;
import com.hyphenate.easeui.widget.EaseSwitchButton;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EMLog;
import com.hyphenate.util.NetUtils;

import java.util.ArrayList;
import java.util.List;

import yiyou.heedu.com.yiyou.R;
import yiyou.heedu.com.yiyou.model.Model;
import yiyou.heedu.com.yiyou.utils.Constant;

/**
 * Created by Administrator on 2016/12/24 0024.
 */
public class GroupDetailActivity extends Activity implements View.OnClickListener {
    public GroupDetailActivity() {
    }

    private static final String TAG = "GroupDetailsActivity";
    private static final int REQUEST_CODE_ADD_USER = 0;
    private static final int REQUEST_CODE_EXIT = 1;
    private static final int REQUEST_CODE_EXIT_DELETE = 2;
    private static final int REQUEST_CODE_EDIT_GROUPNAME = 5;
    private String groupId;
    private EMGroup group;

    public GroupDetailActivity instance;
    private String st;
    private ProgressBar loadingPB;
    private Button exitBtn;
    private Button deleteBtn;
    private EaseSwitchButton switchButton;

    private ProgressDialog progressDialog;
    private GroupDetailActivity.GridAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        groupId = getIntent().getStringExtra(Constant.GROUP_ID);
        group = EMClient.getInstance().groupManager().getGroup(groupId);
        // we are not supposed to show the group if we don't find the group
        //不支持显示没有的群组
        if (group == null) {
            finish();
            return;
        }
        setContentView(R.layout.em_activity_group_details);

        instance = this;
        st = getResources().getString(R.string.people);
        RelativeLayout clearAllHistory = (RelativeLayout) findViewById(R.id.clear_all_history);
        EaseExpandGridView userGridview = (EaseExpandGridView) findViewById(R.id.gridview);
        loadingPB = (ProgressBar) findViewById(R.id.progressBar);
        exitBtn = (Button) findViewById(R.id.btn_exit_grp);
        deleteBtn = (Button) findViewById(R.id.btn_exitdel_grp);
        RelativeLayout changeGroupNameLayout = (RelativeLayout) findViewById(R.id.rl_change_group_name);
        RelativeLayout idLayout = (RelativeLayout) findViewById(R.id.rl_group_id);
        idLayout.setVisibility(View.VISIBLE);
        TextView idText = (TextView) findViewById(R.id.tv_group_id_value);

        RelativeLayout rl_switch_block_groupmsg = (RelativeLayout) findViewById(R.id.rl_switch_block_groupmsg);
        switchButton = (EaseSwitchButton) findViewById(R.id.switch_btn);

        idText.setText(groupId);
        if (group.getOwner() == null || "".equals(group.getOwner())
                || !group.getOwner().equals(EMClient.getInstance().getCurrentUser())) {
            exitBtn.setVisibility(View.GONE);
            deleteBtn.setVisibility(View.GONE);
            changeGroupNameLayout.setVisibility(View.GONE);
        }
        // show dismiss button if you are owner of group
        //如果是群主
        if (EMClient.getInstance().getCurrentUser().equals(group.getOwner())) {
            exitBtn.setVisibility(View.GONE);
            deleteBtn.setVisibility(View.VISIBLE);
        }

        GroupDetailActivity.GroupChangeListener groupChangeListener = new GroupDetailActivity.GroupChangeListener();
        EMClient.getInstance().groupManager().addGroupChangeListener(groupChangeListener);
        //显示群ID并显示多少人
        ((TextView) findViewById(R.id.group_name)).setText(group.getGroupName() + "(" + group.getAffiliationsCount() + st);

        List<String> members = new ArrayList<String>();
        members.addAll(group.getMembers());

        adapter = new GroupDetailActivity.GridAdapter(this, R.layout.em_grid, members);
        userGridview.setAdapter(adapter);

        // 保证每次进详情看到的都是最新的group
        updateGroup();

        // 设置OnTouchListener
        userGridview.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (adapter.isInDeleteMode) {
                            adapter.isInDeleteMode = false;
                            adapter.notifyDataSetChanged();
                            return true;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        v.performClick();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        clearAllHistory.setOnClickListener(this);
        changeGroupNameLayout.setOnClickListener(this);
        rl_switch_block_groupmsg.setOnClickListener(this);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String st1 = getResources().getString(R.string.being_added);
        final String st2 = getResources().getString(R.string.is_quit_the_group_chat);
        final String st3 = getResources().getString(R.string.chatting_is_dissolution);
        String st4 = getResources().getString(R.string.are_empty_group_of_news);
        String st5 = getResources().getString(R.string.is_modify_the_group_name);
        final String st6 = getResources().getString(R.string.Modify_the_group_name_successful);
        final String st7 = getResources().getString(R.string.change_the_group_name_failed_please);

        if (resultCode == RESULT_OK) {
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(GroupDetailActivity.this);
                progressDialog.setMessage(st1);
                progressDialog.setCanceledOnTouchOutside(false);
            }
            switch (requestCode) {
                case REQUEST_CODE_ADD_USER:// 添加群成员
                    final String[] newmembers = data.getStringArrayExtra("newmembers");
                    progressDialog.setMessage(st1);
                    progressDialog.show();
                    addMembersToGroup(newmembers);
                    break;
                case REQUEST_CODE_EXIT: // 退出群
                    progressDialog.setMessage(st2);
                    progressDialog.show();
                    exitGrop();
                    break;
                case REQUEST_CODE_EXIT_DELETE: // 解散群
                    progressDialog.setMessage(st3);
                    progressDialog.show();
                    deleteGrop();
                    break;

                case REQUEST_CODE_EDIT_GROUPNAME: //修改群名称
                    final String returnData = data.getStringExtra("data");
                    if (!TextUtils.isEmpty(returnData)) {
                        progressDialog.setMessage(st5);
                        progressDialog.show();

                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    EMClient.getInstance().groupManager().changeGroupName(groupId, returnData);
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            ((TextView) findViewById(R.id.group_name)).setText(returnData + "(" + group.getAffiliationsCount()
                                                    + st);
                                            progressDialog.dismiss();
                                            Toast.makeText(getApplicationContext(), st6, Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                } catch (HyphenateException e) {
                                    e.printStackTrace();
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            progressDialog.dismiss();
                                            Toast.makeText(getApplicationContext(), st7, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        }).start();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void addMembersToGroup(final String[] newmembers) {
        final String st6 = getResources().getString(R.string.Add_group_members_fail);
        Model.getinstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // 创建者调用add方法
                    if (EMClient.getInstance().getCurrentUser().equals(group.getOwner())) {
                        EMClient.getInstance().groupManager().addUsersToGroup(groupId, newmembers);
                    } else {
                        // 一般成员调用invite方法
                        EMClient.getInstance().groupManager().inviteUser(groupId, newmembers, null);
                    }
                    runOnUiThread(new Runnable() {
                        public void run() {
                            refreshMembers();
                            Toast.makeText(getApplicationContext(), "已发送邀请", Toast.LENGTH_SHORT).show();
                            ((TextView) findViewById(R.id.group_name)).setText(group.getGroupName() + "(" + group.getAffiliationsCount()
                                    + st);
                            progressDialog.dismiss();
                        }
                    });
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), st6 + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

    /**
     * 点击退出群组按钮
     *
     * @param view
     */
    public void exitGroup(View view) {
        startActivityForResult(new Intent(this, ExitGroupDialog.class), REQUEST_CODE_EXIT);

    }

    /**
     * 点击解散群组按钮
     *
     * @param view
     */
    public void exitDeleteGroup(View view) {
        startActivityForResult(new Intent(this, ExitGroupDialog.class).putExtra("deleteToast", getString(R.string.dissolution_group_hint)),
                REQUEST_CODE_EXIT_DELETE);

    }

    private void exitGrop() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    EMClient.getInstance().groupManager().leaveGroup(groupId);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            setResult(RESULT_OK);
                            finish();
                            if (ChatActivity.activityInstance != null)
                                ChatActivity.activityInstance.finish();
                        }
                    });
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.Exit_the_group_chat_failure) + " " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).start();
    }

    private void deleteGrop() {
        final String st5 = getResources().getString(R.string.Dissolve_group_chat_tofail);
        Model.getinstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().groupManager().destroyGroup(groupId);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            setResult(RESULT_OK);
                            finish();
                            if (ChatActivity.activityInstance != null)
                                ChatActivity.activityInstance.finish();
                        }
                    });
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), st5 + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }

    private void updateGroup() {
        Model.getinstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().groupManager().getGroupFromServer(groupId);

                    runOnUiThread(new Runnable() {
                        public void run() {
                            ((TextView) findViewById(R.id.group_name)).setText(group.getGroupName() + "(" + group.getAffiliationsCount()
                                    + ")");
                            loadingPB.setVisibility(View.INVISIBLE);
                            refreshMembers();
                            if (EMClient.getInstance().getCurrentUser().equals(group.getOwner())) {
                                // 显示解散按钮
                                exitBtn.setVisibility(View.GONE);
                                deleteBtn.setVisibility(View.VISIBLE);
                            } else {
                                // 显示退出按钮
                                exitBtn.setVisibility(View.VISIBLE);
                                deleteBtn.setVisibility(View.GONE);
                            }

                            // update block
                            EMLog.d(TAG, "group msg is blocked:" + group.isMsgBlocked());
                            if (group.isMsgBlocked()) {
                                switchButton.openSwitch();
                            } else {
                                switchButton.closeSwitch();
                            }
                        }
                    });

                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            loadingPB.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        });

    }

    private class GroupChangeListener implements EMGroupChangeListener {
        @Override
        public void onInvitationReceived(String s, String s1, String s2, String s3) {

        }

        @Override
        public void onApplicationReceived(String s, String s1, String s2, String s3) {

        }

        @Override
        public void onApplicationAccept(String s, String s1, String s2) {

        }

        @Override
        public void onApplicationDeclined(String s, String s1, String s2, String s3) {

        }

        @Override
        public void onInvitationAccepted(String s, String s1, String s2) {

        }

        @Override
        public void onInvitationDeclined(String s, String s1, String s2) {

        }

        @Override
        public void onUserRemoved(String s, String s1) {
            finish();
        }

        @Override
        public void onGroupDestroyed(String s, String s1) {
            finish();
        }

        @Override
        public void onAutoAcceptInvitationFromGroup(String s, String s1, String s2) {

        }
    }


    private class GridAdapter extends ArrayAdapter<String> {

        private int res;
        public boolean isInDeleteMode;
        private List<String> objects;

        public GridAdapter(Context context, int textViewResourceId, List<String> objects) {
            super(context, textViewResourceId, objects);
            this.objects = objects;
            res = textViewResourceId;
            isInDeleteMode = false;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            GroupDetailActivity.ViewHolder holder = null;
            if (convertView == null) {
                holder = new GroupDetailActivity.ViewHolder();
                convertView = LayoutInflater.from(getContext()).inflate(res, null);
                holder.imageView = (ImageView) convertView.findViewById(R.id.iv_avatar);
                holder.textView = (TextView) convertView.findViewById(R.id.tv_name);
                holder.badgeDeleteView = (ImageView) convertView.findViewById(R.id.badge_delete);
                convertView.setTag(holder);
            } else {
                holder = (GroupDetailActivity.ViewHolder) convertView.getTag();
            }
            final LinearLayout button = (LinearLayout) convertView.findViewById(R.id.button_avatar);
            // 最后一个item，减人按钮
            if (position == getCount() - 1) {
                holder.textView.setText("");
                // 设置成删除按钮
                holder.imageView.setImageResource(R.drawable.em_smiley_minus_btn);
//				button.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.smiley_minus_btn, 0, 0);
                // 如果不是创建者或者没有相应权限，不提供加减人按钮
                if (!group.getOwner().equals(EMClient.getInstance().getCurrentUser())) {
                    // if current user is not group admin, hide add/remove btn
                    convertView.setVisibility(View.INVISIBLE);
                } else { // 显示删除按钮
                    if (isInDeleteMode) {
                        // 正处于删除模式下，隐藏删除按钮
                        convertView.setVisibility(View.INVISIBLE);
                    } else {
                        // 正常模式
                        convertView.setVisibility(View.VISIBLE);
                        convertView.findViewById(R.id.badge_delete).setVisibility(View.INVISIBLE);
                    }
                    final String st10 = getResources().getString(R.string.The_delete_button_is_clicked);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EMLog.d(TAG, st10);
                            isInDeleteMode = true;
                            notifyDataSetChanged();
                        }
                    });
                }
            } else if (position == getCount() - 2) { // 添加群组成员按钮
                holder.textView.setText("");
                holder.imageView.setImageResource(R.drawable.em_smiley_add_btn);
//				button.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.smiley_add_btn, 0, 0);
                // 如果不是创建者或者没有相应权限
                if (!group.isAllowInvites() && !group.getOwner().equals(EMClient.getInstance().getCurrentUser())) {
                    // if current user is not group admin, hide add/remove btn
                    convertView.setVisibility(View.INVISIBLE);
                } else {
                    // 正处于删除模式下,隐藏添加按钮
                    if (isInDeleteMode) {
                        convertView.setVisibility(View.INVISIBLE);
                    } else {
                        convertView.setVisibility(View.VISIBLE);
                        convertView.findViewById(R.id.badge_delete).setVisibility(View.INVISIBLE);
                    }
                    final String st11 = getResources().getString(R.string.Add_a_button_was_clicked);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EMLog.d(TAG, st11);
                            // 进入选人页面
                            startActivityForResult(
                                    (new Intent(GroupDetailActivity.this, GroupPickContactsActivity.class).putExtra("groupId", groupId)),
                                    REQUEST_CODE_ADD_USER);
                        }
                    });
                }
            } else { // 普通item，显示群组成员
                final String username = getItem(position);
                convertView.setVisibility(View.VISIBLE);
                button.setVisibility(View.VISIBLE);
//				Drawable avatar = getResources().getDrawable(R.drawable.default_avatar);
//				avatar.setBounds(0, 0, referenceWidth, referenceHeight);
//				button.setCompoundDrawables(null, avatar, null, null);
                EaseUserUtils.setUserNick(username, holder.textView);
                EaseUserUtils.setUserAvatar(getContext(), username, holder.imageView);
                if (isInDeleteMode) {
                    // 如果是删除模式下，显示减人图标
                    convertView.findViewById(R.id.badge_delete).setVisibility(View.VISIBLE);
                } else {
                    convertView.findViewById(R.id.badge_delete).setVisibility(View.INVISIBLE);
                }
                final String st12 = getResources().getString(R.string.not_delete_myself);
                final String st13 = getResources().getString(R.string.Are_removed);
                final String st14 = getResources().getString(R.string.Delete_failed);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isInDeleteMode) {
                            // 如果是删除自己，return
                            if (EMClient.getInstance().getCurrentUser().equals(username)) {
                                new EaseAlertDialog(GroupDetailActivity.this, st12).show();
                                return;
                            }
                            if (!NetUtils.hasNetwork(getApplicationContext())) {
                                Toast.makeText(getApplicationContext(), getString(R.string.network_unavailable), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            EMLog.d("group", "remove user from group:" + username);
                            deleteMembersFromGroup(username);
                        } else {
                            // 正常情况下点击user，可以进入用户详情或者聊天页面等等
//                             startActivity(new
//                             Intent(GroupDetailsActivity.this,
//                            UserDetailsActivity.class).putExtra("userId",
//                             user.getUsername()));

                        }
                    }

                    /**
                     * 删除群成员
                     *
                     * @param username
                     */
                    protected void deleteMembersFromGroup(final String username) {
                        final ProgressDialog deleteDialog = new ProgressDialog(GroupDetailActivity.this);
                        deleteDialog.setMessage(st13);
                        deleteDialog.setCanceledOnTouchOutside(false);
                        deleteDialog.show();
                        new Thread(new Runnable() {

                            @Override
                            public void run() {
                                try {
                                    // 删除被选中的成员
                                    EMClient.getInstance().groupManager().removeUserFromGroup(groupId, username);
                                    isInDeleteMode = false;
                                    runOnUiThread(new Runnable() {

                                        @Override
                                        public void run() {
                                            deleteDialog.dismiss();
                                            refreshMembers();
                                            ((TextView) findViewById(R.id.group_name)).setText(group.getGroupName() + "("
                                                    + group.getAffiliationsCount() + st);
                                        }
                                    });
                                } catch (final Exception e) {
                                    deleteDialog.dismiss();
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), st14 + e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }

                            }
                        }).start();
                    }
                });


            }
            return convertView;
        }

        @Override
        public int getCount() {
            return super.getCount() + 2;
        }
    }

    private void refreshMembers() {
        adapter.clear();
        List<String> members = new ArrayList<String>();
        members.addAll(group.getMembers());
        adapter.addAll(members);

        adapter.notifyDataSetChanged();
    }

    private static class ViewHolder {
        ImageView imageView;
        TextView textView;
        ImageView badgeDeleteView;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_switch_block_groupmsg: // 屏蔽或取消屏蔽群组
                toggleBlockGroup();
                break;

            case R.id.clear_all_history: // 清空聊天记录
                String st9 = getResources().getString(R.string.sure_to_empty_this);
                new EaseAlertDialog(GroupDetailActivity.this, null, st9, null, new EaseAlertDialog.AlertDialogUser() {

                    @Override
                    public void onResult(boolean confirmed, Bundle bundle) {
                        if (confirmed) {
                            clearGroupHistory();
                        }
                    }
                }, true).show();

                break;


            case R.id.rl_change_group_name:
                startActivityForResult(new Intent(this, EditActivity.class).putExtra("data", group.getGroupName()), REQUEST_CODE_EDIT_GROUPNAME);
                break;

            default:
                break;
        }

    }

    private void clearGroupHistory() {
        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(group.getGroupId(), EMConversation.EMConversationType.GroupChat);
        if (conversation != null) {
            conversation.clearAllMessages();
        }
        Toast.makeText(this, R.string.messages_are_empty, Toast.LENGTH_SHORT).show();
    }

    private void toggleBlockGroup() {
        if (switchButton.isSwitchOpen()) {
            EMLog.d(TAG, "change to unblock group msg");
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(GroupDetailActivity.this);
                progressDialog.setCanceledOnTouchOutside(false);
            }
            progressDialog.setMessage(getString(R.string.Is_unblock));
            progressDialog.show();
            new Thread(new Runnable() {
                public void run() {
                    try {
                        EMClient.getInstance().groupManager().unblockGroupMessage(groupId);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                switchButton.closeSwitch();
                                progressDialog.dismiss();
                                Toast.makeText(GroupDetailActivity.this,"已取消屏蔽群消息。",Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), R.string.remove_group_of, Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                }
            }).start();

        } else {
            String st8 = getResources().getString(R.string.group_is_blocked);
            final String st9 = getResources().getString(R.string.group_of_shielding);
            EMLog.d(TAG, "change to block group msg");
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(GroupDetailActivity.this);
                progressDialog.setCanceledOnTouchOutside(false);
            }
            progressDialog.setMessage(st8);
            progressDialog.show();
            Model.getinstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        EMClient.getInstance().groupManager().blockGroupMessage(groupId);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                switchButton.openSwitch();
                                progressDialog.dismiss();
                                Toast.makeText(GroupDetailActivity.this,"已屏蔽群消息。",Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), st9, Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            });

        }
    }

    public void back(View view) {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance = null;
    }
}
