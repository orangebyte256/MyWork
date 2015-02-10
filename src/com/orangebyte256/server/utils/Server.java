package com.orangebyte256.server.utils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import com.orangebyte256.server.utils.Command;
import com.orangebyte256.server.utils.Factory;
import com.sun.xml.internal.ws.util.ByteArrayBuffer;

class SocketProcessor implements Runnable
{
    private Socket s;
    private InputStream inputStream;
    private OutputStream outputStream;
    private String ConnectionName;

    private boolean Autorize(String s)
    {
        String names[] = s.split(",");
        if(names.length != 2)
        {
            System.out.print("Wrong authtorization, try again");
            return false;
        }
        ConnectionName = names[0];
        return true;
    }
    private String ReadMessage(BufferedReader reader)
    {
        String result = "";
        String d = "ga12";
        try
        {
            int count = reader.read();
            do {
                char buf[] = new char[count - result.length()];
                int readed = reader.read(buf, 0, count - result.length());
                result += (new String(buf)).substring(0, readed);
            }
            while(result.length() != count);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    private void WriteMessage(OutputStream outputStream, String message)
    {
//        message = Character.toChars(message.length()) + message;
//        byte[] b = message.getBytes();
        try {
//            PrintWriter writer = new PrintWriter(outputStream, true);
//            writer.println(message);
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
    SocketProcessor(Socket s)
    {
        this.s = s;
        try
        {
            inputStream = s.getInputStream();
            outputStream = s.getOutputStream();
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
            BufferedReader br = null;
            try {
                br = new BufferedReader(new InputStreamReader(inputStream, "UTF16"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
/*            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(fileDir), "UTF8"));
*/
/*            String temp = ReadMessage(br);
            while(!Autorize(temp))
            {
                WriteMessage(outputStream, "Repeat");
            }
*/            WriteMessage(outputStream, "Okey, write this ok 12122");
/*            while(true)
            {
                String s = br.readLine();
                if(s == null || s.trim().length() == 0)
                {
                    break;
                }
                System.out.print(s);
            }
        } catch (Throwable t) {
                /*do nothing*/

        } finally {
            try {
                s.close();
            } catch (Throwable t) {
                    /*do nothing*/
            }
        }
        System.err.println("Client processing finished");
    }

/*    private void writeResponse(String s) throws Throwable {
        String response = "HTTP/1.1 200 OK\r\n" +
                "Server: YarServer/2009-09-09\r\n" +
                "Content-Type: text/html\r\n" +
                "Content-Length: " + s.length() + "\r\n" +
                "Connection: close\r\n\r\n";
        String result = response + s;
        os.write(result.getBytes());
        os.flush();
    }

    private void readInputHeaders() throws Throwable {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        while(true) {
            String s = br.readLine();
            if(s == null || s.trim().length() == 0) {
                break;
            }
        }
    }
    */
}

public class Server
{
    public Server(int port)
    {
        try
        {
            ServerSocket ss = new ServerSocket(port);
            while (true)
            {
                Socket s = ss.accept();
                System.err.println("Client accepted");
                new Thread(new SocketProcessor(s)).start();
            }
        }
        catch (Throwable e)
        {
            System.err.println("Client accepted");
        }
    }
}

