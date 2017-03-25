package Chat.chat;

import org.jgroups.JChannel;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ChannelsUtils {

    public static JChannel resolveChannelFromName(String channelName) throws Exception {
        InetAddress address = getAddressFromChannelName(channelName);
        return getChannelFromNameAndAddress(channelName, address);

    }

    private static JChannel getChannelFromNameAndAddress(String channelName, InetAddress address) throws Exception {
        JChannel jChannel = new JChannel();
        return jChannel;
    }

    private static InetAddress getAddressFromChannelName(String channelName) throws UnknownHostException {
        InetAddress address = InetAddress.getByName(channelName);
        return address;

    }

    public static void sendJoinMessage(String nickname, String channelName) {

    }
}
