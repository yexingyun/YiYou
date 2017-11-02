package yiyou.heedu.com.yiyou.controler.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/6/8.
 */

public class HomePagerAdapter extends Adapter {
    private static int currentType = 0;
    private static final int banner = 0;
    public static final  int ACT = 3;
    public static final  int CHANNER = 4;
    private final Context mContext;
    private final LayoutInflater minflater;

    public HomePagerAdapter(Context context, ArrayList arrayList) {
        mContext = context;
        minflater = LayoutInflater.from(context);
    }

    //创建ViewHolder
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }
//绑定数据模块
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }
//总共多少个item
    @Override
    public int getItemCount() {
        return 1;
    }

    //得到类型。
    @Override
    public int getItemViewType(int position) {
        switch (position){
            case banner:
                currentType = banner;
                break;
            case ACT:
                currentType = ACT;
                break;
            case CHANNER:
                currentType = CHANNER;
                break;
        }

        return super.getItemViewType(position);
    }
}
