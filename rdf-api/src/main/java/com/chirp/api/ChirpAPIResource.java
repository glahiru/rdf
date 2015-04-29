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

import com.chirp.api.util.RDFFile;
import com.google.gson.Gson;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.Rule;
import com.hp.hpl.jena.util.FileManager;
import org.apache.commons.lang3.StringEscapeUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/")
public class ChirpAPIResource {
    private final static Logger logger = LoggerFactory.getLogger(ChirpAPIResource.class);

    public static final String CHIRP_API_VERSION = "0.1-SNAPSHOT";

    private ObjectMapper objectMapper;

    private List<RDFFile> rdfFiles;

    private Map<String,String> rdfMap;

    private final String basePath = "/Users/lginnali/masters/data-semantics/project/rdf/rdf-files";

    public ChirpAPIResource() throws Exception {
        this.objectMapper = new ObjectMapper();
        rdfFiles = new ArrayList<RDFFile>();
        rdfMap = new HashMap<String, String>();

        rdfFiles.add(new RDFFile("camera.rdf", "/Users/lginnali/masters/data-semantics/project/rdf/rdf-files/camerainfo.rdf"));
        rdfMap.put("camera.rdf", "/Users/lginnali/masters/data-semantics/project/rdf/rdf-files/camerainfo.rdf");
    }

    @POST
    @Path("/login")
    public Response login(InputStream incomingData){
        try {
            HashMap<String,String> parameters = objectMapper.readValue(getInputString(incomingData),
                    HashMap.class);
            String username = parameters.get("username");
            String password = parameters.get("password");
            if(username.equals(password)) {
                if (username.equals("admin")) {
                    return Response.status(200).entity("success").build();
                }
            }
            return Response.status(200).entity("fail").build();
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return Response.status(400).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/view")
    public Response getRDFByName(InputStream incomingData){
        try {
            HashMap<String,String> parameters = objectMapper.readValue(getInputString(incomingData),
                    HashMap.class);
            String name = parameters.get("name");
            String path = rdfMap.get(name);
            // reading the file
            String rdfContent = null;
            String line = null;

            FileInputStream fileInputStream = new FileInputStream(new File(path));
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            while((line=bufferedReader.readLine())!=null){
                rdfContent+=line;
                rdfContent += "\n";
            }
            return Response.status(200).entity(rdfContent).build();
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return Response.status(400).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/getRDFMeta")
    public Response getRDFMetadata(InputStream incomingData){
        try {
            List<Map> fullPostsInformation = new ArrayList<Map>();

            for (int i = 0; i < rdfFiles.size(); i++) {
                HashMap<String, String> stringStringHashMap = new HashMap<String, String>();
                stringStringHashMap.put("name", rdfFiles.get(i).getName());
                stringStringHashMap.put("path", rdfFiles.get(i).getPath());
                fullPostsInformation.add(stringStringHashMap);
            }
            Gson gson = new Gson();
            return Response.status(200).entity(gson.toJson(fullPostsInformation)).build();
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return Response.status(400).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/search")
    public Response getSettings(InputStream incomingData){
        try {
            HashMap<String,String> parameters = objectMapper.readValue(getInputString(incomingData),
                    HashMap.class);
            String name = parameters.get("name");
            String query = parameters.get("query");
            String rules = parameters.get("rule");

            String path = rdfMap.get(name);
            if(path==null){
                return Response.status(200).build();
            }

            // Create an empty in-memory model
            Model model = ModelFactory.createDefaultModel(); // default model this will be changed if there is a rule


            // use the FileManager to open the bloggers RDF graph from the filesystem
            InputStream in = FileManager.get().open(path);
            if (in == null) {
                throw new IllegalArgumentException("File: " + path + " not found");
            }

            // read the RDF/XML file
            model.read(in, "");
            InfModel infModel = null;
            QueryExecution qe = null;
            if(rules!=null && !rules.isEmpty()){
                Reasoner reasoner = new GenericRuleReasoner(Rule.parseRules(rules));
                infModel = ModelFactory.createInfModel(reasoner, model);
            }
            Query queryObj = QueryFactory.create(query);

            // Execute the query and obtain results
            if(infModel!=null) {
                 qe = QueryExecutionFactory.create(queryObj, infModel);
            }else {
                qe = QueryExecutionFactory.create(queryObj, model);
            }
            ResultSet results = qe.execSelect();
            String s = ResultSetFormatter.asText(results);
            s = StringEscapeUtils.escapeHtml3(s);
            return Response.status(200).entity(s).build();
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
