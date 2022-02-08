package org.jlab.epsci.ersap.ejfat.io.engine;

import org.jlab.epsci.ersap.engine.EngineDataType;
import org.jlab.epsci.ersap.std.services.AbstractEventWriterService;
import org.jlab.epsci.ersap.std.services.EventWriterException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Copyright (c) 2021, Jefferson Science Associates, all rights reserved.
 * See LICENSE.txt file.
 * Thomas Jefferson National Accelerator Facility
 * Experimental Physics Software and Computing Infrastructure Group
 * 12000, Jefferson Ave, Newport News, VA 23606
 * Phone : (757)-269-7100
 *
 * @author gurjyan on 2/3/22
 * @project ersap-ejfat
 */
public class EEventWriter extends AbstractEventWriterService<FileOutputStream> {
    private static final String FILE_EVENTS = "file-events";
    private int evtCount;
    private int fileCount = 1;
    private int numFileEvents;
    private Path file;

    @Override
    protected FileOutputStream createWriter(Path file, JSONObject opts)
            throws EventWriterException {
        numFileEvents = opts.has(FILE_EVENTS) ? opts.getInt(FILE_EVENTS) : 100000;
        this.file = file;
        try {
            return new FileOutputStream(file.toString());
        } catch (IOException e) {
            throw new EventWriterException(e);
        }
    }

    @Override
    protected void closeWriter() {
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void writeEvent(Object event) throws EventWriterException {
        evtCount++;
        try {
            ByteBuffer bb = (ByteBuffer)event;
            bb.rewind();
//            int evtNumber = bb.getInt();
//            int evtLength = bb.getInt();
//            int hipoPointer = bb.getInt();
//            int hipoSize = bb.getInt();
//            System.out.println("DDD:JavaProc evtNumber = "+ evtNumber + " length = "+evtLength);
//            System.out.println("DDD:JavaProc hipoPoint = "+ hipoPointer + " HipoSize = "+hipoSize);

            writer.write(bb.array());

            if (evtCount >= numFileEvents) {
                System.out.println("DDD:Writer evtCount = " + evtCount +" "+ numFileEvents);
                evtCount = 0;
                writer.close();
                writer = new FileOutputStream(file.toString() + "_" + (fileCount++) +".ers");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected EngineDataType getDataType() {
        return EngineDataType.BYTES;
    }
}

