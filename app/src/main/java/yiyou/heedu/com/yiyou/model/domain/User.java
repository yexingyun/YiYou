package yiyou.heedu.com.yiyou.model.domain;

import cn.bmob.v3.BmobUser;

/**
 * Created by Administrator on 2017/5/24.
 */

public class User extends BmobUser {

    private Integer age;

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public User() {
    }

}
