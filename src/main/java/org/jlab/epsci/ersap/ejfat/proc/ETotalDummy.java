package org.jlab.epsci.ersap.ejfat.proc;

import org.jlab.epsci.ersap.base.ErsapUtil;
import org.jlab.epsci.ersap.engine.Engine;
import org.jlab.epsci.ersap.engine.EngineData;
import org.jlab.epsci.ersap.engine.EngineDataType;

import java.util.Set;

/**
 * Copyright (c) 2021, Jefferson Science Associates, all rights reserved.
 * See LICENSE.txt file.
 * Thomas Jefferson National Accelerator Facility
 * Experimental Physics Software and Computing Infrastructure Group
 * 12000, Jefferson Ave, Newport News, VA 23606
 * Phone : (757)-269-7100
 *
 * @author gurjyan on 3/17/22
 * @project ersap-ejfat
 */
public class ETotalDummy implements Engine {
    @Override
    public EngineData configure(EngineData input) {
        return null;
    }

    @Override
    public EngineData execute(EngineData input) {
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
        return ErsapUtil.buildDataTypes(EngineDataType.BYTES);    }

    @Override
    public Set<String> getStates() {
        return null;
    }

    @Override
    public String getDescription() {
        return "Total dummy engine";
    }

    @Override
    public String getVersion() {
        return "v1";
    }

    @Override
    public String getAuthor() {
        return "gurjyan";
    }

    @Override
    public void reset() {

    }

    @Override
    public void destroy() {

    }
}
