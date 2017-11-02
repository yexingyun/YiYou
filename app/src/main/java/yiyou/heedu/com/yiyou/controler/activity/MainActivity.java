package yiyou.heedu.com.yiyou.controler.activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.Toast;


import java.util.ArrayList;

import yiyou.heedu.com.yiyou.R;
import yiyou.heedu.com.yiyou.pager.MesgPager;
import yiyou.heedu.com.yiyou.pager.MePager;
import yiyou.heedu.com.yiyou.pager.VdcaPager;
import yiyou.heedu.com.yiyou.pager.HomePager;

/**
 * Created by Administrator on 2016/10/17 0017.
 */
public class MainActivity extends FragmentActivity  {

    public FrameLayout fl_main_content;
    private RadioGroup rg_bottom_tag;

    int position;
    private ArrayList<Fragment> basePagers;

    public  MainActivity(){

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fl_main_content = (FrameLayout) findViewById(R.id.fl_main_content);
        rg_bottom_tag = (RadioGroup) findViewById(R.id.rg_bottom_tag);

        basePagers = new ArrayList<>();
        basePagers.add(new HomePager());//0
        basePagers.add(new MesgPager());//1
        basePagers.add(new VdcaPager());//2
        basePagers.add(new MePager());//3
        rg_bottom_tag.setOnCheckedChangeListener(new MyOncheckedChangeListener());
        rg_bottom_tag.check(R.id.rb_home);
    }



    class MyOncheckedChangeListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.rb_mesg:
                    position = 1;
                    break;
                case R.id.rb_vdc:
                    position = 2;
                    break;
                case R.id.rb_me:
                    position = 3;
                    break;
                default:
                    position = 0;
                    break;
            }
            setFragment();
        }
    }

    private void setFragment() {
        /**
         * 得到fragment事物，
         * 开启事物
         * 替换
         * 提交事务
         */

       //自定义Fragment

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.replace(R.id.fl_main_content, basePagers.get(position));
        ft.commit();

    }
   private boolean isExit;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==event.KEYCODE_BACK){
            if (position!=0){
                position=0;
                rg_bottom_tag.check(R.id.rb_home);
                return true;
            }else if (!isExit){
                isExit = true;
                Toast.makeText(this,"再按一次退出",Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isExit =false;
                    }
                }, 2000);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }






}
