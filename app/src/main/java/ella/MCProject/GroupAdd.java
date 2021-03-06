package ella.MCProject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;

public class GroupAdd extends AppCompatActivity {
    private static final String TAG = "GroupAdd";

    private Button addBtn;
    private ListView peopleList;
    private AddGroupAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v("GROUP", "GroupAdd on Create");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_add_window);

        addBtn = findViewById(R.id.addBtn);
        peopleList = findViewById(R.id.group_people_view);

        adapter = new AddGroupAdapter(getApplicationContext(), PrefUtils.getCurrentUser(getApplicationContext()).getFriends());
        peopleList.setAdapter(adapter);

        addBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                List<User> users = adapter.getFriends();
                Chat chat = new Chat();
                for(User user : users) {
                    chat.addUser(user);
                }
                if (!MainActivity.getTabGroups().hasChat(chat)) {
                    MainActivity.getTabGroups().addChat(chat);
                }
            }
        });
    }
}
