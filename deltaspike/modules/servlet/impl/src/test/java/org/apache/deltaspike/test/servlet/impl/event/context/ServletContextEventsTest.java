/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.deltaspike.test.servlet.impl.event.context;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.inject.Inject;

import org.apache.deltaspike.test.category.WebProfileCategory;
import org.apache.deltaspike.test.servlet.impl.Deployments;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.spec.servlet.web.WebAppDescriptor;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

/**
 * Test which validates that CDI events are fired when the servlet context is created.
 */
@RunWith(Arquillian.class)
@Category(WebProfileCategory.class)
public class ServletContextEventsTest
{

    private final static String MODULE = "servlet-context-events-test";

    @Deployment
    public static WebArchive getDeployment()
    {
        return ShrinkWrap.create(WebArchive.class, "test.war")
                .addAsLibraries(Deployments.getDeltaSpikeCoreArchives())
                .addAsLibraries(Deployments.getDeltaSpikeServletArchives())
                .addAsLibraries(Deployments.getTestSupportArchives())
                .addClass(ServletContextEventsObserver.class)
                .addAsWebInfResource(new StringAsset("<beans/>"), "beans.xml")
                .addAsWebResource(new StringAsset("foobar"), "foobar.txt")
                .setWebXML(new StringAsset(
                        Descriptors.create(WebAppDescriptor.class)
                                .displayName(MODULE)
                                .exportAsString()));

    }

    @Inject
    private ServletContextEventsObserver observer;

    @Test
    public void shouldReceiveServletContextInitializedEvent()
    {
        assertEquals(1, observer.getEventCount());
        assertTrue("Didn't receive expected event",
                observer.getEventLog().contains("Initialized ServletContext: " + MODULE));
    }

}
