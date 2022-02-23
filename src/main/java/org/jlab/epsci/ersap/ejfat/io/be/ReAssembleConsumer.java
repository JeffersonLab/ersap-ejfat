package org.jlab.epsci.ersap.ejfat.io.be;

import com.lmax.disruptor.*;

import java.nio.ByteBuffer;

/**
 * Copyright (c) 2021, Jefferson Science Associates, all rights reserved.
 * See LICENSE.txt file.
 * Thomas Jefferson National Accelerator Facility
 * Experimental Physics Software and Computing Infrastructure Group
 * 12000, Jefferson Ave, Newport News, VA 23606
 * Phone : (757)-269-7100
 *
 * @author gurjyan on 2/22/22
 * @project ersap-ejfat
 */
public class ReAssembleConsumer {
    private RingBuffer<RingEjfatEvent> ringBuffer;
    private Sequence sequence;
    private SequenceBarrier barrier;
    private long nextSequence;
    private long availableSequence;

    public ReAssembleConsumer(RingBuffer<RingEjfatEvent> ringBuffer,
                              Sequence sequence,
                              SequenceBarrier barrier) {
        this.ringBuffer = ringBuffer;
        this.sequence = sequence;
        this.barrier = barrier;

        nextSequence = sequence.get() + 1L;
        availableSequence = -1L;
    }

    public RingEjfatEvent get() throws InterruptedException {
        RingEjfatEvent event = null;
        try {
            if (availableSequence < nextSequence) {
                availableSequence = barrier.waitFor(nextSequence);
            }

            event = ringBuffer.get(nextSequence);
        } catch (final TimeoutException | AlertException ex) {
            ex.printStackTrace();
        }
        return event;
    }

    public void put() throws InterruptedException {

        // Tell input ring that we're done with the event we're consuming
        sequence.set(nextSequence);

        // Go to next item to consume on input ring
        nextSequence++;
    }

    public ByteBuffer getEvent() throws Exception {
        RingEjfatEvent event = get();
        ByteBuffer b = event.getPayloadBuffer();
        put();
        return b;
    }

    public void exit() {
    }
}
