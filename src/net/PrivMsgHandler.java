package net;

public class PrivMsgHandler extends CommandHandler {
    

    public PrivMsgHandler() {
        super(2);
    }

    @Override
    public void processParams(User user, String[] params) {
        String receiver = params[0];
        String message = params[1];

        if (receiver.startsWith("#")) {
            Channel channel = Channel.get(receiver);
            if (channel == null) {
                //error
                return;
            }

            String reply = new StringBuilder("PRIVMSG ")
                .append(channel.name())
                .append(" ")
                .append(user.nickName())
                .append(" ")
                .append(message)
                .toString();

            ReplyHelper.sendChannel(channel, new StringWebSocketMessage(reply));
        } else {
            //direct message
        }
    }


}
