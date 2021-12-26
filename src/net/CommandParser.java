package net;

import java.util.HashMap;
import java.util.Map;

public class CommandParser {
    private Map<String, CommandHandler> handlers;

    public CommandParser() {
        handlers = new HashMap<>();
    }

    public void addHandler(String command, CommandHandler handler) {
        handlers.put(command, handler);
    }

    public void processRequest(User user, String request) {
        int firstSeparatorIndex = request.indexOf(" ");
        String command = firstSeparatorIndex == -1 
                        ? request 
                        : request.substring(0, firstSeparatorIndex);

        try {
            CommandHandler handler = handlers.get(command);
            if (handler == null) {
                return;
            }

            String[] params = handler.numParams() > 0 
                            ? request.substring(firstSeparatorIndex + 1)
                                    .split(" ", handler.numParams())
                            : new String[]{};

            handler.processParams(user, params);
        } catch (CommandException e) {
            //ReplyHelper.sendError(user, e.getMessage());
        }
    }
}
