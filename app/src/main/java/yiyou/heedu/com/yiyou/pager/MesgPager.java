package yiyou.heedu.com.yiyou.pager;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.lang.reflect.Field;
import java.util.ArrayList;

import yiyou.heedu.com.yiyou.R;
import yiyou.heedu.com.yiyou.controler.activity.AddContactActivity;

import static android.content.ContentValues.TAG;
import static yiyou.heedu.com.yiyou.R.id.tv_mesg;
import static yiyou.heedu.com.yiyou.R.id.vPager_chat;

/**
 * Created by Administrator on 2016/10/23 0023.
 */



public class MesgPager extends Fragment implements View.OnClickListener {
    private TextView tv_contract;
    private ArrayList<Fragment> fmList;
    private MesgFragment_mesg fragment_mesg;
    private MesgFragment_contract fragment_contract;
   private FrameLayout vPager_chat;
    private View view;
    private int position=0;
    private TextView tvmesg;
    private RelativeLayout rightlayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.pager_mesg, null);
        vPager_chat = (FrameLayout) view.findViewById(R.id.vPager_chat);
        tvmesg = (TextView) view.findViewById(R.id.tv_mesg);
        tv_contract = (TextView) view.findViewById(R.id.tv_contract);
        rightlayout = (RelativeLayout) view.findViewById(R.id.rightlayout);
        initData();
        return view;
    }





    public void initData() {
        rightlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddContactActivity.class);
                startActivity(intent);
            }
        });
        Log.i(TAG, "initData: 本地视频的页面的数据被初始化了");
        tv_contract.setOnClickListener(this);
        tvmesg.setOnClickListener(this);
        fmList = new ArrayList<Fragment>();
        fragment_mesg = new MesgFragment_mesg();
        fragment_contract = new MesgFragment_contract();

        fmList.add(fragment_mesg);//0
        fmList.add(fragment_contract);//1
        setFrament();


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            default:
                position = 0;
                break;
            case R.id.tv_mesg:
               position = 0;
            break;
            case R.id.tv_contract:
                position = 1;
                break;
        }
        setFrament();
    }



    private void setFrament() {
        FragmentManager manager1 = getChildFragmentManager();
        FragmentTransaction ft1 = manager1.beginTransaction();
        ft1.replace(R.id.vPager_chat, fmList.get(position));
        ft1.commit();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


}
