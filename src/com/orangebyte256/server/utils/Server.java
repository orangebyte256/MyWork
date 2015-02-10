package com.orangebyte256.server.utils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import com.orangebyte256.server.utils.Command;
import com.orangebyte256.server.utils.Factory;
import com.sun.xml.internal.ws.util.ByteArrayBuffer;


class Connection
{
    private InputStream inputStream;
    private OutputStream outputStream;
    BufferedReader bufferedReader;
     Connection(InputStream theInputStream, OutputStream theOutputStream)
    {
        inputStream = theInputStream;
        outputStream = theOutputStream;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF16"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    String ReadMessage()
    {
        String result = "";
        try
        {
            int count = bufferedReader.read();
            do {
                char buf[] = new char[count - result.length()];
                int readed = bufferedReader.read(buf, 0, count - result.length());
                result += (new String(buf)).substring(0, readed);
            }
            while(result.length() != count);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    void WriteMessage(String message)
    {
        try
        {
            byte[] data = new byte[message.length() + 2];
            byte[] message_byte = message.getBytes();
            data[0] = (byte)(message.length() / 256);
            data[1] = (byte)(message.length() % 256);
            System.arraycopy(message_byte, 0, data, 2, message.length());
            outputStream.write(data);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class OutputConnection
{
    private Connection connection;
    OutputConnection(InputStream theInputStream, OutputStream theOutputStream)
    {
        connection = new Connection(theInputStream, theOutputStream);
    }
    void Send()
    {
        while(true)
        {
            connection.WriteMessage("1");
            String result = connection.ReadMessage();
            if(result == "ok")
            {
                break;
            }
        }
    }
}

class InputConnection extends Thread
{
    private Connection connection;
    private HashMap<String, OutputConnection> outputConnectionHashMap;
    private String name;
    InputConnection(InputStream theInputStream, OutputStream theOutputStream,
                    HashMap<String, OutputConnection> theOutputConnectionHashMap, String theName)
    {
        connection = new Connection(theInputStream, theOutputStream);
        outputConnectionHashMap = theOutputConnectionHashMap;
        name = theName;
    }
    @Override
    public void run()
    {
        while(true)
        {
            String s = connection.ReadMessage();
            outputConnectionHashMap.get(s).Send();
        }
    }
}

class MainSocket extends Thread
{
    private Socket s;
    private  Connection connection;
    private InputStream inputStream;
    private OutputStream outputStream;
    private String connectionName;
    private Command signalType;
    private ArrayList<InputConnection> inputConnectionArrayList;
    private HashMap<String, OutputConnection> outputConnectionHashMap;
    private boolean Autorize(String s)
    {
        byte command = (byte)s.charAt(0);
        if(command > Command.values().length)
        {
            System.out.print("Wrong authtorization, try again");
            return false;
        }
        signalType = Command.values()[command];
        s = s.substring(1, s.length());
        String names[] = s.split(",");
        if(names.length != 2)
        {
            System.out.print("Wrong authtorization, try again");
            return false;
        }
        connectionName = names[0];
        return true;
    }
    MainSocket(Socket s, ArrayList<InputConnection> theInputConnectionArrayList, HashMap<String, OutputConnection> theOutputConnectionHashMap)
    {
        this.s = s;
        try
        {
            InputStream inputStream = s.getInputStream();
            OutputStream outputStream = s.getOutputStream();
            connection = new Connection(inputStream, outputStream);
            inputConnectionArrayList = theInputConnectionArrayList;
            outputConnectionHashMap = theOutputConnectionHashMap;
        }
        catch(Throwable e)
        {
            System.out.print("error,trust me");
        }
    }

    public void run()
    {
        try
        {
            String temp = connection.ReadMessage();
            while(!Autorize(temp))
            {
                connection.WriteMessage("Repeat");
            }
            connection.WriteMessage("Okey");
            if(signalType == Command.CONNECT_GET)
            {
                outputConnectionHashMap.put(connectionName, new OutputConnection(inputStream, outputStream));
            }
            else
            {
                InputConnection inputConnection = new InputConnection(inputStream, outputStream,
                        outputConnectionHashMap, connectionName);
                inputConnection.run();
                inputConnectionArrayList.add(inputConnection);
            }
        } finally {
/*            try {
                s.close();
            } catch (Throwable t) {
                    /*do nothing
           }
*/        }
        System.err.println("Client processing finished");
    }

}

public class Server
{
    private ArrayList<InputConnection> inputConnections = new ArrayList<InputConnection>();
    private HashMap<String, OutputConnection> outputConnections = new HashMap<String, OutputConnection>();
    public Server(int port)
    {
        try
        {
            ServerSocket ss = new ServerSocket(port);
            while (true)
            {
                Socket s = ss.accept();
                System.err.println("Client accepted");
                new MainSocket(s, inputConnections, outputConnections).run();
            }
        }
        catch (Throwable e)
        {
            System.err.println("Client accepted");
        }
    }
}

