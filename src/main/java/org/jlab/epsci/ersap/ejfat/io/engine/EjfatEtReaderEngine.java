package org.jlab.epsci.ersap.ejfat.io.engine;

import org.jlab.coda.et.EtConstants;
import org.jlab.epsci.ersap.ejfat.io.be.EjfatEtReader;
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
 * @author gurjyan on 3/15/22
 * @project ersap-ejfat
 */
public class EjfatEtReaderEngine extends AbstractEventReaderService<EjfatEtReader> {
    private static final String ETNAME = "et_name";
    private static final String HOSTNAME = "et_host";
    private static final String TPORT = "et_tcp_port";
    private static final String VERBOSE = "verbose";

    @Override
    protected EjfatEtReader createReader(Path file, JSONObject opts) throws EventReaderException {
        String etName = opts.has(ETNAME) ? opts.getString(ETNAME) : "/tmp/et_sys";
        String hostName = opts.has(HOSTNAME) ? opts.getString(HOSTNAME) : EtConstants.hostLocal;
        int tport = opts.has(TPORT) ? opts.getInt(TPORT) : EtConstants.serverPort;
        boolean verbose = opts.has(VERBOSE) && opts.getBoolean(VERBOSE);
        try {
            EjfatEtReader reader = new EjfatEtReader(etName, hostName, tport, verbose);
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
        return EngineDataType.BYTES;
    }
}
