package org.jlab.epsci.ersap.ejfat.proc;

import org.jlab.epsci.ersap.base.ErsapUtil;
import org.jlab.epsci.ersap.engine.Engine;
import org.jlab.epsci.ersap.engine.EngineData;
import org.jlab.epsci.ersap.engine.EngineDataType;

import java.nio.ByteBuffer;
import java.util.Set;

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
public class EDummyEngine implements Engine {
    int i;
    @Override
    public EngineData configure(EngineData input) {
        return null;
    }

    @Override
    public EngineData execute(EngineData input) {
       if ((i++ % 100) == 0) {
           byte[] ba =  (byte[])input.getData();
           ByteBuffer bb = ByteBuffer.wrap(ba);
           System.out.println(bb.getInt());
       }
        return input;
    }

    @Override
    public EngineData executeGroup(Set<EngineData> inputs) {
        return null;
    }

    @Override
    public Set<EngineDataType> getInputDataTypes() {
        return ErsapUtil.buildDataTypes(EngineDataType.BYTES,
                EngineDataType.JSON);
    }

    @Override
    public Set<EngineDataType> getOutputDataTypes() {
        return ErsapUtil.buildDataTypes(EngineDataType.BYTES);
    }

    @Override
    public Set<String> getStates() {
        return null;
    }

    @Override
    public String getDescription() {
        return "Dummy engine.";
    }

    @Override
    public String getVersion() {
        return "0.1";
    }

    @Override
    public String getAuthor() {
        return "vg";
    }

    @Override
    public void reset() {

    }

    @Override
    public void destroy() {

    }
}
