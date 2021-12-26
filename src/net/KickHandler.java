package net;

public class KickHandler extends CommandHandler {

    public KickHandler() {
        super(2);
    }

    @Override
    public void processParams(User user, String[] params) throws CommandException {
        String channelName = params[0];
        String targetNickName = params[1];
        Channel channel = Channel.get(channelName);

        if (channel == null) {
            return;
        }

        if (!channel.users().contains(user)) {
            //error
            return;
        }

        if (!channel.roles(user).hasRole(Channel.Role.OPERATOR)) {
            return;
        }

        User targetUser = channel.getUser(targetNickName);
        if (targetUser == null) {
            return;
        }

        if (!channel.users().contains(targetUser)) {
            //error
            return;
        }

        if (channel.roles(targetUser).hasRole(Channel.Role.OPERATOR)) {
            return;
        }
        
        String reply = new StringBuilder("KICK ")
            .append(user.nickName())
            .append(" ")
            .append(channelName)
            .append(" ")
            .append(targetNickName)
            .toString();
        
        ReplyHelper.sendChannel(channel, new StringWebSocketMessage(reply));

        channel.removeUser(targetUser);
    }
    
}
