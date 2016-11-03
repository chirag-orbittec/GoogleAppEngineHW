package com.cmpe.jerseyTest;

import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
//The Java class will be hosted at the URI path "/helloworld"
@Path("/helloworld")
public class HelloWorld {

   // The Java method will process HTTP GET requests
   @GET
   // The Java method will produce content identified by the MIME Media
   // type "text/plain"
   @Produces("application/json")
   public String getClichedMessage() {
       // Return some cliched textual content
	   final Datastore datastore = DatastoreOptions.defaultInstance().service();
	   final KeyFactory keyFactory = datastore.newKeyFactory().kind("Employee");
	   
	   
	   Key key = datastore.allocateId(keyFactory.newKey());
	   Entity employee = Entity.builder(key).set("firstName", "chirag")
			   .set("lastName", "Patel").build();
	   datastore.put(employee);
	   
	   final Logger log = Logger.getLogger(HelloWorld.class.getName());
	   log.info("Your information Hello world message.");
	   
	   return "Hello World";
   }
}