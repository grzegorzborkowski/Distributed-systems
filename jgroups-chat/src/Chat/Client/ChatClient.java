package Chat.Client;

import java.util.*;
import java.util.stream.Collectors;

import Chat.chat.ChannelsUtils;
import com.google.protobuf.InvalidProtocolBufferException;
import org.jgroups.*;
import pl.edu.agh.dsrg.sr.chat.protos.ChatOperationProtos;


public class ChatClient extends ReceiverAdapter {
    private Scanner scanner;
    private String nickname;
    private Map<String, JChannel> activeChannels;

    private ChatClient() {
        this.scanner = new Scanner(System.in);
        this.activeChannels = new HashMap<>();
    }

    private void runChat() throws Exception {
        getNickname();

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
                case "help":
                   printHelp();
            }
        }
    }

    private void printHelp() {
        System.out.println("Possible commands:");
        System.out.println("* join <channel_address>");
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
            System.out.printf(String.valueOf(jChannel));
            jChannel.setReceiver(this);
            jChannel.setName(nickname);
            jChannel.connect(channelName);
            ChannelsUtils.sendJoinMessage(nickname, channelName);
            this.activeChannels.put(channelName, jChannel);
            } catch (Exception e) {
                System.out.printf("Error while connecting to the channel");
                e.printStackTrace();
            }
        }
    }

    private void getNickname() {
        System.out.println("Hello. Write your nickname, please :");
        this.nickname = this.scanner.nextLine();
    }

    public static void main(String[] args) throws Exception {
        new ChatClient().runChat();
    }

    @Override
    public void receive(Message msg) {
        try {
            String chatMessage = ChatOperationProtos.ChatMessage.parseFrom(msg.getBuffer()).getMessage();
            System.out.println(msg.getSrc() + "from " + chatMessage);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void viewAccepted(View new_view) {
        System.out.println("** view: " + new_view);
    }

}
