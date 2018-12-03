package ella.idpchat;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import com.google.firebase.FirebaseApp;

public class ChatsListAdapter extends ArrayAdapter {

    private Context context;
    private List<Chat> chats;

    public ChatsListAdapter(Context context, List items) {
        super(context, android.R.layout.simple_list_item_1, items);
        this.context = context;
        chats = items;
    }

    @Override
    public int getCount() {
        return chats.size();
    }
    @Override
    public Object getItem(int pos) {
        return chats.get(pos);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final Chat item = (Chat)getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_chat, parent, false);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.nameTextView);
        textView.setText(item.getName());
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatWindow.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Log.v("CHATUNIQUEID", item.getUniqueId());
                intent.putExtra("chatUniqueId", item.getUniqueId());
                context.startActivity(intent);
            }
        });
        return convertView;
    }
}