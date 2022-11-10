package org.jlab.epsci.ersap.ejfat.data;

/**
 * Copyright (c) 2021, Jefferson Science Associates, all rights reserved.
 * See LICENSE.txt file.
 * Thomas Jefferson National Accelerator Facility
 * Experimental Physics Software and Computing Infrastructure Group
 * 12000, Jefferson Ave, Newport News, VA 23606
 * Phone : (757)-269-7100
 *
 * @author gurjyan on 11/10/22
 * @project ersap-ejfat
 */
import org.jlab.epsci.ersap.base.error.ErsapException;
import org.jlab.epsci.ersap.engine.EngineDataType;
import org.jlab.epsci.ersap.engine.ErsapSerializer;
import org.jlab.jnp.hipo.data.HipoEvent;
import java.nio.ByteBuffer;

public final class Clas12Types {

    private Clas12Types() { }

    private static class HipoSerializer implements ErsapSerializer {

        @Override
        public ByteBuffer write(Object data) throws ErsapException {
            HipoEvent event = (HipoEvent) data;
            return ByteBuffer.wrap(event.getDataBuffer());
        }

        @Override
        public Object read(ByteBuffer buffer) throws ErsapException {
            return new HipoEvent(buffer.array());
        }
    }

    public static final EngineDataType EVIO =
            new EngineDataType("binary/data-evio", EngineDataType.BYTES.serializer());

    public static final EngineDataType HIPO =
            new EngineDataType("binary/data-hipo", new HipoSerializer());
}
