package me.lammazz.poker.client;

import me.lammazz.poker.network.RequestConnectionPacket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Client implements Runnable{

    public String host, name;
    public int id;
    public boolean connected;
    private int port;

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    private boolean running = false;
    private EventListener listener;

    public Client(String host, int port, String name) {
        this.host = host;
        this.port = port;
        this.name = name;
        this.connected = false;
    }

    public boolean connect() {
        try {
            socket = new Socket(host,port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            listener = new EventListener();
            new Thread(this).start();
            return true;
        }catch(ConnectException e) {
            System.out.println("Unable to connect to the server");
        }catch(UnknownHostException e) {
            System.out.println("Unknown host");
        }catch(SocketException e) {
            System.out.println("Unable to connect to the server");
        }catch(IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void close() {
        close(true);
    }

    public void close(boolean sendPacket) {
        try {
            running = false;
            if (sendPacket) {
//                RemoveConnectionPacket packet = new RemoveConnectionPacket();
//                sendObject(packet);
            }
            in.close();
            out.close();
            socket.close();
        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void sendObject(Object packet) {
        try {
            out.writeObject(packet);
            out.flush();
        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            running = true;

            while(running) {
                try {
                    Object data = in.readObject();
                    listener.received(data, this);
                }catch(ClassNotFoundException e) {
                    e.printStackTrace();
                }catch(SocketException e) {
                    if (running) close();
                }
            }
        }catch(IOException e) {
            e.printStackTrace();
        }
    }

}