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
package com.chirp.api.util;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Example {
    public static void main (String args[]) {
        String SOURCE = "http://www.opentox.org/api/1.1";
        String NS = SOURCE + "#";
        //create a model using reasoner
        OntModel model1 = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM_MICRO_RULE_INF);
        //create a model which doesn't use a reasoner
        OntModel model2 = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);

        // read the RDF/XML file
        model1.read( SOURCE, "RDF/XML" );
        model2.read( SOURCE, "RDF/XML" );
        //prints out the RDF/XML structure
//        qe.close();
        System.out.println(" ");


        // Create a new query
        String queryString =
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "+
                        "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>  "+
                        "select ?uri "+
                        "where { "+
                        "?uri rdfs:subClassOf <http://www.opentox.org/api/1.1#Feature>  "+
                        "} \n ";
        Query query = QueryFactory.create(queryString);

        System.out.println("----------------------");

        System.out.println("Query Result Sheet");

        System.out.println("----------------------");

        System.out.println("Direct&Indirect Descendants (model1)");

        System.out.println("-------------------");


        // Execute the query and obtain results
        QueryExecution qe = QueryExecutionFactory.create(query, model1);
        com.hp.hpl.jena.query.ResultSet results =  qe.execSelect();

        // Output query results
        ResultSetFormatter.out(System.out, results, query);

        qe.close();

        System.out.println("----------------------");
        System.out.println("Only Direct Descendants");
        System.out.println("----------------------");

        // Execute the query and obtain results
        qe = QueryExecutionFactory.create(query, model2);
        results =  qe.execSelect();

        // Output query results
        ResultSetFormatter.out(System.out, results, query);
        qe.close();
    }
}
