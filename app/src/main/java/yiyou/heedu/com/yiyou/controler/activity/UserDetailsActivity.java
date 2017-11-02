package yiyou.heedu.com.yiyou.controler.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.easeui.EaseConstant;

import yiyou.heedu.com.yiyou.R;
import yiyou.heedu.com.yiyou.view.CircleImageView;

/**
 * Created by Administrator on 2016/12/12 0012.
 */
public class UserDetailsActivity extends FragmentActivity implements View.OnClickListener {
    private String userID;
    private RelativeLayout title;
    private LinearLayout llBack;
    private TextView addListFriends;
    private TextView more;
    private RelativeLayout rlUserdeta;
    private CircleImageView civUserimg;
    private TextView tvUsernick;
    private TextView tvHxid;
    private EditText etSetremark;
    private Button btnSavemark;
    private LinearLayout llUserdetaPubmdc;
    private TextView tvPubmcd;
    private LinearLayout llVdc;
    private TextView tvVdc;
    private Button sendmsg;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2016-12-19 17:57:20 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        title = (RelativeLayout)findViewById( R.id.title );
        llBack = (LinearLayout)findViewById( R.id.ll_back );
        addListFriends = (TextView)findViewById( R.id.add_list_friends );
        more = (TextView)findViewById( R.id.more );
        rlUserdeta = (RelativeLayout)findViewById( R.id.rl_userdeta );
        civUserimg = (CircleImageView)findViewById( R.id.civ_userimg );
        tvUsernick = (TextView)findViewById( R.id.tv_usernick );
        tvHxid = (TextView)findViewById( R.id.tv_hxid );
        etSetremark = (EditText)findViewById( R.id.et_setremark );
        btnSavemark = (Button)findViewById( R.id.btn_savemark );
        llUserdetaPubmdc = (LinearLayout)findViewById( R.id.ll_userdeta_pubmdc );
        tvPubmcd = (TextView)findViewById( R.id.tv_pubmcd );
        llVdc = (LinearLayout)findViewById( R.id.ll_vdc );
        tvVdc = (TextView)findViewById( R.id.tv_vdc );
        sendmsg = (Button)findViewById( R.id.sendmsg );

        btnSavemark.setOnClickListener( this );
        sendmsg.setOnClickListener( this );
        llBack.setOnClickListener(this);
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2016-12-19 17:57:20 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if ( v == btnSavemark ) {
            // Handle clicks for btnSavemark
        } else if ( v == sendmsg ) {
            // 发消息   跳转到聊天页面
            Intent intent  = new Intent(UserDetailsActivity.this,ChatActivity.class);
            intent.putExtra(EaseConstant.EXTRA_USER_ID,userID);
            startActivity(intent);
        }else if (v.getId()==R.id.ll_back) {
           finish();
        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
setContentView(R.layout.activity_userdetails);
        findViews();
        //获取ID
         userID = getIntent().getStringExtra(EaseConstant.EXTRA_USER_ID);
        initdata();
    }

    private void initdata() {
        tvUsernick.setText(userID);
        tvHxid.setText("易友号:"+userID);
    }
}
