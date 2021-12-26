package net;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint("/chats")
public class ChatEndpoint {
    private static CommandParser cmdParser = new CommandParser();

    private User user;

    static {
        cmdParser.addHandler("NICK", new NickNameHandler());
        cmdParser.addHandler("JOIN", new JoinHandler());
        cmdParser.addHandler("PART", new PartHandler());
        cmdParser.addHandler("PRIVMSG", new PrivMsgHandler());
        cmdParser.addHandler("LIST", new ListHandler());
        cmdParser.addHandler("OPER", new OperHandler());
        cmdParser.addHandler("KICK", new KickHandler());

        Channel.create("#testchannel1").setOperatorPassword("pass");
        Channel.create("#testchannel2").setOperatorPassword("pass");
        Channel.create("#testchannel3").setOperatorPassword("pass");
        Channel.create("#形声字☻♥☺聲字").setOperatorPassword("pass");
    }

    @OnOpen
    public void onOpen(Session session, EndpointConfig conf) {
        user = new User(session);
        session.getUserProperties().put("user", user);
    }

    @OnMessage
    public void textMessage(Session session, String request) {
        cmdParser.processRequest(user, request);
    }

    @OnMessage
    public void binaryMessage(Session session, ByteBuffer buffer) {
         BufferedReader reader = new BufferedReader(
            new InputStreamReader(
                new ByteArrayInputStream(buffer.array()),
                StandardCharsets.UTF_8));
        try {
            String channelName = reader.readLine();
            Channel channel = Channel.get(channelName);

            ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(bytesOut);
            
            String params = user.nickName() + " " + channelName + "\n"; 
            out.write(params.getBytes(StandardCharsets.UTF_8));

            byte[] imgBytes = Arrays.copyOfRange(buffer.array(), channelName.getBytes(StandardCharsets.UTF_8).length + 1, buffer.limit());
            out.write(imgBytes);

            ReplyHelper.sendChannel(channel, new BinaryWebSocketMessage(ByteBuffer.wrap(bytesOut.toByteArray())));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnClose
    public void close(Session session, CloseReason reason) {
        user.remove();
    }
}