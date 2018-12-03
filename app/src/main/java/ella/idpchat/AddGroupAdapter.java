package ella.idpchat;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AddGroupAdapter extends ArrayAdapter {

    private Context context;
    private List<User> friends;
    private List<User> groupUsers;

    public AddGroupAdapter(Context context, List items) {
        super(context, android.R.layout.simple_list_item_1, items);
        this.context = context;
        friends = items;
        groupUsers = new ArrayList<User>();
        groupUsers.add(LoginActivity.getMainUser());
    }

   public List<User> getFriends() {
        return groupUsers;
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
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_group, parent, false);
        }

        final TextView textView = (TextView) convertView.findViewById(R.id.nameTextView);
        textView.setText(item.getName());
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("GROUP", "add friend to group " + item.getName());
                groupUsers.add(item);
                textView.setBackgroundColor(222);
            }
        });
        return convertView;
    }
}