package org.jlab.epsci.ersap.ejfat.io.be;

import com.lmax.disruptor.EventFactory;

/**
 * Copyright (c) 2021, Jefferson Science Associates, all rights reserved.
 * See LICENSE.txt file.
 * Thomas Jefferson National Accelerator Facility
 * Experimental Physics Software and Computing Infrastructure Group
 * 12000, Jefferson Ave, Newport News, VA 23606
 * Phone : (757)-269-7100
 *
 * @author gurjyan on 2/22/22
 * @project ersap-ejfat
 */
public class RingEjfatEventFactory implements EventFactory<RingEjfatEvent> {

    @Override
    public RingEjfatEvent newInstance() {
        return new RingEjfatEvent();
    }
}
