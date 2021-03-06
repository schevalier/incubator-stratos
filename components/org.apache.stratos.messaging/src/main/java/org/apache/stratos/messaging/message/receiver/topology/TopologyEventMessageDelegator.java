/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.stratos.messaging.message.receiver.topology;

import javax.jms.TextMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.stratos.messaging.message.processor.MessageProcessorChain;
import org.apache.stratos.messaging.message.processor.topology.*;
import org.apache.stratos.messaging.util.Constants;


/**
 * Implements logic for processing topology event messages based on a given
 * topology process chain.
 */
public class TopologyEventMessageDelegator implements Runnable {

    private static final Log log = LogFactory.getLog(TopologyEventMessageDelegator.class);
    private MessageProcessorChain processorChain;
    private boolean terminated;

    public TopologyEventMessageDelegator() {
        this.processorChain = new TopologyMessageProcessorChain();
    }

    public TopologyEventMessageDelegator(MessageProcessorChain processorChain) {
        this.processorChain = processorChain;
    }

    @Override
    public void run() {
        try {
            if (log.isInfoEnabled()) {
                log.info("Topology event message delegator started");
            }

            while (!terminated) {
                try {
                    TextMessage message = TopologyEventMessageQueue.getInstance().take();

                    // Retrieve the header
                    String type = message.getStringProperty(Constants.EVENT_CLASS_NAME);

                    // Retrieve the actual message
                    String json = message.getText();

                    if (log.isDebugEnabled()) {
                        log.debug(String.format("Topology event message received from queue: %s", type));
                    }

                    try {
                        TopologyManager.acquireWriteLock();
                        if (log.isDebugEnabled()) {
                            log.debug(String.format("Delegating topology event message: %s", type));
                        }
                        processorChain.process(type, json, TopologyManager.getTopology());
                    } finally {
                        TopologyManager.releaseWriteLock();
                    }

                } catch (Exception e) {
                    log.error("Failed to retrieve topology event message", e);
                }
            }
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Topology event message delegator failed", e);
            }
        }
    }

    /**
     * Terminate topology event message delegator thread.
     */
    public void terminate() {
        terminated = true;
    }
}
