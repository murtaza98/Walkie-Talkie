package com.example.murtaza.walkietalkie;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

public class SocketHandler {
    private static DatagramSocket socket;
    private static InetAddress inetAddress;
    private static int PORT;

    public static InetAddress getInetAddress() {
        return inetAddress;
    }

    public static int getPORT() {
        return PORT;
    }

    public static void setInetAddress(InetAddress inetAddress) {
        SocketHandler.inetAddress = inetAddress;
    }

    public static void setPORT(int PORT) {
        SocketHandler.PORT = PORT;
    }

    public static synchronized DatagramSocket getSocket(){
        return socket;
    }

    public static synchronized void setSocket(DatagramSocket socket){
        SocketHandler.socket = socket;
    }
}
