package org.jlab.epsci.ersap.ejfat.io.be;

import com.lmax.disruptor.RingBuffer;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Copyright (c) 2021, Jefferson Science Associates, all rights reserved.
 * See LICENSE.txt file.
 * Thomas Jefferson National Accelerator Facility
 * Experimental Physics Software and Computing Infrastructure Group
 * 12000, Jefferson Ave, Newport News, VA 23606
 * Phone : (757)-269-7100
 *
 * @author gurjyan on 2/21/22
 * @project ersap-ejfat
 */
public class ReAssembleReceiver extends Thread {
    private DataInputStream dataInputStream;
    private ServerSocket serverSocket;
    private int ejfatPort;

    private RingBuffer<RingEjfatEvent> ringBuffer;
    private long sequenceNumber;

    private AtomicBoolean running = new AtomicBoolean(true);

    public ReAssembleReceiver(int port, RingBuffer<RingEjfatEvent> ringBuffer) {
        ejfatPort = port;
        this.ringBuffer = ringBuffer;
    }

    private RingEjfatEvent get() throws InterruptedException {
        sequenceNumber = ringBuffer.next();
        return ringBuffer.get(sequenceNumber);
    }

    private void publish() {
        ringBuffer.publish(sequenceNumber);
    }

    @Override
    public void run() {
        super.run();
        try {
            serverSocket = new ServerSocket(ejfatPort);
            System.out.println("INFO ERSAP is listening on port " + ejfatPort);
            Socket socket = serverSocket.accept();
            System.out.println("INFO EJFAT client connected");
            InputStream input = socket.getInputStream();
            dataInputStream = new DataInputStream(new BufferedInputStream(input, 65536));
        } catch (
                IOException e) {
            e.printStackTrace();
        }
        while (running.get()) {
            try {
                // Get an empty item from ring
                RingEjfatEvent event = get();

                // get the size of the reassembled event
                short sz = dataInputStream.readShort();
                int size = Integer.reverseBytes(Short.toUnsignedInt(sz));
                System.out.println(String.format("DDD size = %02X", size) + "  " + size );
                byte[] payload = new byte[size];
                dataInputStream.readFully(payload);
                System.out.println("blob size = " + payload.length);
                event.setPayload(payload);

                // Make the buffer available for consumers
                publish();
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }

        }
    }
    public void exit() {
        running.set(false);
        try {
            dataInputStream.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.interrupt();
    }

}
