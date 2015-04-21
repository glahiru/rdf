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
package com.chirp.api.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

public class ChirpAPIProperties {
    private final static Logger logger = LoggerFactory.getLogger(ChirpAPIProperties.class);
    private Properties properties = null;
    private static ChirpAPIProperties instance;

    public static final String SERVER_PROPERTY_FILE = "conf/chirp-api.properties";
    public static final String DEFAULT_SERVER_PROPERTY_FILE = "conf/chirp-api.properties";

    private ChirpAPIProperties(){
        try {
            InputStream fileInput;
            if (new File(SERVER_PROPERTY_FILE).exists()) {
                fileInput = new FileInputStream(SERVER_PROPERTY_FILE);
            } else {
                // try to load default parser.properties from class loader.
                fileInput = this.getClass().getClassLoader().getResourceAsStream(DEFAULT_SERVER_PROPERTY_FILE);
            }
            Properties properties = new Properties();
            properties.load(fileInput);
            fileInput.close();
            this.properties = properties;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ChirpAPIProperties getInstance(){
        if(ChirpAPIProperties.instance == null){
            ChirpAPIProperties.instance = new ChirpAPIProperties();
        }

        return ChirpAPIProperties.instance;
    }

    public String getProperty(String key, String defaultVal){
        String val = this.properties.getProperty(key);
        if(val.isEmpty() || val == ""){
            return defaultVal;
        }else{
            return val;
        }
    }

    public boolean containsKey(String loadBalancingPolicy) {
        return this.properties.containsKey(loadBalancingPolicy);
    }
}