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
package org.apache.stratos.cloud.controller.runtime;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.stratos.cloud.controller.pojo.*;
import org.apache.stratos.cloud.controller.registry.RegistryManager;
import org.apache.stratos.messaging.broker.publish.EventPublisher;
import org.wso2.carbon.databridge.agent.thrift.AsyncDataPublisher;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This object holds all runtime data and provides faster access. This is a Singleton class.
 */
public class FasterLookUpDataHolder implements Serializable{

    private static final long serialVersionUID = -2662307358852779897L;
    
    private static final Log log = LogFactory.getLog(FasterLookUpDataHolder.class);

	private static volatile FasterLookUpDataHolder ctxt;

	/* We keep following maps in order to make the look up time, small. */
	
	/**
     * Key - cluster id
     * Value - list of {@link MemberContext}
     */
    private Map<String, List<MemberContext>> clusterIdToMemberContext = new ConcurrentHashMap<String, List<MemberContext>>();
    

    /**
	 * Key - member id
	 * Value - {@link MemberContext}
	 */
	private Map<String, MemberContext> memberIdToContext = new ConcurrentHashMap<String, MemberContext>();
	
	/**
	 * Key - cluster id
	 * Value - {@link ClusterContext}
	 */
	private Map<String, ClusterContext> clusterIdToContext = new ConcurrentHashMap<String, ClusterContext>();
	
	/**
	 * List of registered {@link Cartridge}s
	 */
	private List<Cartridge> cartridges;

	/**
	 * List of IaaS Providers.
	 */
	private List<IaasProvider> iaasProviders;


	private String serializationDir;
	private boolean enableBAMDataPublisher;
	private transient DataPublisherConfig dataPubConfig;
	private boolean enableTopologySync;
	private transient TopologyConfig topologyConfig;
	
	/**
     * Key - name of the topic
     * Value - corresponding EventPublisher
     */
    private transient Map<String, EventPublisher> topicToPublisherMap = new HashMap<String, EventPublisher>();

	private transient AsyncDataPublisher dataPublisher;
	private String streamId;
	private boolean isPublisherRunning;
	private boolean isTopologySyncRunning;


	public static FasterLookUpDataHolder getInstance() {

		if (ctxt == null) {
			synchronized (FasterLookUpDataHolder.class) {
				if (ctxt == null && RegistryManager.getInstance() != null) {

					Object obj = RegistryManager.getInstance().retrieve();
					if (obj != null) {
						if (obj instanceof FasterLookUpDataHolder) {
							ctxt = (FasterLookUpDataHolder) obj;
						}
					} 
				}
				if(ctxt == null) {
					ctxt = new FasterLookUpDataHolder();
				}
			}
		}

		return ctxt;
	}

	private FasterLookUpDataHolder() {

		cartridges = new ArrayList<Cartridge>();

	}

	public List<Cartridge> getCartridges() {
		return cartridges;
	}
	
	public void setCartridges(List<Cartridge> cartridges) {
	    this.cartridges = cartridges;
	}

	public Cartridge getCartridge(String cartridgeType) {
		for (Cartridge cartridge : cartridges) {
			if (cartridge.getType().equals(cartridgeType)) {
				return cartridge;
			}
		}

		return null;

	}
	
	public void addCartridge(Cartridge newCartridges) {
	
		cartridges.add(newCartridges);
	}

	public void removeCartridges(List<Cartridge> cartridges) {
		if (this.cartridges != null) {
			this.cartridges.removeAll(cartridges);
		}

	}
	
	public IaasProvider getIaasProvider(String type) {
	    if(type == null) {
	        return null;
	    }
	    
	    for (IaasProvider iaasProvider : iaasProviders) {
            if(type.equals(iaasProvider.getType())) {
                return iaasProvider;
            }
        }
	    return null;
	}

	public List<IaasProvider> getIaasProviders() {
		return iaasProviders;
	}

	public void setIaasProviders(List<IaasProvider> iaasProviders) {
		this.iaasProviders = iaasProviders;
	}

	public String getSerializationDir() {
		return serializationDir;
	}

	public void setSerializationDir(String serializationDir) {
		this.serializationDir = serializationDir;
	}

	public AsyncDataPublisher getDataPublisher() {
		return dataPublisher;
	}

	public void setDataPublisher(AsyncDataPublisher dataPublisher) {
		this.dataPublisher = dataPublisher;
	}

	public String getStreamId() {
		return streamId;
	}

	public void setStreamId(String streamId) {
		this.streamId = streamId;
	}

	public boolean getEnableBAMDataPublisher() {
		return enableBAMDataPublisher;
	}

	public void setEnableBAMDataPublisher(boolean enableBAMDataPublisher) {
		this.enableBAMDataPublisher = enableBAMDataPublisher;
	}

	public boolean isPublisherRunning() {
		return isPublisherRunning;
	}

	public void setPublisherRunning(boolean isPublisherRunning) {
		this.isPublisherRunning = isPublisherRunning;
	}

	public boolean getEnableTopologySync() {
		return enableTopologySync;
	}

	public void setEnableTopologySync(boolean enableTopologySync) {
		this.enableTopologySync = enableTopologySync;
	}

	public boolean isTopologySyncRunning() {
	    return isTopologySyncRunning;
    }

	public void setTopologySyncRunning(boolean isTopologySyncRunning) {
	    this.isTopologySyncRunning = isTopologySyncRunning;
    }

	public TopologyConfig getTopologyConfig() {
		return topologyConfig;
	}

	public void setTopologyConfig(TopologyConfig topologyConfig) {
		this.topologyConfig = topologyConfig;
	}
	
	public EventPublisher getEventPublisher(String topic){
    	return topicToPublisherMap.get(topic);
    }
	
	public List<EventPublisher> getAllEventPublishers() {
		return new ArrayList<EventPublisher>(topicToPublisherMap.values());
	}
	
    public void addEventPublisher(EventPublisher publisher, String topicName) {
        topicToPublisherMap.put(topicName, publisher);
    }

    public DataPublisherConfig getDataPubConfig() {
        return dataPubConfig;
    }

    public void setDataPubConfig(DataPublisherConfig dataPubConfig) {
        this.dataPubConfig = dataPubConfig;
    }
    
    public void addMemberContext(MemberContext ctxt) {
        memberIdToContext.put(ctxt.getMemberId(), ctxt);
        
        List<MemberContext> ctxts;
        
        if((ctxts = clusterIdToMemberContext.get(ctxt.getClusterId())) == null) {
            ctxts = new ArrayList<MemberContext>();
        } 
        if(ctxts.contains(ctxt)) {
        	ctxts.remove(ctxt);
        }
        ctxts.add(ctxt);
        clusterIdToMemberContext.put(ctxt.getClusterId(), ctxts);
        if(log.isDebugEnabled()) {
        	
        	log.debug("Added Member Context to the information model. "+ctxt);
        }
    }
    
    public void removeMemberContextsOfCluster(String clusterId) {
        List<MemberContext> ctxts = clusterIdToMemberContext.remove(clusterId);
        if(ctxts == null) {
            return;
        }
        for (MemberContext memberContext : ctxts) {
            String memberId = memberContext.getMemberId();
            memberIdToContext.remove(memberId);
        }
        if(log.isDebugEnabled()) {
        	
        	log.debug("Removed Member Context from the information model. "+ctxt);
        }
    }
    
    public void removeMemberContext(String memberId, String clusterId) {
    	memberIdToContext.remove(memberId);
        List<MemberContext> ctxts = clusterIdToMemberContext.get(clusterId);
        if(ctxts == null) {
            return;
        }
        
        List<MemberContext> newCtxts =  new ArrayList<MemberContext>(ctxts);
        
        for (Iterator<MemberContext> iterator = newCtxts.iterator(); iterator.hasNext();) {
			MemberContext memberContext = (MemberContext) iterator.next();
			if(memberId.equals(memberContext.getMemberId())) {
				if(log.isDebugEnabled()) {
					
					log.debug("MemberContext [id]: "+memberId+" removed from information model.");
				}
            	iterator.remove();
            }
		}
        
        clusterIdToMemberContext.put(clusterId, newCtxts);
    }
    
    public MemberContext getMemberContextOfMemberId(String memberId) {
        return memberIdToContext.get(memberId);
    }
    
    public List<MemberContext> getMemberContextsOfClusterId(String clusterId) {
        return clusterIdToMemberContext.get(clusterId);
    }

    public Map<String, List<MemberContext>> getClusterIdToMemberContext() {
        return clusterIdToMemberContext;
    }
    
    public void setClusterIdToMemberContext(Map<String, List<MemberContext>> clusterIdToMemberContext) {
        this.clusterIdToMemberContext = clusterIdToMemberContext;
    }

    public Map<String, MemberContext> getMemberIdToContext() {
        return memberIdToContext;
    }

    public void setMemberIdToContext(Map<String, MemberContext> memberIdToContext) {
        this.memberIdToContext = memberIdToContext;
    }

    public void addClusterContext(ClusterContext ctxt) {
        clusterIdToContext.put(ctxt.getClusterId(), ctxt);
    }
    
    public ClusterContext getClusterContext(String clusterId) {
        return clusterIdToContext.get(clusterId);
    }
    
    public ClusterContext removeClusterContext(String clusterId) {
        return clusterIdToContext.remove(clusterId);
    }
    
    public Map<String, ClusterContext> getClusterIdToContext() {
        return clusterIdToContext;
    }

    public void setClusterIdToContext(Map<String, ClusterContext> clusterIdToContext) {
        this.clusterIdToContext = clusterIdToContext;
    }

}