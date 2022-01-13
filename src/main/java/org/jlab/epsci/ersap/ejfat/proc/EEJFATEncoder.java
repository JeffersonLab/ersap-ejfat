package org.jlab.epsci.ersap.ejfat.proc;

import org.jlab.epsci.ersap.engine.Engine;
import org.jlab.epsci.ersap.engine.EngineData;
import org.jlab.epsci.ersap.engine.EngineDataType;
import org.jlab.jnp.hipo.data.HipoEvent;
import org.json.JSONObject;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Set;

/**
 * Copyright (c) 2021, Jefferson Science Associates, all rights reserved.
 * See LICENSE.txt file.
 * Thomas Jefferson National Accelerator Facility
 * Experimental Physics Software and Computing Infrastructure Group
 * 12000, Jefferson Ave, Newport News, VA 23606
 * Phone : (757)-269-7100
 *
 * @author gurjyan on 1/13/22
 * @project ersap-ejfat
 */
public class EEJFATEncoder implements Engine {

    private static final String UDP_HOST = "udp_host";
    private static final String UDP_PORT = "udp_port";
    private static final String UDP_CLIENT_PORT = "udp_client_port";

    private String udpHost;
    private int udpPort = 1234;
    private int udpClientPort =5678;

    private InetAddress address;
    private DatagramPacket packet;
    private DatagramSocket socket;

    @Override
    public EngineData configure(EngineData input) {
        System.out.println("EEJFATEncoder engine configure...");
        if (input.getMimeType().equalsIgnoreCase(EngineDataType.JSON.mimeType())) {
            String source = (String) input.getData();
            JSONObject data = new JSONObject(source);
            if (data.has(UDP_HOST)) {
                udpHost = data.getString(UDP_HOST);
            }
            if (data.has(UDP_PORT)) {
                udpPort = data.getInt(UDP_PORT);
            }
            if (data.has(UDP_CLIENT_PORT)) {
                udpClientPort = data.getInt(UDP_CLIENT_PORT);
            }
        }

        try {
            address = InetAddress.getByName(udpHost);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public EngineData execute(EngineData input) {
        HipoEvent event = (HipoEvent) input.getData();
        ByteBuffer bb = ByteBuffer.wrap(event.getDataBuffer());

        byte[] data = bb.array();
        byte[] lbMeta = new byte[16];
        byte[] reMeta = new byte[16];
        byte[] ba = new byte[data.length + lbMeta.length + reMeta.length];

        // populate and add arrays

        packet = new DatagramPacket(ba, ba.length, address, udpPort);
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public EngineData executeGroup(Set<EngineData> inputs) {
        return null;
    }

    @Override
    public Set<EngineDataType> getInputDataTypes() {
        return null;
    }

    @Override
    public Set<EngineDataType> getOutputDataTypes() {
        return null;
    }

    @Override
    public Set<String> getStates() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getVersion() {
        return null;
    }

    @Override
    public String getAuthor() {
        return null;
    }

    @Override
    public void reset() {

    }

    @Override
    public void destroy() {

    }
}
