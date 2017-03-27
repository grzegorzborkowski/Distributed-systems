package Chat.Client;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

import Chat.chat.ChannelsUtils;
import Chat.chat.ManagmentChannel;
import Chat.chat.State;
import com.google.protobuf.InvalidProtocolBufferException;
import org.jgroups.*;
import org.jgroups.util.Util;
import pl.edu.agh.dsrg.sr.chat.protos.ChatOperationProtos;


// TODO: refactor extract two classes, client which handles menu and calls actions; chat handling everything else
public class ChatClient extends ReceiverAdapter {
    private Scanner scanner;
    private String nickname;
    private Map<String, JChannel> activeChannels;
    private ManagmentChannel managmentChannel;
    private State state;

    private ChatClient() throws Exception {
        System.setProperty("java.net.preferIPv4Stack", "true");
        this.scanner = new Scanner(System.in);
        getNickname();
        this.activeChannels = new HashMap<>();
        this.managmentChannel = new ManagmentChannel();
        this.state = new State();
        this.managmentChannel.getChannel().getState(null, 2000);
    }

    public static void main(String[] args) throws Exception {
        new ChatClient().runChat();
    }

    private void runChat() throws Exception {

        while(true) {
            String line = this.scanner.nextLine();
            if(line.startsWith("quit")) {
                break;
            }
            String[] split = line.split(" ");
            switch (split[0]) {
                case "join":
                    joinChannel(split[1]);
                case "list":
                    break;
                case "send":
                    sendMessage(split[1], split);
                    break;
                case "leave":
                    leaveChannel(split[1]);
                    break;
                case "help":
                    printHelp();
                    break;
            }
        }
    }

    private void printHelp() {
        System.out.println("Possible commands:");
        System.out.println("* join <channel_address>");
        System.out.println("* leave <channel_address>");
        System.out.println("* list (list all channels)");
        System.out.println("* send <channel_address> <message>");
        System.out.println("* help");
        System.out.println("* quit ");

    }

    private void sendMessage(String channelName, String[] split) throws Exception {
        JChannel destinationChannel = activeChannels.get(channelName);
        List splitArrayList = new ArrayList<>(Arrays.asList(split));
        splitArrayList.remove(0);
        splitArrayList.add(0, "channel");
        splitArrayList.add(2, ">");
        String messageContent = (String) splitArrayList.stream().collect(Collectors.joining(" "));
        ChatOperationProtos.ChatMessage chatMessage = ChatOperationProtos.ChatMessage.newBuilder().
                setMessage(messageContent).build();
        Message message = new Message(null, null, chatMessage.toByteArray());
        destinationChannel.send(message);

    }

    private void joinChannel(String channelName) {
        if (activeChannels.containsKey(channelName)) {
            System.out.printf("You already joined channel: %s! \n", channelName);
        } else {
            try {
            JChannel jChannel = ChannelsUtils.resolveChannelFromName(channelName);
            jChannel.setReceiver(this);
            jChannel.setName(nickname);
            jChannel.connect(channelName);
            ChannelsUtils.sendJoinMessage(nickname, channelName, managmentChannel);
            this.activeChannels.put(channelName, jChannel);
            } catch (Exception e) {
                System.out.printf("Error while connecting to the channel");
                e.printStackTrace();
            }
        }
    }

    private void leaveChannel(String channelName) {
        JChannel jChannel = activeChannels.get(channelName);
        if(jChannel == null) {
            System.out.println("You haven't joined this channel yet!");
        } else {
            activeChannels.remove(channelName);
            jChannel.close();
            ChannelsUtils.sendLeaveMessage(nickname, channelName, managmentChannel);
        }
    }

    private void getNickname() {
        System.out.println("Hello. Write your nickname, please :");
        this.nickname = this.scanner.nextLine();
    }

    @Override
    public void receive(Message msg) {
        try {
            String chatMessage = ChatOperationProtos.ChatMessage.
                    parseFrom(msg.getBuffer()).getMessage();
            System.out.println(msg.getSrc() + " from " + chatMessage);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void viewAccepted(View new_view) {
        System.out.println("** view: " + new_view);
    }


    @Override
    public void getState(OutputStream output) throws Exception {
        ChatOperationProtos.ChatState.Builder chatState = ChatOperationProtos.ChatState.newBuilder();
        synchronized(state) {
            for (Map.Entry<String, List<String>> entry : state.getChannelState().entrySet()) {
                for (String userName: entry.getValue()) {
                    ChatOperationProtos.ChatAction action = ChatOperationProtos
                            .ChatAction
                            .newBuilder()
                            .setChannel(entry.getKey())
                            .setNickname(userName)
                            .setAction(ChatOperationProtos.ChatAction.ActionType.JOIN)
                            .build();
                    chatState.addState(action);
                }
            }
            byte [] chatStateBytes = chatState.build().toByteArray();
            output.write(chatStateBytes);
        }
    }

    @Override
    public void setState(InputStream inputStream) throws Exception {
        synchronized(state) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[16384];

            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            ChatOperationProtos.ChatState newState = ChatOperationProtos.ChatState.parseFrom(buffer.toByteArray());
            state.getChannelState().clear();
            for(ChatOperationProtos.ChatAction action: newState.getStateList()) {
                String channelName = action.getChannel();
                String userName = action.getNickname();
                state.getChannelState().get(channelName).add(userName);
            }
        }
    }

}
