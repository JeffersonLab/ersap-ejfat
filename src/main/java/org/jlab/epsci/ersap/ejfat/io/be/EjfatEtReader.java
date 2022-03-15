package org.jlab.epsci.ersap.ejfat.io.be;

import com.lmax.disruptor.*;

import java.nio.ByteBuffer;

import static com.lmax.disruptor.RingBuffer.createSingleProducer;

/**
 * Copyright (c) 2021, Jefferson Science Associates, all rights reserved.
 * See LICENSE.txt file.
 * Thomas Jefferson National Accelerator Facility
 * Experimental Physics Software and Computing Infrastructure Group
 * 12000, Jefferson Ave, Newport News, VA 23606
 * Phone : (757)-269-7100
 *
 * @author gurjyan on 3/15/22
 * @project ersap-ejfat
 */
public class EjfatEtReader {
    private final static int maxRingItems = 4;

    private RingBuffer<RingEjfatEvent> ringBuffer;
    private Sequence sequence;
    private SequenceBarrier sequenceBarrier;

    private ReAssembleReceiverET receiver;
    private ReAssembleConsumer consumer;

    public EjfatEtReader(String etName, String hostName, int tPort, boolean verbose) {
        ringBuffer = createSingleProducer(new RingEjfatEventFactory(), maxRingItems,
                new YieldingWaitStrategy());
        sequence = new Sequence(Sequencer.INITIAL_CURSOR_VALUE);
        sequenceBarrier = ringBuffer.newBarrier();
        ringBuffer.addGatingSequences(sequence);

        receiver = new ReAssembleReceiverET(etName, hostName, tPort, verbose, ringBuffer);
        consumer = new ReAssembleConsumer(ringBuffer, sequence, sequenceBarrier);
    }

    public void go() {
        receiver.start();
    }

    public ByteBuffer getEjfatEvent() throws Exception {
        return consumer.getEvent();
    }

    public void close() {
        receiver.exit();
        consumer.exit();
    }
}
