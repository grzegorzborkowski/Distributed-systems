package Chat.chat;

import org.jgroups.JChannel;

import java.util.*;

public class State {

    private Map<String, List<String>> channelState;
    private Map<String, JChannel> channelMap;

    public State() {
        this.channelState = new LinkedHashMap<>();
        this.channelMap = new HashMap<>();
    }

    public Map<String, List<String>> getChannelState() {
        return channelState;
    }


    //TODO: refactor
    void addUserToState(String channel, String nickname) {
        if(channelState.containsKey(channel)) {
            if(channelState.get(channel) == null) {
                List<String> users = new ArrayList<>();
                users.add(nickname);
                channelState.put(channel, users);
            } else {
                List<String> users = channelState.get(channel);
                users.add(nickname);
                channelState.put(channel, users);
            }
        } else {
            List<String> users = new ArrayList<>();
            users.add(nickname);
            channelState.put(channel, users);
        }
        System.out.println("a user" + nickname + " joined to channel:" + channel);
    }

    void removeUserFromState(String channel, String nickname) {
        if(channelState.containsKey(channel)) {
            List<String> users = channelState.get(channel);
            users.remove(nickname);
            channelState.put(channel, users);
        }
        System.out.println("a user" + nickname + " removed from channel:" + channel);

    }
}
