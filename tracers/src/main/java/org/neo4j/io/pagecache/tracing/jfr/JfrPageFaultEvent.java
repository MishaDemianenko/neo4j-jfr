/**
 * Copyright (c) 2002-2017 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.io.pagecache.tracing.jfr;

import com.oracle.jrockit.jfr.ContentType;
import com.oracle.jrockit.jfr.EventDefinition;
import com.oracle.jrockit.jfr.TimedEvent;
import com.oracle.jrockit.jfr.ValueDefinition;

import java.util.concurrent.atomic.AtomicLong;

import org.neo4j.io.pagecache.tracing.EvictionEvent;
import org.neo4j.io.pagecache.tracing.PageFaultEvent;

@EventDefinition(path = "neo4j/io/pagecache/fault")
public class JfrPageFaultEvent extends TimedEvent implements PageFaultEvent
{
    private final AtomicLong bytesReadTotal;
    private final EvictionEventStarter evictionEventStarter;

    @ValueDefinition(name = "pinEventId", relationKey = JfrPinEvent.REL_KEY_PIN_EVENT_ID)
    private long pinEventId;
    @ValueDefinition(name = "filePageId")
    private long filePageId;
    @ValueDefinition(name = "filename")
    private String filename;
    @ValueDefinition(name = "bytesRead", contentType = ContentType.Bytes)
    private long bytesRead;
    @ValueDefinition(name = "gotException")
    private boolean gotException;
    @ValueDefinition(name = "exceptionMessage")
    private String exceptionMessage;
    @ValueDefinition(name = "cachePageId")
    private int cachePageId;

    public JfrPageFaultEvent( AtomicLong bytesRead, EvictionEventStarter evictionEventStarter )
    {
        super( JfrPageCacheTracer.faultToken );
        bytesReadTotal = bytesRead;
        this.evictionEventStarter = evictionEventStarter;
    }

    @Override
    public void addBytesRead( long bytes )
    {
        this.bytesRead += bytes;
        bytesReadTotal.getAndAdd( bytes );
    }

    @Override
    public void done()
    {
        end();
        commit();
    }

    @Override
    public void done( Throwable throwable )
    {
        this.gotException = true;
        this.exceptionMessage = throwable.getMessage();
        done();
    }

    @Override
    public EvictionEvent beginEviction()
    {
        return evictionEventStarter.startEviction();
    }

    public void setPinEventId( long pinEventId )
    {
        this.pinEventId = pinEventId;
    }

    public void setFilePageId( long filePageId )
    {
        this.filePageId = filePageId;
    }

    public void setFilename( String filename )
    {
        this.filename = filename;
    }

    public String getExceptionMessage()
    {
        return exceptionMessage;
    }

    public boolean getGotException()
    {
        return gotException;
    }

    public long getBytesRead()
    {
        return bytesRead;
    }

    public String getFilename()
    {
        return filename;
    }

    public long getFilePageId()
    {
        return filePageId;
    }

    public long getPinEventId()
    {
        return pinEventId;
    }

    public int getCachePageId()
    {
        return cachePageId;
    }

    @Override
    public void setCachePageId( int cachePageId )
    {
        this.cachePageId = cachePageId;
    }
}
