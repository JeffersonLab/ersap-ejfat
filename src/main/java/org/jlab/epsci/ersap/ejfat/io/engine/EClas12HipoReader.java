/**
 * Copyright (c) 2021, Jefferson Science Associates, all rights reserved.
 * See LICENSE.txt file.
 * Thomas Jefferson National Accelerator Facility
 * Experimental Physics Software and Computing Infrastructure Group
 * 12000, Jefferson Ave, Newport News, VA 23606
 * Phone : (757)-269-7100
 *
 * @author gurjyan on 1/13/22
 * @project ersap-ejfat
 */
package org.jlab.epsci.ersap.ejfat.io.engine;

import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;
import org.jlab.epsci.ersap.engine.EngineDataType;
import org.jlab.epsci.ersap.std.services.AbstractEventReaderService;
import org.jlab.epsci.ersap.std.services.EventReaderException;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Path;
import java.util.*;

/**
 * Reads CLAS12 decoded HIPO files and streams build
 * events to ERSAP microservices down the data pipeline.
 */
public class EClas12HipoReader extends AbstractEventReaderService<HipoReader> {

    private Timer timer;
private int evt = 1;
private Object payload;

private Object createSingleEvent(HipoReader reader, int eventNumber) {
        Event event = new Event();
        reader.nextEvent(event);
        int evtLength = event.getEventBufferSize();

        ByteBuffer eventBuffer = event.getEventBuffer();
        eventBuffer.rewind();

        byte[] evt = new byte[evtLength];
        eventBuffer.get(evt);

        ByteBuffer outBuffer = ByteBuffer.wrap(evt);
        outBuffer.order(ByteOrder.LITTLE_ENDIAN);
        outBuffer.rewind();

        // Debug printout to check the consistency of the h5. hipoPointer = 61345645 (EV4a)
//            int hipoPointer = outBuffer.getInt();
//            int hipoSize = outBuffer.getInt();
//            System.out.println("DDD:Reader hipoPoint = "
//                    + String.format("%x", hipoPointer)
//                    + " HipoSize = " + hipoSize);

        ByteBuffer payload = ByteBuffer.allocate(evtLength + 8)
                .putInt(eventNumber) //tick
                .putInt(evtLength) // length
                .put(outBuffer);
        return payload;
}

    @Override
    protected HipoReader createReader(Path file, JSONObject opts)
            throws EventReaderException {
        try {
            HipoReader reader = new HipoReader();
            reader.open(file.toString());

            // create event number 13 data
            payload = createSingleEvent(reader,13);

            timer = new Timer();
            timer.schedule(new PrintRates(), 0, 1000);

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
    public int readEventCount() throws EventReaderException {
        return reader.getEventCount();
    }

    @Override
    public ByteOrder readByteOrder() throws EventReaderException {
        return ByteOrder.BIG_ENDIAN;

    }

    /**
     * This will read the Hipo event.
     * Prepend 3 word header:
     * 32bit int: event number
     * 32bit int: Hipo event size
     */
    @Override
    public Object readEvent(int eventNumber) throws EventReaderException {
        evt++;
        return payload;

//        try {
//            evt++;
////            evt = eventNumber;
//            Event event = new Event();
//            reader.nextEvent(event);
//            int evtLength = event.getEventBufferSize();
//
//            ByteBuffer eventBuffer = event.getEventBuffer();
//            eventBuffer.rewind();
//
//            byte[] evt = new byte[evtLength];
//            eventBuffer.get(evt);
//
//            ByteBuffer outBuffer = ByteBuffer.wrap(evt);
//            outBuffer.order(ByteOrder.LITTLE_ENDIAN);
//            outBuffer.rewind();
//
//            // Debug printout to check the consistency of the h5. hipoPointer = 61345645 (EV4a)
////            int hipoPointer = outBuffer.getInt();
////            int hipoSize = outBuffer.getInt();
////            System.out.println("DDD:Reader hipoPoint = "
////                    + String.format("%x", hipoPointer)
////                    + " HipoSize = " + hipoSize);
//
//            ByteBuffer payload = ByteBuffer.allocate(evtLength + 8)
//                    .putInt(eventNumber) //tick
//                    .putInt(evtLength) // length
//                    .put(outBuffer);
//            return payload;
////            return event.getEventBuffer();
//        } catch (Exception e) {
//            throw new EventReaderException(e);
//        }
    }

    @Override
    protected EngineDataType getDataType() {
//        return Clas12Types.HIPO;
        return EngineDataType.BYTES;
    }

    private class PrintRates extends TimerTask {
        ArrayList<Integer> al = new ArrayList<>();
        @Override
        public void run() {
            if (evt == 0) {
                int sum = 0;
                for (int i = 0; i<al.size(); i++ ){
                    sum += al.get(i);
                }
                System.out.println("average rate = " + sum/al.size() +" Hz");
                System.out.println("min = " + Collections.min(al) +" max = " + Collections.max(al));
                this.cancel();
            }
//            System.out.println("evtRate = "+ evt + "Hz");
            al.add(evt);
            evt = 0;

        }
    }
}
