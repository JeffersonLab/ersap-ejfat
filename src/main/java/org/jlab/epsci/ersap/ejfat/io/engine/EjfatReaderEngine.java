package org.jlab.epsci.ersap.ejfat.io.engine;

import org.jlab.epsci.ersap.ejfat.data.Clas12Types;
import org.jlab.epsci.ersap.ejfat.io.be.EjfatReader;
import org.jlab.epsci.ersap.engine.EngineDataType;
import org.jlab.epsci.ersap.std.services.AbstractEventReaderService;
import org.jlab.epsci.ersap.std.services.EventReaderException;
import org.json.JSONObject;

import java.nio.ByteOrder;
import java.nio.file.Path;

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
public class EjfatReaderEngine extends AbstractEventReaderService<EjfatReader> {
    private static final String PORT = "port";


    @Override
    protected EjfatReader createReader(Path file, JSONObject opts) throws EventReaderException {
        int port = opts.has(PORT) ? opts.getInt(PORT) : 6000;
        try {
            EjfatReader reader = new EjfatReader(port);
            reader.go();
            return reader;
        } catch (Exception e) {
            throw new EventReaderException(e);
        }
    }

    @Override
    protected void closeReader() {
       reader.close();
    }

    @Override
    protected int readEventCount() throws EventReaderException {
        return Integer.MAX_VALUE;
    }

    @Override
    protected ByteOrder readByteOrder() throws EventReaderException {
        return ByteOrder.LITTLE_ENDIAN;
    }

    @Override
    protected Object readEvent(int eventNumber) throws EventReaderException {
        try {
            return reader.getEjfatEvent();
        } catch (Exception e) {
            throw new EventReaderException(e);
        }
    }

    @Override
    protected EngineDataType getDataType() {
        return Clas12Types.HIPO;
//        return EngineDataType.BYTES;
    }
}
