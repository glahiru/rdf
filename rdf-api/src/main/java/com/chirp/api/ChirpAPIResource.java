/*
 *
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
 *
*/
package com.chirp.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Path("/")
public class ChirpAPIResource {
    private final static Logger logger = LoggerFactory.getLogger(ChirpAPIResource.class);

    public static final String CHIRP_API_VERSION = "0.1-SNAPSHOT";


    public ChirpAPIResource() throws Exception {
    }

    @POST
    @Path("/view")
    public Response removePrivacySettings(InputStream incomingData){
        try {
            return Response.status(200).build();
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return Response.status(400).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/search")
    public Response getSettings(InputStream incomingData){
        try {
            return Response.status(200).build();
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return Response.status(400).entity(e.getMessage()).build();
        }
    }

    /**
     * Method to get the input data as string
     * @param incomingData
     * @return
     * @throws java.io.IOException
     */
    private String getInputString(InputStream incomingData) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
        String line = null;
        while ((line = in.readLine()) != null) {
            stringBuilder.append(line);
        }
        return stringBuilder.toString();
    }
}
