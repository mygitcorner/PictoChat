package net;

public class JoinHandler extends CommandHandler {
    
    public JoinHandler() {
        super(1);
    }

    @Override
    public void processParams(User user, String[] params) {
        if (!user.hasNickName()) {
            return;
        }

        String channelName = params[0];

        if (!channelName.startsWith("#")) {
            return;
        }

        Channel channel = Channel.create(channelName);
        if (!channel.addUser(user)) {
            //error
            return;
        }

        StringBuilder reply = new StringBuilder("JOIN ")
            .append(channel.name())
            .append(" ")
            .append(user.nickName());

        for (User channelUser : channel.users()) {
            reply.append(" ").append(channelUser.nickName());
            
            for (Channel.Role role : channel.roles(channelUser).list()) {
                reply.append(" -").append(role.symbol());
            }
        }

        ReplyHelper.sendChannel(channel, new StringWebSocketMessage(reply.toString()));
    }

}
