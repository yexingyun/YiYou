package yiyou.heedu.com.yiyou.pager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import yiyou.heedu.com.yiyou.R;

import static android.content.ContentValues.TAG;

/**
 * Created by Administrator on 2016/10/23 0023.
 */

@SuppressLint("ValidFragment")
public class HomePager extends Fragment {

    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

         view = inflater.inflate(R.layout.pager_home, null);
        initData();
        return view;
    }



    public void initData() {
        Log.i(TAG, "initData: 联网请求数据");
    }


}
