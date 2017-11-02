package yiyou.heedu.com.yiyou.model;

import android.content.Context;
import android.util.Log;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import yiyou.heedu.com.yiyou.model.dao.UserAccountDAO;
import yiyou.heedu.com.yiyou.model.db.DBManager;
import yiyou.heedu.com.yiyou.model.domain.UserInfo;

import static android.content.ContentValues.TAG;

/**
 * Created by Administrator on 2016/12/6 0006.
 */
//数据模型层全局类
public class Model {


    private static Model model = new Model();
    private Context mContext;
    private ExecutorService executors = Executors.newCachedThreadPool();
    private UserAccountDAO userAccountDAO;
    private DBManager dbManager;

    private Model() {

    }

    //获取单例对象
    public static Model getinstance() {
        return model;
    }

    public void inite(Context context) {
        mContext = context;
         userAccountDAO = new UserAccountDAO(mContext);
        Log.i(TAG, "inite: userAccountDAO");
        EventListener eventListener = new EventListener(mContext);
    }
    //获取全局线程池对象
    public ExecutorService getGlobalThreadPool() {
       return executors;
    }

    public UserAccountDAO getAccountDAO(){
        return userAccountDAO;
    }
    public void loginSuccess(UserInfo account) {
        if (account==null){
            return;
        }
        if (dbManager!=null){
            dbManager.close();
        }
         dbManager = new DBManager(mContext, account.getName());

    }
//获取dbManager对象
    public DBManager getDbManager(){
        return dbManager;
    }
}
