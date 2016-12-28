/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.tomitribe.jcache.cdi;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.configuration.Configuration;
import javax.cache.spi.CachingProvider;
import javax.ejb.embeddable.EJBContainer;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import java.util.Random;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

public class ExtraJCacheExtensionTest
{
    public ExtraJCacheExtensionTest() {
        final String logging = "hazelcast.logging.type";
        if (System.getProperty(logging) == null) {
            System.setProperty(logging, "jdk");
        }

        System.setProperty("hazelcast.version.check.enabled", "false");
        System.setProperty("hazelcast.mancenter.enabled", "false");
        System.setProperty("hazelcast.wait.seconds.before.join", "1");
        System.setProperty("hazelcast.local.localAddress", "127.0.0.1");
        System.setProperty("java.net.preferIPv4Stack", "true");
        System.setProperty("hazelcast.jmx", "true");

        // randomize multicast group...
        Random rand = new Random();
        int g1 = rand.nextInt(255);
        int g2 = rand.nextInt(255);
        int g3 = rand.nextInt(255);
        System.setProperty("hazelcast.multicast.group", "224." + g1 + "." + g2 + "." + g3);
        System.setProperty("hazelcast.jcache.provider.type", "server");
    }

    private EJBContainer container;

    @Before
    public void setUp() {
        try {
            container = EJBContainer.createEJBContainer();
            container.getContext().bind("inject", this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @After
    public void cleanUp() {
        try {
            container.getContext().unbind("inject");
            container.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Inject
    private BeanWithInjections bean;

    @Test
    public void defaultCacheManager()
    {
        assertNotNull(bean.getMgr());
    }

    @Test
    public void defaultCacheProvider()
    {
        assertNotNull(bean.getProvider());
    }

    @Test
    public void declarativeCacheCache1() {
        Cache<String, String> cache1 = bean.getCache1();
        assertNotNull(cache1);
        Configuration configuration = cache1.getConfiguration(Configuration.class);
        assertSame(String.class, configuration.getKeyType());
        assertSame(String.class, configuration.getValueType());
    }

    @Test
    public void declarativeCacheCache2() {
        Cache<Integer, Integer> cache2 = bean.getCache2();
        assertNotNull(cache2);
        Configuration configuration = cache2.getConfiguration(Configuration.class);
        assertSame(Integer.class, configuration.getKeyType());
        assertSame(Integer.class, configuration.getValueType());
    }

    @Test
    public void declarativeCachesNotSame() {
        Cache<String, String> cache1 = bean.getCache1();
        Cache<Integer, Integer> cache2 = bean.getCache2();
        assertNotSame(cache1, cache2);
    }

    public static class BeanWithInjections {
        @Inject
        private CacheManager mgr;

        @Inject
        private CachingProvider provider;

        @Inject
        @DeclarativeCache(value = "test1", keyType = String.class, valueType = String.class)
        private Cache<String, String> cache1;

        @Inject
        @DeclarativeCache(value = "test2", keyType = Integer.class, valueType = Integer.class)
        private Cache<Integer, Integer> cache2;

        @Inject
        @DeclarativeCache(value = "test3")
        private Cache<Object, Object> cache3;

        public CacheManager getMgr()
        {
            return mgr;
        }

        public CachingProvider getProvider()
        {
            return provider;
        }

        public Cache<String, String> getCache1()
        {
            return cache1;
        }

        public Cache<Integer, Integer> getCache2()
        {
            return cache2;
        }
    }

}
