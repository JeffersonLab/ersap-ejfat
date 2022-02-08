package org.jlab.epsci.ersap.ejfat.io.alone;

import java.io.*;
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
 * @author gurjyan on 2/5/22
 * @project ersap-ejfat
 */
public class EjfatReasReader {
    public static void main(String[] args) {
        DataInputStream dataInputStream;
        ByteBuffer dataBuffer;

        String fileName = args[0];
        try {
            // get dataInputStream from a file
            FileInputStream inputStream
                    = new FileInputStream(
                    fileName);

            dataInputStream = new DataInputStream(inputStream);
            // Count the total bytes
            // form the input stream
            int count = inputStream.available();

            // Create byte array
            byte[] b = new byte[count];

            // Read data into byte array
            int bytes = dataInputStream.read(b);

            // Print number of bytes
            // actually read
            System.out.println(bytes);

            dataBuffer = ByteBuffer.wrap(b);
            dataBuffer.rewind();

            int evtNumber = dataBuffer.getInt();
            int evtLength = dataBuffer.getInt();

            dataBuffer.order(ByteOrder.LITTLE_ENDIAN);
            int hipoPointer = dataBuffer.getInt();
            int hipoSize = dataBuffer.getInt();
            System.out.println("DDD:Writer evtNumber = "+ evtNumber + " length = "+evtLength);
            System.out.println("DDD:Writer hipoPoint = "
                    + String.format("%x", hipoPointer)
                    + " HipoSize = " + hipoSize);

        } catch (
                IOException e) {
            e.printStackTrace();
        }

    }
}
