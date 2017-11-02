package yiyou.heedu.com.yiyou.model.domain;

/**
 * Created by Administrator on 2016/12/8 0008.
 */

public class UserInfo {
    private String name;
    private String hxid;
    private String nick;
    private String photo;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHxid() {
        return hxid;
    }

    public void setHxid(String hxid) {
        this.hxid = hxid;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }


    public UserInfo() {

    }
    public UserInfo(String name) {
        this.name = name;
        this.hxid = name;
        this.nick = name;
        this.photo = name;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "name='" + name + '\'' +
                ", hxid='" + hxid + '\'' +
                ", nick='" + nick + '\'' +
                ", photo='" + photo + '\'' +
                '}';
    }
}
