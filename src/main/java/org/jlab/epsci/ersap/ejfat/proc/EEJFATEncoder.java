package org.jlab.epsci.ersap.ejfat.proc;

import org.jlab.epsci.ersap.engine.Engine;
import org.jlab.epsci.ersap.engine.EngineData;
import org.jlab.epsci.ersap.engine.EngineDataType;
import org.jlab.io.base.DataBank;
import org.jlab.io.hipo.HipoDataEvent;
import org.jlab.jnp.hipo.data.HipoEvent;
import org.jlab.jnp.hipo.schema.SchemaFactory;
import org.json.JSONObject;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Set;
@Deprecated
// Experimental. An actual engine is going to be in C++
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
    private int udpPort = 0x4c42;
    private int udpSourcePort = 5678;

    private InetAddress address;
    private DatagramPacket packet;
    private DatagramSocket socket;

    private static final byte version = 0x1;
    private static final byte protocol = 0x1;

    volatile SchemaFactory engineDictionary;

    private short id = 1;
    private int offset = 0;

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
                udpSourcePort = data.getInt(UDP_CLIENT_PORT);
            }
        }

        try {
            address = InetAddress.getByName(udpHost);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        try {
            socket = new DatagramSocket(udpSourcePort);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        engineDictionary = new SchemaFactory();
        engineDictionary.initFromDirectory("CLAS12DIR", "etc/bankdefs/hipo");
        return null;
    }

    @Override
    public EngineData execute(EngineData input) {
        long evtNumber;
        HipoEvent event = (HipoEvent) input.getData();
        event.setSchemaFactory(engineDictionary, false);
        HipoDataEvent dataEventHipo = new HipoDataEvent(event);
        if (dataEventHipo.hasBank("RUN::config")) {
            DataBank bank = dataEventHipo.getBank("RUN::config");
            evtNumber = bank.getLong("event", 0);

            // actual data object
            ByteBuffer bb = ByteBuffer.wrap(event.getDataBuffer());
            bb.flip();

            // load balancer meta data
            ByteBuffer lbMeta = ByteBuffer.allocate(12);
            lbMeta.put((byte) 'L');
            lbMeta.put((byte) 'B');
            lbMeta.put(version);
            lbMeta.put(protocol);
            lbMeta.putLong(evtNumber);
            lbMeta.flip();

            // reassembly meta data
            ByteBuffer reMeta = ByteBuffer.allocate(12);
            // first 0x1002, last 0x1001, otherwise 0x1000
            short s = 0x1000;
            reMeta.putShort(s);
            reMeta.putShort(id);
            reMeta.putInt(offset);
            reMeta.flip();

            // put together byteBuffers
            ByteBuffer payload = ByteBuffer.allocate(lbMeta.limit() + reMeta.limit() + bb.limit())
                    .put(lbMeta)
                    .put(reMeta)
                    .put(bb)
                    .rewind();

            byte[] baPayload = payload.array();

            packet = new DatagramPacket(baPayload, baPayload.length, address, udpPort);

            try {
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
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
