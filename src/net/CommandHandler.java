package net;

public abstract class CommandHandler {

    private int numParams;

    public CommandHandler(int numParams) {
        this.numParams = numParams;
    }

    public int numParams() {
        return numParams;
    }

    public abstract void processParams(User user, String[] params) throws CommandException;
}
