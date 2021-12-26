package net;

public class OperHandler extends CommandHandler {
    
    public OperHandler() {
        super(2);
    }

    @Override
    public void processParams(User user, String[] params) {
        String channelName = params[0];
        String password = params[1];
        Channel channel = Channel.get(channelName);

        if (channel == null) {
            //error
            return;
        }

        if (!channel.operatorPassword().equals(password)) {
            //error
            return;
        }

        if (channel.roles(user).setRole(Channel.Role.OPERATOR)) {
            String reply = new StringBuilder("OPER ")
                .append(channelName)
                .append(" ")
                .append(user.nickName())
                .toString();

            ReplyHelper.sendChannel(channel, new StringWebSocketMessage(reply));
        }
    }
}
