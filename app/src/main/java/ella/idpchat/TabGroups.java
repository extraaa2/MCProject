package ella.idpchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class TabGroups extends Fragment {
    private static ArrayList<Chat> groups = new ArrayList<>();
    GroupListAdapter adapter;
    DatabaseReference databaseRef;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.tab_groups, container, false);
        super.onActivityCreated(savedInstanceState);

        adapter = new GroupListAdapter(getActivity().getApplicationContext(), groups);
        ListView listView = (ListView) rootView.findViewById(R.id.tab_group_view);
        listView.setAdapter(adapter);

        // set listener
        FirebaseApp app = FirebaseApp.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance(app);

        databaseRef = database.getReference("chat");

        databaseRef.addChildEventListener(new ChildEventListener() {
            public void onChildAdded(DataSnapshot snapshot, String s) {
                // Get the chat message from the snapshot and add it to the UI
                String chatUniqueId = snapshot.getKey();
                if (chatUniqueId.indexOf(LoginActivity.getMainUser().getId()) != -1) {
                    Chat group = new Chat();
                    String []usernames = chatUniqueId.split(" ");
                    User mainUser = LoginActivity.getMainUser();
                    group.addUser(mainUser);
                    for (String friendId : usernames) {
                        User friend = mainUser.findFriend(friendId);
                        if (friend == null) {
                            Log.v("FRIENDNOTFOUND", friendId);
                            if (friendId.compareTo(mainUser.getId()) != 0) {
                                friend = new User("unknown", friendId);
                                Log.v("FRIEND", friend.getName());
                                group.addUser(friend);
                            }
                        } else {
                            Log.v("FRIEND", friend.getName());
                            group.addUser(friend);
                        }
                    }
                    if (group.getParts().size() > 2 && !hasChat(group)) {
                        groups.add(group);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            public void onChildChanged(DataSnapshot dataSnapshot, String s) { }
            public void onChildRemoved(DataSnapshot dataSnapshot) { }
            public void onChildMoved(DataSnapshot dataSnapshot, String s) { }
            public void onCancelled(DatabaseError databaseError) { }
        });

        FloatingActionButton addGroup = (FloatingActionButton) rootView.findViewById(R.id.add_group);
        addGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("GROUP", "add_group button clicked");
                Intent intent = new Intent(getActivity(), GroupAdd.class);
                startActivity(intent);
            }
        });

        return rootView;
    }

    public void addChat(Chat chat) {
        groups.add(chat);
        Log.v("ADDGROUP ", "TabChats: " + groups.toString());
        adapter.notifyDataSetChanged();
    }

    public boolean hasChat(Chat chat) {
        ArrayList<User> list1 = chat.getParts();

        for (Chat c: groups) {
            ArrayList<User> list2 = c.getParts();
            int count = 0;

            if (list1.size() != list2.size())
                return false;

            for (User u1: list1) {
                for (User u2: list2) {
                    if (u1.getId().compareTo(u2.getId()) == 0) {
                        ++count;
                    }
                }
            }
            if (count == list1.size())
                return true;
        }
        return false;
    }
}
