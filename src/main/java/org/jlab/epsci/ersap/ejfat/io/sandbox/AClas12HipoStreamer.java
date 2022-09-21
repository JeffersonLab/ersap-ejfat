//package org.jlab.epsci.ersap.ejfat.io.fe;
//
///**
// * Copyright (c) 2021, Jefferson Science Associates, all rights reserved.
// * See LICENSE.txt file.
// * Thomas Jefferson National Accelerator Facility
// * Experimental Physics Software and Computing Infrastructure Group
// * 12000, Jefferson Ave, Newport News, VA 23606
// * Phone : (757)-269-7100
// *
// * @author gurjyan on 1/13/22
// * @project ersap-ejfat
// */
//import java.nio.ByteBuffer;
//import java.util.List;
//import java.util.Timer;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import org.jlab.coda.hipo.HipoException;
//import org.jlab.coda.hipo.Reader;
//import org.jlab.coda.hipo.RecordInputStream;
//import org.jlab.coda.hipo.Reader.RecordPosition;
//import org.jlab.jnp.hipo.data.HipoEvent;
//import org.jlab.jnp.hipo.data.HipoNode;
//import org.jlab.jnp.hipo.io.DataEventHipo;
//import org.jlab.jnp.hipo.io.HipoWriter;
//import org.jlab.jnp.hipo.schema.Schema;
//import org.jlab.jnp.hipo.schema.SchemaFactory;
//
//public class AClas12HipoStreamer {
//    private Reader reader = null;
//    private boolean forceScan = true;
//    private final SchemaFactory schemaFactory = new SchemaFactory();
//
//    public AClas12HipoStreamer() {
//    }
//
//    public AClas12HipoStreamer(boolean fs) {
//        this.forceScan = fs;
//    }
//
//    public RecordInputStream getUserHeaderRecord() {
//        ByteBuffer userHeader = this.reader.readUserHeader();
//        RecordInputStream userRecord = new RecordInputStream();
//
//        try {
//            userRecord.readRecord(userHeader, 0);
//            return userRecord;
//        } catch (HipoException var4) {
//            Logger.getLogger(org.jlab.jnp.hipo.io.HipoReader.class.getName()).log(Level.SEVERE, (String)null, var4);
//            return null;
//        }
//    }
//
//    public void open(String filename) {
//        long start_time = System.currentTimeMillis();
//        this.reader = new Reader(filename, this.forceScan);
//        long end_time = System.currentTimeMillis();
//        ByteBuffer userHeader = this.reader.readUserHeader();
//        if (userHeader.capacity() > 56) {
//            RecordInputStream userRecord = new RecordInputStream();
//
//            try {
//                userRecord.readRecord(userHeader, 0);
//
//                for(int i = 0; i < userRecord.getEntries(); ++i) {
//                    byte[] eventBytes = userRecord.getEvent(i);
//                    HipoEvent event = new HipoEvent(eventBytes);
//                    if (event.hasNode(31111, 1)) {
//                        HipoNode node = event.getNode(31111, 1);
//                        Schema schema = new Schema();
//                        schema.setFromText(node.getString());
//
//                        try {
//                            this.schemaFactory.addSchema(schema);
//                        } catch (Exception var14) {
//                            Logger.getLogger(org.jlab.jnp.hipo.io.HipoReader.class.getName()).log(Level.SEVERE, (String)null, var14);
//                        }
//                    }
//                }
//            } catch (HipoException var15) {
//                Logger.getLogger(org.jlab.jnp.hipo.io.HipoReader.class.getName()).log(Level.SEVERE, (String)null, var15);
//            }
//        }
//
//        long openTime = end_time - start_time;
//        String outMessage = String.format("** reader-open ** records = %d, events = %d, schemas = %d, time = %d ms", this.reader.getRecordCount(), this.reader.getEventCount(), this.schemaFactory.getSchemaList().size(), openTime);
//        System.out.println(outMessage);
//    }
//
//    public HipoWriter createWriter() {
//        HipoWriter writer = new HipoWriter();
//        writer.appendSchemaFactory(this.schemaFactory);
//        return writer;
//    }
//
//    public int getRecordCount() {
//        return this.reader.getRecordCount();
//    }
//
//    public boolean readRecord(int index) {
//        try {
//            return this.reader.readRecord(index);
//        } catch (HipoException var3) {
//            Logger.getLogger(org.jlab.jnp.hipo.io.HipoReader.class.getName()).log(Level.SEVERE, (String)null, var3);
//            return false;
//        }
//    }
//
//    public SchemaFactory getSchemaFactory() {
//        return this.schemaFactory;
//    }
//
//    public int getEventCount() {
//        return this.reader == null ? 0 : this.reader.getEventCount();
//    }
//
//    public boolean hasNext() {
//        return this.reader == null ? false : this.reader.hasNext();
//    }
//
//    public HipoEvent readPreviousEvent() {
//        try {
//            byte[] event = this.reader.getPrevEvent();
//            return new HipoEvent(event, this.schemaFactory);
//        } catch (HipoException var3) {
//            Logger.getLogger(org.jlab.jnp.hipo.io.HipoReader.class.getName()).log(Level.SEVERE, (String)null, var3);
//            return null;
//        }
//    }
//
//    public HipoEvent readNextEvent() {
//        try {
//            byte[] event = this.reader.getNextEvent();
//            return new HipoEvent(event, this.schemaFactory);
//        } catch (HipoException var3) {
//            Logger.getLogger(org.jlab.jnp.hipo.io.HipoReader.class.getName()).log(Level.SEVERE, (String)null, var3);
//            return null;
//        }
//    }
//
//    public int getRecordEventCount() {
//        return this.reader.getCurrentRecordStream().getEntries();
//    }
//
//    public HipoEvent readEvent(int index) {
//        try {
//            byte[] event = this.reader.getEvent(index);
//            return new HipoEvent(event, this.schemaFactory);
//        } catch (HipoException var4) {
//            Logger.getLogger(org.jlab.jnp.hipo.io.HipoReader.class.getName()).log(Level.SEVERE, (String)null, var4);
//            return null;
//        }
//    }
//
//    public HipoEvent readRecordEvent(int index) {
//        byte[] event = this.reader.getCurrentRecordStream().getEvent(index);
//        return new HipoEvent(event, this.schemaFactory);
//    }
//
//    public void readRecordEvent(DataEventHipo event, int index) {
//        try {
//            int dataSize = this.reader.getCurrentRecordStream().getEventLength(index);
//            if (dataSize < 0) {
//                System.out.println(" ** error ** failed to read event # " + index);
//                return;
//            }
//
//            event.resize(dataSize);
//            this.reader.getCurrentRecordStream().getEvent(event.eventBuffer, 0, index);
//            ByteBuffer var10000 = event.eventBuffer;
//            event.getClass();
//            var10000.putInt(4, dataSize);
//            event.setSchemaFactory(this.schemaFactory);
//            event.updateIndex();
//        } catch (HipoException var4) {
//            Logger.getLogger(org.jlab.jnp.hipo.io.HipoReader.class.getName()).log(Level.SEVERE, (String)null, var4);
//        }
//
//    }
//
//    public void close() {
//        this.reader = null;
//    }
//
//    public List<RecordPosition> getRecordPositions() {
//        return this.reader.getRecordPositions();
//    }
//
//    public static void main(String[] args) {
//        String file = args[0];
//        org.jlab.jnp.hipo.io.HipoReader reader = new org.jlab.jnp.hipo.io.HipoReader();
//        reader.open(file);
//        int nevents = reader.getEventCount();
//        System.out.println(" N# = " + nevents);
//        int icounter = 0;
//        DataEventHipo dataEvent = new DataEventHipo();
//
//        for(int i = 0; i < nevents; ++i) {
//            HipoEvent event = reader.readNextEvent();
//            ByteBuffer buff = event.getDataByteBuffer();
//        }
//
//    }
//}
