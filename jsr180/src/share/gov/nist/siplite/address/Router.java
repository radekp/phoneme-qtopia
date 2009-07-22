/*
 * Portions Copyright  2000-2009 Sun Microsystems, Inc. All Rights
 * Reserved.  Use is subject to license terms.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License version
 * 2 only, as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License version 2 for more details (a copy is
 * included at /legal/license.txt).
 * 
 * You should have received a copy of the GNU General Public License
 * version 2 along with this work; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA
 * 
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa
 * Clara, CA 95054 or visit www.sun.com if you need additional
 * information or have any questions.
 */
package gov.nist.siplite.address;
import gov.nist.siplite.message.*;
import gov.nist.siplite.*;
import java.util.*;

/**
 * Path for outbound message routing.
 */
public interface Router {

    /**
     * Return a linked list of addresses corresponding to a requestURI.
     * This is called for sending out outbound messages for which we do
     * not directly have the request URI. The implementaion function
     * is expected to return a linked list of addresses to which the
     * request is forwarded. The implementation may use this method
     * to perform location searches etc.
     *
     * @param sipRequest is the message to route.
     * @param isDialog target URI is taken from route list inside of dialog,
     * else it is taken from request URI
     * @return enumeration of next hops
     */
    public Enumeration getNextHops(Request sipRequest, boolean isDialog);
    
    /**
     * Sets the outbound proxy.
     * @param outboundProxy the outbound proxy address
     */
    public void setOutboundProxy(String outboundProxy);
    
    
    /**
     * Sets the sip stack.
     * @param sipStack the SIP stack context
     */
    public void setSipStack(SipStack sipStack);
    
    /**
     * Gets the outbound proxy.
     * @return the outbounc proxy address
     */
    public Hop getOutboundProxy();
    
    
}

