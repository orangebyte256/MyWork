package com.orangebyte256.server.utils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.concurrent.Callable;
import com.orangebyte256.server.utils.Command;
import com.orangebyte256.server.utils.Factory;

class SocketProcessor implements Runnable
{
    private Socket s;
    private InputStream inputStream;
    private OutputStream outputStream;
    private String ConnectionName;

    private boolean Autorize(String s)
    {
        String names[] = s.split("//");
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
        int count = 0;
        try {
            count = reader.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        char []temp = new char[count];
        try {
            reader.read(temp, 0, count);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return temp.toString();
    }
    private void WriteMessage(OutputStream outputStream, String message)
    {
        message = Character.toChars(message.length()) + message;
        try {
            outputStream.write(message.getBytes());
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
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String temp = ReadMessage(br);
            while(!Autorize(temp))
            {
                WriteMessage(outputStream, "Repeat");
            }
            WriteMessage(outputStream, "Ok");
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

