package net;

public class NickNameHandler extends CommandHandler {

    private static int MAX_NICKNAME_LENGTH = 9;

    
    public NickNameHandler() {
        super(1);
    }

    @Override
    public void processParams(User user, String[] params) {
        String newNickName = params[0];
        if (newNickName.length() > MAX_NICKNAME_LENGTH) {
            newNickName = newNickName.substring(0, MAX_NICKNAME_LENGTH);
        }

        String oldNickName = user.nickName();
        user.setNickName(newNickName);

        String reply = new StringBuilder("NICK ")
            .append(oldNickName)
            .append(" ")
            .append(newNickName)
            .toString();

        ReplyHelper.sendUsers(User.list(), new StringWebSocketMessage(reply));
    }
}
