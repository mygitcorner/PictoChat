package net;

public class CommandException extends Exception {

    public enum Error {
        
    }
    
    public CommandException(String message) {
        super(message);
    }
}
