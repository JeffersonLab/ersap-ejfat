package org.jlab.epsci.ersap.ejfat.io.fe;

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
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jlab.coda.hipo.HeaderType;
import org.jlab.coda.hipo.RecordOutputStream;
import org.jlab.coda.hipo.Writer;
import org.jlab.jnp.hipo.data.HipoEvent;
import org.jlab.jnp.hipo.data.HipoNode;
import org.jlab.jnp.hipo.schema.Schema;
import org.jlab.jnp.hipo.schema.SchemaFactory;
import org.jlab.jnp.hipo.utils.HipoLogo;

public class AClas12HipoWriter {
    public static final int SCHEMA_GROUP = 31111;
    public static final int SCHEMA_ITEM = 1;
    protected boolean OVERWRITE_FILE = true;
    private Writer writer = null;
    private final SchemaFactory schemaFactory = new SchemaFactory();
    private boolean schemaFilter = false;

    public AClas12HipoWriter(String options) {
        this.writer = new Writer(HeaderType.HIPO_FILE, ByteOrder.LITTLE_ENDIAN, 1000000, 8388608);
        this.writer.setCompressionType(2);
        this.writer.addTrailer(true);
        if (options.contains("CREATE")) {
            this.OVERWRITE_FILE = false;
        }

    }

    public AClas12HipoWriter() {
        this.writer = new Writer(HeaderType.HIPO_FILE, ByteOrder.LITTLE_ENDIAN, 1000000, 8388608);
        this.writer.setCompressionType(2);
        this.writer.addTrailer(true);
    }

    public AClas12HipoWriter(int bufferSize) {
        this.writer = new Writer(HeaderType.HIPO_FILE, ByteOrder.LITTLE_ENDIAN, 10000000, bufferSize);
        this.writer.setCompressionType(2);
        this.writer.addTrailer(true);
    }

    public AClas12HipoWriter(int bufferSize, String options) {
        this.writer = new Writer(HeaderType.HIPO_FILE, ByteOrder.LITTLE_ENDIAN, 10000000, bufferSize);
        this.writer.setCompressionType(2);
        if (options.contains("CREATE")) {
            this.OVERWRITE_FILE = false;
        }

        this.writer.addTrailer(true);
    }

    public void defineSchema(Schema schema) {
        try {
            this.schemaFactory.addSchema(schema);
        } catch (Exception var3) {
            Logger.getLogger(org.jlab.jnp.hipo.io.HipoWriter.class.getName()).log(Level.SEVERE, (String)null, var3);
        }

    }

    public void appendSchemaFactory(SchemaFactory factory) {
        Iterator var2 = factory.getSchemaList().iterator();

        while(var2.hasNext()) {
            Schema schema = (Schema)var2.next();

            try {
                this.schemaFactory.addSchema(schema);
            } catch (Exception var5) {
                Logger.getLogger(org.jlab.jnp.hipo.io.HipoWriter.class.getName()).log(Level.SEVERE, (String)null, var5);
            }
        }

    }

    public void defineSchema(String name, int group, String format) {
        try {
            this.schemaFactory.addSchema(new Schema(name, group, format));
        } catch (Exception var5) {
            Logger.getLogger(org.jlab.jnp.hipo.io.HipoWriter.class.getName()).log(Level.SEVERE, (String)null, var5);
        }

    }

    public void setSchemaFilter(boolean flag) {
        this.schemaFilter = flag;
        System.out.println("[HipoWriter] ----> Dictionary Schema Filtering set to : " + this.schemaFilter);
        if (this.schemaFilter) {
            Iterator var2 = this.schemaFactory.getSchemaList().iterator();

            while(var2.hasNext()) {
                Schema schema = (Schema)var2.next();
                System.out.println("[SchemaFactory] ---> adding bank to filter : " + schema.getName());
                this.schemaFactory.addFilter(schema.getName());
            }
        }

    }

    public void appendSchemaFactoryFromDirectory(String env, String relativePath) {
        SchemaFactory scf = new SchemaFactory();
        scf.initFromDirectory(env, relativePath);
        this.appendSchemaFactory(scf);
    }

    public void setCompressionType(int compression) {
        this.writer.setCompressionType(compression);
    }

    public SchemaFactory getSchemaFactory() {
        return this.schemaFactory;
    }

    public HipoEvent createEvent() {
        HipoEvent event = new HipoEvent(this.schemaFactory);
        return event;
    }

    private RecordOutputStream createSchemaRecord() {
        RecordOutputStream rec = new RecordOutputStream();
        Iterator var2 = this.schemaFactory.getSchemaList().iterator();

        while(var2.hasNext()) {
            Schema schema = (Schema)var2.next();
            HipoEvent event = new HipoEvent();
            HipoNode schemaNode = schema.createNode(31111, 1);
            event.addNode(schemaNode);
            rec.addEvent(event.getDataBuffer());
        }

        return rec;
    }

    public final void open(String filename, byte[] userHeader) {
        if (this.outputFileExits(filename)) {
            System.out.println("[HIPO-WRITER] ** error ** the output file already exists : " + filename);
            if (this.OVERWRITE_FILE) {
                System.out.println("[HIPO-WRITER] ** warning ** rewriting the file : " + filename);

                try {
                    Files.delete(Paths.get(filename));
                } catch (IOException var4) {
                    System.out.println("[HIPO-WRITER] ** error ** failed deleting file : " + filename);
                }
            }
        } else {
            this.writer.open(filename, userHeader);
            HipoLogo.showLogo();
        }

    }

    public final void open(String filename) {
        if (this.outputFileExits(filename)) {
            System.out.println("[HIPO-WRITER] ** error ** the output file already exists : " + filename);
            System.out.println("[HIPO-WRITER] ** warning ** attempt to delete the file : " + filename);

            try {
                Files.delete(Paths.get(filename));
                System.out.println("[HIPO-WRITER] ** warning ** delete successful ");
            } catch (IOException var7) {
                System.out.println("[HIPO-WRITER] ** error ** failed deleting file : " + filename);
            }
        }

        if (this.schemaFactory.getSchemaList().isEmpty()) {
            System.out.println("[HIPO-WRITER] ---> Schema factory is empty.");
            this.writer.open(filename);
            HipoLogo.showLogo();
        } else {
            RecordOutputStream recDictionary = this.createSchemaRecord();
            recDictionary.getHeader().setCompressionType(0);
            recDictionary.build();
            ByteBuffer buffer = recDictionary.getBinaryBuffer();
            int size = buffer.limit();
            int sizeWords = buffer.getInt(0);
            byte[] userHeader = new byte[sizeWords * 4];
            System.arraycopy(buffer.array(), 0, userHeader, 0, userHeader.length);
            this.writer.open(filename, userHeader);
            HipoLogo.showLogo();
            System.out.println("[HIPO-WRITER] ---> schema dictionary written with " + this.schemaFactory.getSchemaList().size() + " entries.");
            System.out.println("[HIPO-WRITER] ---> compression type " + this.writer.getCompressionType());
        }

    }

    private boolean outputFileExits(String filename) {
        File f = new File(filename);
        return f.exists();
    }

    public void writeEvent(HipoEvent event) {
        if (this.schemaFilter) {
            HipoEvent filtered = this.schemaFactory.getFilteredEvent(event);
            this.writer.addEvent(filtered.getDataBuffer());
        } else {
            this.writer.addEvent(event.getDataBuffer());
        }

    }

    public void close() {
        this.writer.close();
    }

    public static void main(String[] args) {
        System.setProperty("COATJAVA", "/Users/gavalian/Work/Software/Release-4a.0.0/COATJAVA/coatjava");
        System.setProperty("JNPDIR", "/Users/gavalian/Work/Software/project-4a.0.0/Distribution/jnp");
        org.jlab.jnp.hipo.io.HipoWriter writer = new org.jlab.jnp.hipo.io.HipoWriter();
        writer.setCompressionType(0);
        writer.appendSchemaFactoryFromDirectory("JNPDIR", "jnp-hipo/etc");
        writer.open("dictionary_test.hipo");
        writer.setSchemaFilter(true);

//        writer.writeEvent(event);

        writer.close();
    }
}

