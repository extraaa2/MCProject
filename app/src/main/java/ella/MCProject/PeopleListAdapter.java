package ella.MCProject;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class PeopleListAdapter extends ArrayAdapter {

    private Context context;
    private List<User> friends;

    public PeopleListAdapter(Context context, List items) {
        super(context, android.R.layout.simple_list_item_1, items);
        this.context = context;
        friends = items;
    }

    @Override
    public int getCount() {
        return friends.size();
    }
    @Override
    public Object getItem(int pos) {
        return friends.get(pos);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final User item = (User)getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_people, parent, false);
        }

        TextView textView = convertView.findViewById(R.id.nameTextView);
        textView.setText(item.getName());
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Chat chat = new Chat();
                chat.addUser(LoginActivity.getMainUser());
                User user = LoginActivity.getMainUser().findFriend(item.getId());
                if (user != null)
                    chat.addUser(user);
                else {
                    Log.v("ADDCHAT", "user not found in friends");
                    return;
                }
                if (!MainActivity.getTabChats().hasChat(chat)) {
                    Log.v("ADDCHAT", item.getName());
                    MainActivity.getTabChats().addChat(chat);
                }
            }
        });
        return convertView;
    }
}