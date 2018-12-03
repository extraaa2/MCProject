package ella.MCProject;

import java.util.ArrayList;

public class User {

    private String id;

    private String name;

    private ArrayList<User> friends;

    public String getName() {
        return name;
    }

    public User(String name, String id){
        this.name = name;
        this.id = id;
        this.friends = new ArrayList<>();
    }

    public User() {
    }

    public String getId() {
        return id;
    }

    public ArrayList<User> getFriends() {
        return friends;
    }

    public void addFriend(User friend) {
        friends.add(friend);
    }

    public User findFriend(String id) {
        for (User friend: friends) {
            if (friend.getId().compareTo(id) == 0)
                return friend;
        }
        return null;
    }

    @Override
    public String toString() {
        return getName();
    }
}
