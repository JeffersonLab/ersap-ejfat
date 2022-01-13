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

import org.jlab.epsci.ersap.ejfat.io.Clas12Types;
import org.jlab.epsci.ersap.engine.EngineDataType;
import org.jlab.epsci.ersap.std.services.AbstractEventWriterService;
import org.jlab.epsci.ersap.std.services.EventWriterException;
import org.jlab.jnp.hipo.data.HipoEvent;
import org.jlab.jnp.hipo.io.HipoWriter;
import org.jlab.jnp.utils.file.FileUtils;
import org.json.JSONObject;

import java.nio.file.Path;

/**
 * ERSAP service that writes HIPO events to an output file.
 */
public class EClas12HipoWriter extends AbstractEventWriterService<HipoWriter> {
    private static final String CONF_COMPRESSION = "compression";
    private static final String CONF_SCHEMA = "schema_dir";

    @Override
    protected HipoWriter createWriter(Path file, JSONObject opts) throws EventWriterException {
        try {
            HipoWriter writer = new HipoWriter();
            int compression = getCompression(opts);
            System.out.printf("%s service: using compression level %d%n", getName(), compression);
            writer.setCompressionType(compression);
            writer.getSchemaFactory().initFromDirectory(getSchemaDirectory(opts));
            writer.open(file.toString());
            return writer;
        } catch (Exception e) {
            throw new EventWriterException(e);
        }
    }

    private int getCompression(JSONObject opts) {
        return opts.has(CONF_COMPRESSION) ? opts.getInt(CONF_COMPRESSION) : 0;
    }

    private String getSchemaDirectory(JSONObject opts) {
        return opts.has(CONF_SCHEMA)
                ? opts.getString(CONF_SCHEMA)
                : FileUtils.getEnvironmentPath("CLAS12DIR", "etc/bankdefs/hipo");
    }


    @Override
    protected void closeWriter() {
        writer.close();
    }

    @Override
    protected void writeEvent(Object event) throws EventWriterException {
        try {
            writer.writeEvent((HipoEvent) event);
        } catch (Exception e) {
            throw new EventWriterException(e);
        }
    }

    @Override
    protected EngineDataType getDataType() {
        return Clas12Types.HIPO;
    }
}
