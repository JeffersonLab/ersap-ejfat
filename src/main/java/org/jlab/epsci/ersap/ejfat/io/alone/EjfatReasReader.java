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
            dataBuffer.order(ByteOrder.LITTLE_ENDIAN);
            dataBuffer.rewind();

            // read eventNumber
//            int evtNumber = Integer.reverseBytes(dataBuffer.getInt());
            int evtNumber = dataBuffer.getInt();
            // read event Length
//            int eventLength = Integer.reverseBytes(dataBuffer.getInt());
            int eventLength = dataBuffer.getInt();
            System.out.println("EventNumber = " + evtNumber);
            System.out.println("EventLength = " + eventLength);

            // get HIPO event
            byte[] payloadData = new byte[bytes-8];
            dataBuffer.get(payloadData);

            File outputFile = new File("v.hipo");
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            outputStream.write(payloadData);
            outputStream.close();

        } catch (
                IOException e) {
            e.printStackTrace();
        }

    }
}
