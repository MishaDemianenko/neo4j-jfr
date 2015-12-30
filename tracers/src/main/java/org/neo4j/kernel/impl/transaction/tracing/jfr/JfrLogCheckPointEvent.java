/**
 * Copyright (c) 2002-2015 "Neo Technology,"
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
package org.neo4j.kernel.impl.transaction.tracing.jfr;

import com.oracle.jrockit.jfr.EventDefinition;
import com.oracle.jrockit.jfr.TimedEvent;

import org.neo4j.kernel.impl.transaction.tracing.LogCheckPointEvent;
import org.neo4j.kernel.impl.transaction.tracing.LogForceEvent;
import org.neo4j.kernel.impl.transaction.tracing.LogForceWaitEvent;

@EventDefinition( path = "neo4j/transaction/checkpoint" )
public class JfrLogCheckPointEvent extends TimedEvent implements LogCheckPointEvent
{
    protected JfrLogCheckPointEvent()
    {
        super( JfrTransactionTracer.logCheckPointToken );
    }

    @Override
    public void close()
    {
        commit();
    }

    @Override
    public LogForceWaitEvent beginLogForceWait()
    {
        JfrLogForceWaitEvent event = new JfrLogForceWaitEvent();
        event.begin();
        return event;
    }

    @Override
    public LogForceEvent beginLogForce()
    {
        JfrLogForceEvent event = new JfrLogForceEvent();
        event.begin();
        return event;
    }
}