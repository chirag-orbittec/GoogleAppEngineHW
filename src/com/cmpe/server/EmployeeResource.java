package com.cmpe.server;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

@Path("/employee")
public class EmployeeResource {

	@Path("/")
	@POST
	@Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
	public Response createEmployee(@Context UriInfo uriInfo,String employeeJSON) throws URISyntaxException
	{
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		Employee employee = new Employee(employeeJSON);

		Filter filter = new FilterPredicate("id",FilterOperator.EQUAL,employee.getId());
		Query q = new Query("Employee").setFilter(filter);
		PreparedQuery preparedQuery = datastore.prepare(q);
		if(preparedQuery.asSingleEntity()!=null)
		{
			URI location = new URI(uriInfo.getAbsolutePath() + "/" + employee.getId());
			return Response.status(Status.CONFLICT).location(location).build();
		}

		Entity entity = new Entity("Employee");
		entity.setProperty("id", employee.getId());
		entity.setProperty("firstName", employee.getFirstName());
		entity.setProperty("lastName", employee.getLastName());
		datastore.put(entity);
		URI location = new URI(uriInfo.getAbsolutePath() + "/" + employee.getId());
		return Response.ok(employee.getJSON(), MediaType.APPLICATION_JSON).status(Status.CREATED).location(location).build();
	}



	@Path("/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getEmployee(@PathParam("id") int id)
	{
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService(); 
		Filter filter = new FilterPredicate("id",FilterOperator.EQUAL,id);
		Query q = new Query("Employee").setFilter(filter);
		PreparedQuery preparedQuery = datastore.prepare(q);
		Entity entity = preparedQuery.asSingleEntity();
		if(preparedQuery.asSingleEntity()==null)
		{
			return Response.status(Status.NOT_FOUND).build();
		}
		Key key = entity.getKey();
		URI location = null;
		Employee employee= null;
		try 
		{ 
			Entity emp = datastore.get(key); 
			employee = new Employee();
			employee.setId(id);
			employee.setFirstName(emp.getProperty("firstName").toString());
			employee.setLastName(emp.getProperty("lastName").toString());
		}
		catch (Exception e) 
		{ 
			// TODO Auto-generated catch block 
			e.printStackTrace(); 
		}

		return Response.ok(employee.getJSON(), MediaType.APPLICATION_JSON).status(Status.OK).build();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getEmployee()
	{
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService(); 
		Query query = new Query("Employee");
		PreparedQuery preparedQuery = datastore.prepare(query);
		Iterable<Entity> entities = preparedQuery.asIterable();
		return Response.ok(Employee.createListJSON(entities), MediaType.APPLICATION_JSON).status(Status.OK).build();
	}

	@PUT
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateEmployee(@Context UriInfo uriInfo,@PathParam("id") int id,String employeeJSON) throws URISyntaxException
	{
		Employee employee = new Employee(employeeJSON);

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService(); 
		Filter filter = new FilterPredicate("id",FilterOperator.EQUAL,id);
		Query q = new Query("Employee").setFilter(filter);
		PreparedQuery preparedQuery = datastore.prepare(q);
		Entity entity = preparedQuery.asSingleEntity();
		URI location = null;
		if(entity==null)
		{
			location = new URI(uriInfo.getAbsolutePath() + "/" + id); 
			return Response.status(Status.NOT_FOUND).location(location).build();
		}

		try 
		{ 
			//Entity emp = datastore.get(key);
			System.out.println(entity);

			if(employee.getFirstName()!=null)
			{
				entity.setProperty("firstName", employee.getFirstName()); 
			}
			if(employee.getLastName()!=null)
			{
				entity.setProperty("lastName", employee.getLastName()); 
			}
			datastore.put(entity); 
			location = new URI(uriInfo.getAbsolutePath() + "/" + employee.getId()); 
		} 
		catch (Exception e) 
		{ 
			// TODO Auto-generated catch block 
			e.printStackTrace(); 
		}
		return Response.ok(employee.getJSON(), MediaType.APPLICATION_JSON).status(Status.OK).location(location).build();
	}
	
	@DELETE 
	@Path("/{id}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response deleteEmployee(@PathParam("id") int id) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService(); 
		Filter filter = new FilterPredicate("id",FilterOperator.EQUAL,id);
		Query q = new Query("Employee").setFilter(filter);
		PreparedQuery preparedQuery = datastore.prepare(q);
		Entity entity = preparedQuery.asSingleEntity();
		if(preparedQuery.asSingleEntity()==null)
		{
			return Response.status(Status.NOT_FOUND).build();
		}
		Key key = entity.getKey();
		datastore.delete(key);
		
		return Response.status(Status.OK).build();
	}

}
