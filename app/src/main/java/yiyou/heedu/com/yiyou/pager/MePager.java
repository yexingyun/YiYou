package yiyou.heedu.com.yiyou.pager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.smile.filechoose.api.ChosenFile;
import com.smile.filechoose.api.FileChooserManager;
import com.yancy.imageselector.ImageConfig;
import com.yancy.imageselector.ImageLoader;
import com.yancy.imageselector.ImageSelector;
import com.yancy.imageselector.ImageSelectorActivity;

import java.io.File;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.ProgressCallback;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import yiyou.heedu.com.yiyou.R;
import yiyou.heedu.com.yiyou.controler.activity.LoginActivity;
import yiyou.heedu.com.yiyou.controler.activity.RegisterActivity;
import yiyou.heedu.com.yiyou.model.Model;
import yiyou.heedu.com.yiyou.model.domain.Imguser;
import yiyou.heedu.com.yiyou.model.domain.UserInfo;
import yiyou.heedu.com.yiyou.utils.SpUtils;
import yiyou.heedu.com.yiyou.view.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;
import static yiyou.heedu.com.yiyou.controler.activity.BaseActivity.log;

/**
 * Created by Administrator on 2016/10/23 0023.
 */


public class MePager extends Fragment implements View.OnClickListener {
//    private static final String SAVE_PIC_PATH= Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED) ? Environment.getExternalStorageDirectory().getAbsolutePath() : /mnt/sdcard;//保存到SD卡
//    private static final String SAVE_REAL_PATH = SAVE_PIC_PATH+"/yiyou/savePic";//保存的确切位置
    private RelativeLayout rlHead;
    private RelativeLayout rlRegLogin;
    private CircleImageView meIvUserimg;
    private Button btnReg;
    private Button btnLogin;
    private RelativeLayout rlAfRegLogin;
    private CircleImageView meAfIvUserimg;
    private TextView tvUsername;
    private LinearLayout llMygoods;
    private LinearLayout llMyvdc;
    private LinearLayout llSetting;
    private Button btnLogout;

    private View view;
    private ChosenFile choosedFile;

    private ProgressDialog dialog;
    private String url;
    private FileChooserManager fm;
    private String path;
    private String userimgurl;
    private String filename;
    private String username;

    public MePager(){

    }
    private void findViews() {
        rlHead = (RelativeLayout) view.findViewById(R.id.rl_head);
        rlRegLogin = (RelativeLayout) view.findViewById(R.id.rl_reg_login);
        rlAfRegLogin = (RelativeLayout) view.findViewById(R.id.rl_af_reg_login);
        meIvUserimg = (CircleImageView) view.findViewById(R.id.me_iv_userimg);
        btnReg = (Button) view.findViewById(R.id.btn_reg);
        btnLogin = (Button) view.findViewById(R.id.btn_login);
        meAfIvUserimg = (CircleImageView) view.findViewById(R.id.me_af_iv_userimg);
        tvUsername = (TextView) view.findViewById(R.id.tv_username);
        llMygoods = (LinearLayout) view.findViewById(R.id.ll_mygoods);
        llMyvdc = (LinearLayout) view.findViewById(R.id.ll_myvdc);
        llSetting = (LinearLayout) view.findViewById(R.id.ll_setting);
        btnLogout = (Button) view.findViewById(R.id.btn_logout);

        btnReg.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
        meAfIvUserimg.setOnClickListener(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (view!=null){
            isloginAndChangeRL_head();
        }


    }

    @Override
    public void onClick(View v) {
        if (v == btnReg) {
            // Handle clicks for btnReg
            Intent intent = new Intent(getActivity(), RegisterActivity.class);
            this.startActivity(intent);
        } else if (v == btnLogin) {
            // Handle clicks for btnLogin
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            this.startActivity(intent);
        } else if (v == btnLogout) {
            // Handle clicks for btnLogout 退出登录的业务逻辑处理
            Logout();
        }else if (v == meAfIvUserimg) {
            // 更换头像
            insertDataWithOne();

        }
    }

    /** 插入单条数据（单个BmobFile列）
     */
    private void insertDataWithOne(){
        if(choosedFile ==null){

           Toast.makeText(getActivity(), "请先选择文件",
                    Toast.LENGTH_SHORT).show();
            pickFile();

            return;
        }
    }
    public void pickFile() {
        ImageConfig imageConfig = new ImageConfig.Builder(new GlideLoader())
                .steepToolBarColor(getResources().getColor(R.color.blue))
                .titleBgColor(getResources().getColor(R.color.blue))
                .titleSubmitTextColor(getResources().getColor(R.color.white))
                .titleTextColor(getResources().getColor(R.color.white))
                // (截图默认配置：关闭    比例 1：1    输出分辨率  500*500)
                .crop(1, 2, 500, 1000)
                // 开启单选   （默认为多选）
                .singleSelect()
                // 开启拍照功能 （默认关闭）
                .showCamera()
                // 拍照后存放的图片路径（默认 /temp/picture） （会自动创建）
                .filePath("/ImageSelector/Pictures")
                .build();


        ImageSelector.open(MePager.this, imageConfig);   // 开启图片选择器


    }

    private void Logout() {

            new AlertDialog.Builder(getActivity()).setTitle("确认退出吗？")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 点击“确认”后的操作
                            logout_progress();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {// 点击“返回”后的操作,这里不设置没有任何操作
                            return;
                        }
                    }).show();
    }
    private void logout_progress() {
        final ProgressDialog pd = new ProgressDialog(getActivity());
        pd.setMessage("正在退出...");
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        EMClient.getInstance().logout(false,new EMCallBack() {

            @Override
            public void onSuccess() {

               Model.getinstance().getGlobalThreadPool().execute(new Runnable() {
                    public void run() {
                        pd.dismiss();
                        //关闭数据库
                        Model.getinstance().getDbManager().close();
                        SpUtils.getInstance().save("userimgurl","");
                        // show login screen
                        getActivity().finish();
                        getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));

                    }
                });
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                Model.getinstance().getGlobalThreadPool().execute (new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        pd.dismiss();
                        Toast.makeText(getActivity(), "退出失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.pager_me, null);
       findViews();
        String userimgurltemp = SpUtils.getInstance().getString("userimgurl", "");
        if (!userimgurltemp.equals("")&&userimgurltemp!=null){
            loadUserImg(userimgurltemp);
        }
        isloginAndChangeRL_head();
        return view;
    }


    public void initData() {
        Log.i(TAG, "我的页面的数据被初始化了");



    }

    private void isloginAndChangeRL_head() {
        boolean loggedInBefore = EMClient.getInstance().isLoggedInBefore();
        UserInfo account;
        if (loggedInBefore) {//已经登录过，获取账号信息并显示
            account = Model.getinstance().getAccountDAO().getAccount(EMClient.getInstance().getCurrentUser());
            rlAfRegLogin.setVisibility(View.VISIBLE);
            rlRegLogin.setVisibility(View.GONE);
            tvUsername.setText(account.getName());
            username = EMClient.getInstance().getCurrentUser().toString();
            queryuserimg();


        }else{//没有登录，就显示提示登录页面，
            rlAfRegLogin.setVisibility(View.GONE);
            rlRegLogin.setVisibility(View.VISIBLE);
        }

    }

    private void queryuserimg() {
        BmobQuery<Imguser> query = new BmobQuery<Imguser>();
//查询playerName叫“比目”的数据
        query.addWhereEqualTo("name", username);
//返回50条数据，如果不加上这条语句，默认返回10条数据
        query.setLimit(10);
//执行查询方法
        query.findObjects(new FindListener<Imguser>() {
            @Override
            public void done(List<Imguser> object, BmobException e) {
                if(e==null){
                  //  toast();
                    Toast.makeText(getActivity(), "查询成功：共"+object.size()+"条数据。",
                            Toast.LENGTH_SHORT).show();
                    for (Imguser movie : object) {
                        //获得playerName的信息
                        String name = movie.getName();
                        BmobFile file = movie.getFile();
                        String fileUrl = file.getFileUrl();
                        Toast.makeText(getActivity(), name+fileUrl,
                                Toast.LENGTH_SHORT).show();
                        loadUserImg(fileUrl);
                        //获得数据的objectId信息
//                        gameScore.getObjectId();
//                        //获得createdAt数据创建时间（注意是：createdAt，不是createAt）
//                        gameScore.getCreatedAt();
                    }
                }else{
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ImageSelector.IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {

            // Get Image Path List
            List<String> pathList = data.getStringArrayListExtra(ImageSelectorActivity.EXTRA_RESULT);

            for (String p : pathList) {
                path = p;
            }
            File img = new File(path);

            uploadMovoieFile(img);
        }
    }

    private void uploadMovoieFile(File file) {

        dialog = new ProgressDialog(getActivity());
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setTitle("上传中...");
        dialog.setIndeterminate(false);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        final BmobFile bmobFile = new BmobFile(file);
        bmobFile.uploadObservable(new ProgressCallback() {//上传文件操作
            @Override
            public void onProgress(Integer value, long total) {
                log("uploadMovoieFile-->onProgress:"+value);
                dialog.setProgress(value);
            }
        }).doOnNext(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                 url = bmobFile.getUrl();
                SpUtils.getInstance().save("userimgurl",url);
                 filename = bmobFile.getFilename();
                //MediaStore.Images.Media.insertImage(getContentResolver(), "image path", "title", "description");

//                try {
//                    Bitmap bitmap = null;
//                    saveFile(bitmap,filename,SAVE_REAL_PATH);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

                log("上传成功："+url+","+bmobFile.getFilename());

                loadUserImg(url);
            }
        }).concatMap(new Func1<Void, Observable<String>>() {//将bmobFile保存到movie表中
            @Override
            public Observable<String> call(Void aVoid) {
                Imguser movie = new Imguser(username, bmobFile);
                return saveObservable(movie);
            }
        }).concatMap(new Func1<String, Observable<String>>() {//下载文件
            @Override
            public Observable<String> call(String s) {
                return bmobFile.downloadObservable(new ProgressCallback() {
                    @Override
                    public void onProgress(Integer value, long total) {
                        log("download-->onProgress:"+value+","+total);
                    }
                });
            }
        }).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                log("--onCompleted--");
            }

            @Override
            public void onError(Throwable e) {
                log("--onError--:"+e.getMessage());
                dialog.dismiss();
               // choosedFile=null;
            }

            @Override
            public void onNext(String s) {
                dialog.dismiss();
              userimgurl = s ;
                log("download的文件地址："+s);
            }
        });
    }

    private void loadUserImg(String url) {
        String URL  = url;
        Glide.with(MePager.this)
                .load(URL)
                .centerCrop()
                .into(meAfIvUserimg);

//        bitmap = Glide.with(MePager.this)
//                .load(url)
//                .asBitmap()
//                ;

    }


    private Observable<String> saveObservable(BmobObject obj){
        return obj.saveObservable();
    }



    private class GlideLoader implements ImageLoader {
        @Override
        public void displayImage(Context context, String path, ImageView imageView) {
            Glide.with(context)
                    .load(path)
                    .placeholder(com.yancy.imageselector.R.mipmap.imageselector_photo)
                    .centerCrop()
                    .into(imageView);
        }

    }

}
