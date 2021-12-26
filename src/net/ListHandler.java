package net;

public class ListHandler extends CommandHandler {

    public ListHandler() {
        super(0);
    }

    @Override
    public void processParams(User user, String[] params) {
        StringBuilder reply = new StringBuilder("LIST");

        Channel.list().stream()
            .map(Channel::name)
            .forEach(name -> reply.append(" ").append(name));

        ReplyHelper.sendUser(user, new StringWebSocketMessage(reply.toString()));
    }
    
}
