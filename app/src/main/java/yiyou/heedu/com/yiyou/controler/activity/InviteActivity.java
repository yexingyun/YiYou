package yiyou.heedu.com.yiyou.controler.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.List;

import yiyou.heedu.com.yiyou.R;
import yiyou.heedu.com.yiyou.controler.adapter.InviteAdapter;
import yiyou.heedu.com.yiyou.model.Model;
import yiyou.heedu.com.yiyou.model.domain.InvationInfo;
import yiyou.heedu.com.yiyou.utils.Constant;

/**
 * Created by Administrator on 2016/12/12 0012.
 */
public class InviteActivity extends Activity {
    private ListView lvIvate;
    private InviteAdapter.OnInviteListener mOnInviteOnListener = new InviteAdapter.OnInviteListener() {
        @Override
        public void onAccept(final InvationInfo invationInfo) {
            Model.getinstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    //去环信服务器同意请求
                    try {
                        EMClient.getInstance().contactManager().acceptInvitation(invationInfo.getUser().getHxid());
                        //本地数据库的更新
                        Model.getinstance().getDbManager().getInviteTableDao().updateInvitationStatus(InvationInfo.InvitationStatus.INVITE_ACCEPT, invationInfo.getUser().getHxid());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                //页面刷新
                                Toast.makeText(InviteActivity.this, "已接受邀请", Toast.LENGTH_SHORT).show();
                                refresh();
                            }
                        });

                    } catch (HyphenateException e) {
                        //页面刷新
                        Toast.makeText(InviteActivity.this, "接受邀请失败", Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }

        @Override
        public void onReject(final InvationInfo invationInfo) {
            Model.getinstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    //去环信服务器拒绝请求
                    try {
                        EMClient.getInstance().contactManager().declineInvitation(invationInfo.getUser().getHxid());
                        //本地数据库的更新
                        Model.getinstance().getDbManager().getInviteTableDao().removeInvitation(invationInfo.getUser().getHxid());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //页面刷新
                                Toast.makeText(InviteActivity.this, "已拒绝邀请", Toast.LENGTH_SHORT).show();
                                refresh();
                            }
                        });

                    } catch (HyphenateException e) {
                        e.printStackTrace();
                    }
                }
            });

        }

        @Override
        public void onInviteAccept(final InvationInfo invationInfo) {
            Model.getinstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        // 告诉环信服务器接受了邀请
                        EMClient.getInstance().groupManager().acceptInvitation(invationInfo.getGroup().getGroupId(), invationInfo.getGroup().getInvatePerson());

                        // 本地数据更新
                        invationInfo.setStatus(InvationInfo.InvitationStatus.GROUP_ACCEPT_INVITE);
                        Model.getinstance().getDbManager().getInviteTableDao().addInvitation(invationInfo);

                        // 内存数据的变化
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "接受邀请", Toast.LENGTH_SHORT).show();

                                // 刷新页面
                                refresh();
                            }
                        });
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "接受邀请失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }

        @Override
        public void onInviteReject(final InvationInfo invationInfo) {
            Model.getinstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        // 告诉环信服务器拒绝了邀请
                        EMClient.getInstance().groupManager().declineInvitation(invationInfo.getGroup().getGroupId(), invationInfo.getGroup().getInvatePerson(),"拒绝邀请");

                        // 更新本地数据库
                        invationInfo.setStatus(InvationInfo.InvitationStatus.GROUP_REJECT_INVITE);
                        Model.getinstance().getDbManager().getInviteTableDao().addInvitation(invationInfo);

                        // 更新内存的数据
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "拒绝邀请", Toast.LENGTH_SHORT).show();

                                // 刷新页面
                                refresh();
                            }
                        });
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        @Override
        public void onApplicationAccept(final InvationInfo invationInfo) {
            Model.getinstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        // 告诉环信服务器接受了申请
                        EMClient.getInstance().groupManager().acceptApplication(invationInfo.getGroup().getGroupId(), invationInfo.getGroup().getInvatePerson());

                        // 更新数据库
                        invationInfo.setStatus(InvationInfo.InvitationStatus.GROUP_ACCEPT_APPLICATION);
                        Model.getinstance().getDbManager().getInviteTableDao().addInvitation(invationInfo);

                        // 更新内存
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "接受申请", Toast.LENGTH_SHORT).show();

                                refresh();
                            }
                        });
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        @Override
        public void onApplicationReject(final InvationInfo invationInfo) {
            Model.getinstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        // 告诉环信服务器拒绝了申请
                        EMClient.getInstance().groupManager().declineApplication(invationInfo.getGroup().getGroupId(),invationInfo.getGroup().getInvatePerson(),"拒绝申请");

                        // 更新本地数据库
                        invationInfo.setStatus(InvationInfo.InvitationStatus.GROUP_REJECT_APPLICATION);
                        Model.getinstance().getDbManager().getInviteTableDao().addInvitation(invationInfo);

                        // 更新内存
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "拒绝申请", Toast.LENGTH_SHORT).show();

                                refresh();
                            }
                        });
                    } catch (HyphenateException e) {
                        e.printStackTrace();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "拒绝申请失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }

    };
    private InviteAdapter inviteAdapter;
    private LocalBroadcastManager mLBM;
    private BroadcastReceiver InviteChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refresh();
        }
    };
    private List<InvationInfo> invitations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.row_invite_msg);
        initView();
        initData();


    }

    private void initView() {
        lvIvate = (ListView) findViewById(R.id.lv_invite);
    }

    private void initData() {

        inviteAdapter = new InviteAdapter(this, mOnInviteOnListener);
        lvIvate.setAdapter(inviteAdapter);
        refresh();

        // 注册邀请信息变化的广播
        mLBM = LocalBroadcastManager.getInstance(this);
        mLBM.registerReceiver(InviteChangedReceiver, new IntentFilter(Constant.CONTACT_INVITE_CHANGED));
        mLBM.registerReceiver(InviteChangedReceiver, new IntentFilter(Constant.GROUP_INVITE_CHANGED));
    }

    private void refresh() {
        invitations = Model.getinstance().getDbManager().getInviteTableDao().getInvitations();
        inviteAdapter.refresh(invitations);
        inviteAdapter.refresh(invitations);

    }


    public void back(View v) {
        finish();
    }
}
