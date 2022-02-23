package org.jlab.epsci.ersap.ejfat.io.be;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

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
public class RingEjfatEvent {
    private byte[] payload;
    private ByteBuffer payloadBuffer;
    private int payloadDataLength;

    public RingEjfatEvent() {
        payload = new byte[100000];
        payloadBuffer = ByteBuffer.wrap(payload);
        payloadBuffer.order(ByteOrder.LITTLE_ENDIAN);
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    public ByteBuffer getPayloadBuffer() {
        return cloneByteBuffer(payloadBuffer);
    }


    public int getPayloadDataLength() {
        return payloadDataLength;
    }

    public void setPayloadDataLength(int payloadDataLength) {
        this.payloadDataLength = payloadDataLength;
    }

    private ByteBuffer cloneByteBuffer(final ByteBuffer original) {

        // Create clone with same capacity as original.
        final ByteBuffer clone = (original.isDirect()) ?
                ByteBuffer.allocateDirect(original.capacity()) :
                ByteBuffer.allocate(original.capacity());

        original.rewind();
        clone.put(original);
        clone.flip();
        clone.order(original.order());
        return clone;
    }
}
