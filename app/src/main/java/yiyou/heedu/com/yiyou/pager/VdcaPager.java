package yiyou.heedu.com.yiyou.pager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import yiyou.heedu.com.yiyou.R;

/**
 * Created by Administrator on 2016/10/23 0023.
 */


public class VdcaPager extends Fragment {
    TextView textView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pager_vdc, null);
        View viewById = view.findViewById(R.id.vdc_title);
         textView = (TextView) viewById.findViewById(R.id.title_bar);
        textView.setText("表白墙");
        return view;


    }








}
