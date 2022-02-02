package org.jlab.epsci.ersap.ejfat.data;

import org.jlab.epsci.ersap.base.error.ErsapException;
import org.jlab.epsci.ersap.engine.EngineDataType;
import org.jlab.epsci.ersap.engine.ErsapSerializer;

import java.nio.ByteBuffer;

/**
 * Copyright (c) 2021, Jefferson Science Associates, all rights reserved.
 * See LICENSE.txt file.
 * Thomas Jefferson National Accelerator Facility
 * Experimental Physics Software and Computing Infrastructure Group
 * 12000, Jefferson Ave, Newport News, VA 23606
 * Phone : (757)-269-7100
 *
 * @author gurjyan on 2/2/22
 * @project ersap-ejfat
 */

public class ClasEjfatDataType extends EngineDataType {
    private static final String MIME_TYPE = "binary/clas-ejfat";
    public static ClasEjfatDataType INSTANCE = new ClasEjfatDataType();

    private ClasEjfatDataType() {
        super(MIME_TYPE, new ErsapSerializer() {
            @Override
            public ByteBuffer write(Object data) throws ErsapException {
                return (ByteBuffer)data;
            }

            @Override
            public Object read(ByteBuffer buffer) throws ErsapException {
                return buffer;
            }
        });
    }
}
