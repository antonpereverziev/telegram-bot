package me.ap.home.rpi.helper.station.udpserver;

import lombok.extern.slf4j.Slf4j;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

@Slf4j
public class Server extends Thread {

    private UdpLogServerService udpLogServerService;
    private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[256];

    public Server(UdpLogServerService udpLogServerService) throws SocketException {
        this.udpLogServerService = udpLogServerService;
        socket = new DatagramSocket(5555);
    }

    public void run() {
        running = true;
        while (running) {
            try {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                packet = new DatagramPacket(buf, buf.length, address, port);
                String received = new String(packet.getData(), 0, packet.getLength());
                log.info("Log message received: {}", received);
                if (received.contains("EventStart") && received.contains("SmartMotionHuman")) {
                    udpLogServerService.processMotionEvent();
                }
                socket.send(packet);
            } catch (Exception e) {
                log.error("Error in UDP server", e);
            }
        }
        socket.close();
    }
}