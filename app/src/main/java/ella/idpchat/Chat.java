package ella.idpchat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Chat {
    ArrayList<User> parts;

    Chat() {
        parts = new ArrayList<>();
    }

    public void addUser(User user) {
        parts.add(user);
    }

    public ArrayList<User> getParts() {
        return parts;
    }
    public String getName() {
        String names = "";
        int index = 0;
        int size = parts.size();
        for (User user : parts) {
            if (index != 0 && index < size - 1)
                names += user.getName() + ", ";
            else if (index == size - 1)
                names += user.getName();
            ++index;
        }
        return names;
    }

    String getUniqueId() {
        ArrayList<User> copy = new ArrayList<>(parts);
        Collections.sort(copy, new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                return o1.getId().compareTo(o2.getId());
            }
        });
        String r = "";
        for (User user : copy) {
            r += user.getId() + " ";
        }
        return r;
    }

    @Override
    public String toString() {
        return getName();
    }
}
