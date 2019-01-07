package ella.MCProject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

public class TabPeople extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.tab_people, container, false);
        super.onActivityCreated(savedInstanceState);

        ArrayList<User> friends = PrefUtils.getCurrentUser(getActivity().getApplicationContext()).getFriends();
        Log.v("FRIENDS ", friends.toString());

        PeopleListAdapter adapter = new PeopleListAdapter(getActivity().getApplicationContext(), friends);
        ListView listView = rootView.findViewById(R.id.tab_people_view);
        listView.setAdapter(adapter);
        return rootView;
    }
}
