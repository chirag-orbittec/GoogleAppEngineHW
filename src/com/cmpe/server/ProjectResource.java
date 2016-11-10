package com.cmpe.server;

import java.net.URI;
import java.net.URISyntaxException;

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
import com.google.appengine.api.search.StatusCode;

@Path("/project")
public class ProjectResource {

	@Path("/")
	@POST
	@Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
	public Response createProject(@Context UriInfo uriInfo,String ProjectJSON) throws URISyntaxException
	{
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		Project Project = new Project(ProjectJSON);

		Filter filter = new FilterPredicate("id",FilterOperator.EQUAL,Project.getId());
		Query q = new Query("Project").setFilter(filter);
		PreparedQuery preparedQuery = datastore.prepare(q);
		if(preparedQuery.asSingleEntity()!=null)
		{
			URI location = new URI(uriInfo.getAbsolutePath() + "/" + Project.getId());
			return Response.status(Status.CONFLICT).location(location).build();
		}

		Entity entity = new Entity("Project");
		entity.setProperty("id", Project.getId());
		entity.setProperty("name", Project.getName());
		entity.setProperty("budget", Project.getBudget());
		datastore.put(entity);
		URI location = new URI(uriInfo.getAbsolutePath() + "/" + Project.getId());
		return Response.ok(Project.getJSON(), MediaType.APPLICATION_JSON).status(Status.CREATED).location(location).build();
	}



	@Path("/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getProject(@PathParam("id") int id)
	{
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService(); 
		Filter filter = new FilterPredicate("id",FilterOperator.EQUAL,id);
		Query q = new Query("Project").setFilter(filter);
		PreparedQuery preparedQuery = datastore.prepare(q);
		Entity entity = preparedQuery.asSingleEntity();
		
		
		if(entity==null)
		{
			return Response.status(Status.NOT_FOUND).build();
		}
		
		Key key = entity.getKey();
		URI location = null;
		Project Project= null;
		try 
		{ 
			Entity emp = datastore.get(key); 
			Project = new Project();
			Project.setId(id);
			Project.setName(emp.getProperty("name").toString());
			Project.setBudget((float)emp.getProperty("budget"));
		}
		catch (Exception e) 
		{ 
			// TODO Auto-generated catch block 
			e.printStackTrace(); 
		}

		return Response.ok(Project.getJSON(), MediaType.APPLICATION_JSON).status(Status.OK).build();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getProject()
	{
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService(); 
		Query query = new Query("Project");
		PreparedQuery preparedQuery = datastore.prepare(query);
		Iterable<Entity> entities = preparedQuery.asIterable();
		return Response.ok(Project.createListJSON(entities), MediaType.APPLICATION_JSON).status(Status.OK).build();
	}

	@PUT
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateProject(@Context UriInfo uriInfo,@PathParam("id") int id,String ProjectJSON) throws URISyntaxException
	{
		Project Project = new Project(ProjectJSON);

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService(); 
		Filter filter = new FilterPredicate("id",FilterOperator.EQUAL,id);
		Query q = new Query("Project").setFilter(filter);
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

			if(Project.getName()!=null)
			{
				entity.setProperty("name", Project.getName()); 
			}
			if(Project.getBudget()!=null)
			{
				entity.setProperty("budget", Project.getBudget()); 
			}
			datastore.put(entity); 
			location = new URI(uriInfo.getAbsolutePath() + "/" + Project.getId()); 
		} 
		catch (Exception e) 
		{ 
			// TODO Auto-generated catch block 
			e.printStackTrace(); 
		}
		return Response.ok(Project.getJSON(), MediaType.APPLICATION_JSON).status(Status.OK).location(location).build();
	}

	@DELETE 
	@Path("/{id}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response deleteEmployee(@PathParam("id") int id) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService(); 
		Filter filter = new FilterPredicate("id",FilterOperator.EQUAL,id);
		Query q = new Query("Project").setFilter(filter);
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
