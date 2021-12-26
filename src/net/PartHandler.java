package net;

public class PartHandler extends CommandHandler {
    
    public PartHandler() {
        super(1);
    }

    @Override
    public void processParams(User user, String[] params) {
        String channelName = params[0];
        Channel channel = Channel.get(channelName);
        if (channel == null) {
            //error
            return;
        }

        if (!user.hasChannel(channel)) {
            //error
            return;
        }
        
        String reply = new StringBuilder("PART ")
            .append(channel.name())
            .append(" ")
            .append(user.nickName())
            .toString();
        
        ReplyHelper.sendChannel(channel, new StringWebSocketMessage(reply));

        channel.removeUser(user);
    }

    
}
