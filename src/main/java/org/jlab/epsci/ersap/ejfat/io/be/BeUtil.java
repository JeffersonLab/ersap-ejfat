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
 * @author gurjyan on 2/25/22
 * @project ersap-ejfat
 */
public class BeUtil {
    public static void dump (int evtNumber, ByteBuffer bb){
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.rewind();
        int hipoPointer = bb.getInt();
        int hipoSize = bb.getInt();
        System.out.println("DDD: EventNumber = " + evtNumber
                + " HIPO_Magic = " + String.format("%x", hipoPointer)
                + " HIPO_Size = " + hipoSize);
    }
}
