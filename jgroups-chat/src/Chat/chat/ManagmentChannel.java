package Chat.chat;

import com.google.protobuf.InvalidProtocolBufferException;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import pl.edu.agh.dsrg.sr.chat.protos.ChatOperationProtos;

public class ManagmentChannel extends ReceiverAdapter {
    private static final String MANAGMENT_CHANNEL_NAME = "ChatManagement321321";
    private static final String MANAGMENT_CHANNEL_ADDERSS = "230.0.0.36";
    private JChannel jChannel;
    private State state;

    public ManagmentChannel() throws Exception {
        System.setProperty("java.net.preferIPv4Stack", "true");
        this.jChannel = ChannelsUtils.createChannel(MANAGMENT_CHANNEL_ADDERSS);
        this.jChannel.setReceiver(this);
        this.jChannel.setName(MANAGMENT_CHANNEL_NAME);
        this.jChannel.connect(MANAGMENT_CHANNEL_NAME);
        this.state = new State();
    }

    @Override
    public void receive(Message msg) {
        try {
        ChatOperationProtos.ChatAction chatAction = ChatOperationProtos.ChatAction.
                    parseFrom(msg.getBuffer());
        ChatOperationProtos.ChatAction.ActionType action = chatAction.getAction();
        String channel = chatAction.getChannel();
        String nickname = chatAction.getNickname();
        switch (action) {
            case JOIN:
                addUserToState(channel, nickname);
                break;
            case LEAVE:
                removeUserFromState(channel, nickname);
                break;
        }
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    private void addUserToState(String channel, String nickname) {
      this.state.addUserToState(channel, nickname);
        System.out.println(this.state.getChannelState());

    }

    private void removeUserFromState(String channel, String nickname) {
        this.state.removeUserFromState(channel, nickname);
        System.out.println(this.state.getChannelState());
    }

    public JChannel getChannel() {
        return jChannel;
    }


}
