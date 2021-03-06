/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.stratos.manager.lb.category;

import org.apache.stratos.cloud.controller.pojo.CartridgeInfo;
import org.apache.stratos.manager.dao.Cluster;
import org.apache.stratos.manager.exception.ADCException;
import org.apache.stratos.manager.exception.AlreadySubscribedException;
import org.apache.stratos.manager.payload.PayloadData;
import org.apache.stratos.manager.repository.Repository;
import org.apache.stratos.manager.subscriber.Subscriber;

import java.util.Map;

public class ServiceLevelLoadBalancerCategory extends LoadBalancerCategory {

    public PayloadData create (String alias, Cluster cluster, Subscriber subscriber, Repository repository, CartridgeInfo cartridgeInfo,
                               String subscriptionKey, Map<String, String> customPayloadEntries) throws ADCException, AlreadySubscribedException {


        // add payload entry for load balanced service type
        PayloadData serviceLevelLbPayloadData = super.create(alias, cluster, subscriber, repository, cartridgeInfo, subscriptionKey,
                customPayloadEntries);
        serviceLevelLbPayloadData.add("LOAD_BALANCED_SERVICE_TYPE", getLoadBalancedServiceType());
        return serviceLevelLbPayloadData;
    }
}
