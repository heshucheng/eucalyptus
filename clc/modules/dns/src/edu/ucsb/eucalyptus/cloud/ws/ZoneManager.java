/*
 * Software License Agreement (BSD License)
 *
 * Copyright (c) 2009, Eucalyptus Systems, Inc.
 * All rights reserved.
 *
 * Redistribution and use of this software in source and binary forms, with or
 * without modification, are permitted provided that the following conditions
 * are met:
 *
 * * Redistributions of source code must retain the above
 *   copyright notice, this list of conditions and the
 *   following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above
 *   copyright notice, this list of conditions and the
 *   following disclaimer in the documentation and/or other
 *   materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * Author: Neil Soman neil@eucalyptus.com
 */

package edu.ucsb.eucalyptus.cloud.ws;

import org.apache.log4j.Logger;
import org.xbill.DNS.*;
import org.xbill.DNS.Address;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Iterator;

import edu.ucsb.eucalyptus.cloud.entities.*;

public class ZoneManager {
    private static ConcurrentHashMap<Name, Zone> zones = new ConcurrentHashMap<Name, Zone>();
    private static Logger LOG = Logger.getLogger( ZoneManager.class );

    public static Zone getZone(String name) {
        try {
            return zones.get(new Name(name));
        } catch(Exception ex) {
            LOG.error(ex);
        }
        return null;
    }

    public static Zone getZone(Name name) {
        return zones.get(name);
    }

    public static void addZone(ZoneInfo zoneInfo, SOARecordInfo soaRecordInfo, NSRecordInfo nsRecordInfo) {
        try {
            String nameString = zoneInfo.getName();
            Name name =  Name.fromString(nameString);
            long soaTTL = soaRecordInfo.getTtl();
            long serial = soaRecordInfo.getSerialNumber();
            long refresh = soaRecordInfo.getRefresh();
            long retry = soaRecordInfo.getRetry();
            long expires = soaRecordInfo.getExpires();
            long minimum = soaRecordInfo.getMinimum();
            Record soarec = new SOARecord(name, DClass.IN, soaTTL, Name.root, Name.root, serial, refresh, retry, expires, minimum);
            long nsTTL = nsRecordInfo.getTtl();
            Record nsrec = new NSRecord(name, DClass.IN, nsTTL, Name.root);
            zones.putIfAbsent(name, new Zone(name, new Record[]{soarec, nsrec}));
        } catch(Exception ex) {
            LOG.error(ex);
        }
    }

    public static void addRecord(ARecordInfo arecInfo) {
        try {
            ARecord arecord = new ARecord(Name.fromString(arecInfo.getName()), DClass.IN, arecInfo.getTtl(), Address.getByAddress(arecInfo.getAddress()));
            addRecord(arecInfo.getZone(), arecord);
        } catch(Exception ex) {
            LOG.error(ex);
        }
    }

    public static void addRecord(String nameString, Record record) {
        Zone zone = getZone(nameString);
        if(zone == null) {
            try {
                Record[] records = new Record[1];
                records[0] = record;
                Name name =  Name.fromString(nameString);
                long soaTTL = 604800;
                long serial = 1;
                long refresh = 604800;
                long retry = 86400;
                long expires = 2419200;
                long minimum = 604800;
                Record soarec = new SOARecord(name, DClass.IN, soaTTL, name, Name.fromString("root." + nameString), serial, refresh, retry, expires, minimum);
                long nsTTL = soaTTL;
                Record nsrec = new NSRecord(name, DClass.IN, nsTTL, name);
                zone =  zones.putIfAbsent(name, new Zone(name, new Record[]{soarec, nsrec, record}));
                if(zone == null) {
                    zone = zones.get(name);
                    EntityWrapper<ZoneInfo> db = new EntityWrapper<ZoneInfo>();
                    ZoneInfo zoneInfo = new ZoneInfo(nameString);
                    db.add(zoneInfo);
                    EntityWrapper<SOARecordInfo> dbSOA = db.recast(SOARecordInfo.class);
                    SOARecordInfo soaRecordInfo = new SOARecordInfo();
                    soaRecordInfo.setName(nameString);
                    soaRecordInfo.setRecordclass(DClass.IN);
                    soaRecordInfo.setNameserver(Name.root.toString());
                    soaRecordInfo.setAdmin(Name.root.toString());
                    soaRecordInfo.setZone(nameString);
                    soaRecordInfo.setSerialNumber(serial);
                    soaRecordInfo.setTtl(soaTTL);
                    soaRecordInfo.setExpires(expires);
                    soaRecordInfo.setMinimum(minimum);
                    soaRecordInfo.setRefresh(refresh);
                    soaRecordInfo.setRetry(retry);
                    dbSOA.add(soaRecordInfo);

                    EntityWrapper<NSRecordInfo> dbNS = db.recast(NSRecordInfo.class);
                    NSRecordInfo nsRecordInfo = new NSRecordInfo();
                    nsRecordInfo.setName(nameString);
                    nsRecordInfo.setZone(nameString);
                    nsRecordInfo.setRecordClass(DClass.IN);
                    nsRecordInfo.setTarget(Name.root.toString());
                    nsRecordInfo.setTtl(nsTTL);
                    dbNS.add(nsRecordInfo);
                    db.commit();
                }
            } catch(Exception ex) {
                LOG.error(ex);
            }
        } else {
            zone.addRecord(record);
        }
    }

    public static void updateRecord(String zoneName, Record record) {
        try {
            Zone zone = getZone(zoneName);
            RRset rrSet = zone.findExactMatch(record.getName(), record.getDClass());
            Iterator<Record> rrIterator = rrSet.rrs();
            while(rrIterator.hasNext()) {
                Record rec = rrIterator.next();
                if(rec.getName().equals(record.getName())) {
                    zone.removeRecord(rec);
                }
            }
            zone.addRecord(record);
        } catch(Exception ex) {
            LOG.error(ex);
        }
    }
}