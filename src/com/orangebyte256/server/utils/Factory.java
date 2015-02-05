package com.orangebyte256.server.utils;

class HandlerConnectGet implements Handler
{
    public void handle(String s)
    {
    }
}

class HandlerConnectSend implements Handler
{
    public void handle(String s)
    {
        System.out.print(s);
    }
}

class Factory
{
    static Handler create(Command command)
    {
        switch (command)
        {
            case CONNECT_GET:
                return new HandlerConnectGet();
            case CONNECT_SEND:
                return new HandlerConnectSend();
            default:
                throw new Error();
        }
    }
}
