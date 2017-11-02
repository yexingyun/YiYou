package yiyou.heedu.com.yiyou.controler.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.easeui.adapter.EaseContactAdapter;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.widget.EaseSidebar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import yiyou.heedu.com.yiyou.R;
import yiyou.heedu.com.yiyou.model.Model;
import yiyou.heedu.com.yiyou.model.domain.UserInfo;
import yiyou.heedu.com.yiyou.utils.Constant;

/**
 * Created by Administrator on 2016/12/21 0021.
 */
public class GroupPickContactsActivity extends Activity {
    /** if this is a new group */
    protected boolean isCreatingNewGroup;
    private PickContactAdapter contactAdapter;
    /** members already in the group */
    private List<String> existMembers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.em_activity_group_pick_contacts);

        String groupId = getIntent().getStringExtra("groupId");
        if (groupId == null) {// create new group
            isCreatingNewGroup = true;
        } else {
            // get members of the group
            EMGroup group = EMClient.getInstance().groupManager().getGroup(groupId);
            existMembers = group.getMembers();
        }
        if(existMembers == null)
            existMembers = new ArrayList<String>();
        // get contact list
        final List<EaseUser> alluserList = new ArrayList<EaseUser>();
        // 获取数据
        List<UserInfo> contacts = Model.getinstance().getDbManager().getContactTableDao().getContacts();
        // 校验
        if (contacts != null && contacts.size() >= 0) {

            // 转换成EaseUser
            for (UserInfo contact : contacts) {
                EaseUser user = new EaseUser(contact.getHxid());
                if (!user.getUsername().equals(Constant.NEW_FRIENDS_USERNAME) & !user.getUsername().equals(Constant.GROUP_USERNAME))
                    alluserList.add(user);

            }
        }

//        for (EaseUser user : ) {
//            if (!user.getUsername().equals(Constant.NEW_FRIENDS_USERNAME) & !user.getUsername().equals(Constant.GROUP_USERNAME))
//                alluserList.add(user);
//        }
        // sort the list
        Collections.sort(alluserList, new Comparator<EaseUser>() {

            @Override
            public int compare(EaseUser lhs, EaseUser rhs) {
                if(lhs.getInitialLetter().equals(rhs.getInitialLetter())){
                    return lhs.getNick().compareTo(rhs.getNick());
                }else{
                    if("#".equals(lhs.getInitialLetter())){
                        return 1;
                    }else if("#".equals(rhs.getInitialLetter())){
                        return -1;
                    }
                    return lhs.getInitialLetter().compareTo(rhs.getInitialLetter());
                }

            }
        });

        ListView listView = (ListView) findViewById(R.id.list);
        contactAdapter = new PickContactAdapter(this, R.layout.em_row_contact_with_checkbox, alluserList);
        listView.setAdapter(contactAdapter);
        ((EaseSidebar) findViewById(R.id.sidebar)).setListView(listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
                checkBox.toggle();

            }
        });
    }

    /**
     * save selected members
     *
     * @param v
     */
    public void save(View v) {
        List<String> var = getToBeAddMembers();
        setResult(RESULT_OK, new Intent().putExtra("newmembers", var.toArray(new String[var.size()])));
        finish();
    }

    /**
     * get selected members
     *
     * @return
     */
    private List<String> getToBeAddMembers() {
        List<String> members = new ArrayList<String>();
        int length = contactAdapter.isCheckedArray.length;
        for (int i = 0; i < length; i++) {
            String username = contactAdapter.getItem(i).getUsername();
            if (contactAdapter.isCheckedArray[i] && !existMembers.contains(username)) {
                members.add(username);
            }
        }

        return members;
    }

    /**
     * adapter
     */
    private class PickContactAdapter extends EaseContactAdapter {

        private boolean[] isCheckedArray;

        public PickContactAdapter(Context context, int resource, List<EaseUser> users) {
            super(context, resource, users);
            isCheckedArray = new boolean[users.size()];
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);

            final String username = getItem(position).getUsername();

            final CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
            ImageView avatarView = (ImageView) view.findViewById(R.id.avatar);
            TextView nameView = (TextView) view.findViewById(R.id.name);

            if (checkBox != null) {
                if(existMembers != null && existMembers.contains(username)){
                    checkBox.setButtonDrawable(R.drawable.em_checkbox_bg_gray_selector);
                }else{
                    checkBox.setButtonDrawable(R.drawable.em_checkbox_bg_selector);
                }

                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        // check the exist members
                        if (existMembers.contains(username)) {
                            isChecked = true;
                            checkBox.setChecked(true);
                        }
                        isCheckedArray[position] = isChecked;

                    }
                });
                // keep exist members checked
                if (existMembers.contains(username)) {
                    checkBox.setChecked(true);
                    isCheckedArray[position] = true;
                } else {
                    checkBox.setChecked(isCheckedArray[position]);
                }
            }

            return view;
        }
    }

    public void back(View view){
        finish();
    }
}
