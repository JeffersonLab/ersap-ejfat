package org.jlab.epsci.ersap.ejfat.io.be;


import com.lmax.disruptor.RingBuffer;
import org.jlab.coda.et.*;
import org.jlab.coda.et.exception.*;

import java.io.IOException;
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
 * @author gurjyan on 3/9/22
 * @project ersap-ejfat
 */
public class ReAssembleReceiverET extends Thread {
    EtSystemOpenConfig config;
    EtSystem sys;
    EtFifo fifo;
    EtFifoEntry entry;
    int entryCap;

    // Array of events
    EtEvent[] mevs;
    int idCount, bufId, len;

    private RingBuffer<RingEjfatEvent> ringBuffer;
    private long sequenceNumber;

    private AtomicBoolean running = new AtomicBoolean(true);


    public ReAssembleReceiverET(String etName, String hostName, int tPort, boolean verbose,
                                RingBuffer<RingEjfatEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
        try {
            // Use config object to specify how to open ET system
        config = new EtSystemOpenConfig(etName, hostName, tPort);
        // Create ET system object with verbose debugging output
        sys = new EtSystem(config);
        if (verbose) {
            sys.setDebug(EtConstants.debugInfo);
        }
        sys.open();

            // Use FIFO interface
            // (takes care of attaching to proper station, etc.)
            fifo = new EtFifo(sys);
            entry = new EtFifoEntry(sys, fifo);
        } catch (EtException
                | EtTooManyException
                | IOException
                | EtClosedException
                | EtDeadException e) {
            e.printStackTrace();
        }

        // Max number of events per fifo entry
        entryCap = fifo.getEntryCapacity();
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
        while (running.get()) {
            //----------------------------
            // Get events from ET system
            //----------------------------
            try {
                fifo.getEntry(entry);
            mevs = entry.getBuffers();

            idCount = 0;

            // Go through each event and do something with it
            for (int i=0; i < entryCap; i++) {
                // Does this buffer have any data? (Set by producer). If not ...
                if (!mevs[i].hasFifoData()) {
                    // Once we hit a buffer with no data, there is no further data
                    break;
                }
                // Get an empty item from ring
                RingEjfatEvent event = get();

                idCount++;

                // Source Id associated with this buffer in this fifo entry
                bufId = mevs[i].getFifoId();

                // Get event's data buffer
                ByteBuffer buf = mevs[i].getDataBuffer();

                // Or get the array backing the ByteBuffer
                byte[] data = mevs[i].getData();

                // Length of valid data
                len = mevs[i].getLength();
                event.setPayload(data);
                publish();
            }

            //----------------------------
            // Put events back into ET system
            //----------------------------
                fifo.putEntry(entry);
            } catch (EtException
                    | EtDeadException
                    | EtClosedException
                    | IOException
                    | EtWakeUpException
                    | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void exit() {
        running.set(false);
        fifo.close();
        sys.close();
    }


}
