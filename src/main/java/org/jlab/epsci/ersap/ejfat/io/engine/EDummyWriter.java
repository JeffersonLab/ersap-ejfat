package org.jlab.epsci.ersap.ejfat.io.engine;

import org.jlab.epsci.ersap.ejfat.data.Clas12Types;
import org.jlab.epsci.ersap.engine.EngineDataType;
import org.jlab.epsci.ersap.std.services.AbstractEventWriterService;
import org.jlab.epsci.ersap.std.services.EventWriterException;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Copyright (c) 2021, Jefferson Science Associates, all rights reserved.
 * See LICENSE.txt file.
 * Thomas Jefferson National Accelerator Facility
 * Experimental Physics Software and Computing Infrastructure Group
 * 12000, Jefferson Ave, Newport News, VA 23606
 * Phone : (757)-269-7100
 *
 * @author gurjyan on 1/26/22
 * @project ersap-ejfat
 */
public class EDummyWriter extends AbstractEventWriterService<FileWriter> {
    @Override
    protected FileWriter createWriter(Path file, JSONObject opts)
            throws EventWriterException {
        try {
            return new FileWriter(file.toString());
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

    }

    @Override
    protected EngineDataType getDataType() {

        return Clas12Types.HIPO;
//        return EngineDataType.BYTES;
    }
}
