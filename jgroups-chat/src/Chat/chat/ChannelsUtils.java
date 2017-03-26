package Chat.chat;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.protocols.*;
import org.jgroups.protocols.pbcast.*;
import org.jgroups.stack.ProtocolStack;
import pl.edu.agh.dsrg.sr.chat.protos.ChatOperationProtos;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ChannelsUtils {

    public static JChannel resolveChannelFromName(String channelName) throws Exception {
        InetAddress address = getAddressFromChannelName(channelName);
        return getChannelFromNameAndAddress(channelName, address);

    }

    private static JChannel getChannelFromNameAndAddress(String channelName, InetAddress address) throws Exception {
        JChannel jChannel = ChannelsUtils.createChannel(channelName);
        return jChannel;
    }

    private static InetAddress getAddressFromChannelName(String channelName) throws UnknownHostException {
        InetAddress address = InetAddress.getByName(channelName);
        return address;
    }

    public static JChannel createChannel(String multicastAddress) throws Exception {
        JChannel jChannel = new JChannel(false);
        ProtocolStack protocolStack = new ProtocolStack();
        jChannel.setProtocolStack(protocolStack);
        UDP udp = new UDP();
        if (multicastAddress != null) {
            udp.setValue("mcast_group_addr", InetAddress.getByName(multicastAddress));
        }
        protocolStack.addProtocol(udp)
                .addProtocol(new PING())
                .addProtocol(new MERGE3())
                .addProtocol(new FD_SOCK())
                .addProtocol(new FD_ALL().setValue("timeout", 12000).setValue("interval", 3000))
                .addProtocol(new VERIFY_SUSPECT())
                .addProtocol(new BARRIER())
                .addProtocol(new NAKACK2())
                .addProtocol(new UNICAST3())
                .addProtocol(new STABLE())
                .addProtocol(new GMS())
                .addProtocol(new UFC())
                .addProtocol(new MFC())
                .addProtocol(new FRAG2())
                .addProtocol(new STATE_TRANSFER())
                .addProtocol(new FLUSH());
        protocolStack.init();
        return jChannel;
    }

    // TODO: refactor those two methods
    public static void sendJoinMessage(String nickname, String channelName, ManagmentChannel managmentChannel) {
        ChatOperationProtos.ChatAction chatAction = ChatOperationProtos.ChatAction.newBuilder().
                setAction(ChatOperationProtos.ChatAction.ActionType.JOIN).setChannel(channelName)
                .setNickname(nickname).build();
        Message message = new Message(null, null, chatAction.toByteArray());
        try {
            managmentChannel.getChannel().send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendLeaveMessage(String nickname, String channelName, ManagmentChannel managmentChannel) {
        ChatOperationProtos.ChatAction chatAction = ChatOperationProtos.ChatAction.newBuilder().
                setAction(ChatOperationProtos.ChatAction.ActionType.LEAVE).setChannel(channelName)
                .setNickname(nickname).build();
        Message message = new Message(null, null, chatAction.toByteArray());
        try {
            managmentChannel.getChannel().send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
