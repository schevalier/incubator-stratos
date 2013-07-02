/**
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at

 *  http://www.apache.org/licenses/LICENSE-2.0

 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.apache.stratos.mediator.autoscale.lbautoscale.callables;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.stratos.mediator.autoscale.lbautoscale.clients.CloudControllerClient;

import java.util.concurrent.Callable;

/** Calculate pending instances of each service domain, sub domain combination **/
public class PendingInstanceCountCallable implements Callable<Integer> {

    private static final Log log = LogFactory.getLog(PendingInstanceCountCallable.class);
    private String domain;
    private String subDomain;
    private CloudControllerClient client;
    
    public PendingInstanceCountCallable(String domain, String subDomain, CloudControllerClient client){
        this.domain = domain;
        this.subDomain = subDomain;
        this.client = client;
    }
    
    @Override
    public Integer call() throws Exception {
        int pendingInstanceCount = 0;

        try {
            pendingInstanceCount =
                client.getPendingInstanceCount(this.domain,
                    this.subDomain);

        } catch (Exception e) {
            log.error("Failed to retrieve pending instance count for domain: " +
                this.domain + " and sub domain: " + this.subDomain, e);
        }

        log.debug("Pending instance count for domain: " +
            this.domain +
            ", sub domain: " +
            this.subDomain +
            " is " +
            pendingInstanceCount);

        return pendingInstanceCount;
    }

}
