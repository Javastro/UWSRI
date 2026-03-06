package org.javastro.ivoa.uws;


/*
 * Created on 08/01/2026 by Paul Harrison (paul.harrison@manchester.ac.uk).
 */

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.javastro.ivoa.entities.uws.*;
import org.javastro.ivoacore.uws.BaseUWSJob;
import org.javastro.ivoacore.uws.JobManager;
import org.javastro.ivoacore.uws.SimpleLambdaJob;
import org.javastro.ivoacore.uws.UWSException;
import org.javastro.ivoacore.uws.environment.execution.ParameterValue;
import org.javastro.ivoacore.uws.webapi.BaseUWSResource;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.jaxrs.ResponseBuilderImpl;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.stream.Stream;

@Tag(name="UWS", description = "The IVOA standard UWS endpoints")
@ApplicationScoped
@Produces(MediaType.APPLICATION_XML)
@Path("/jobs")
public class UWSResource extends BaseUWSResource {

   @Inject
   JobManager  jobManager;

   @Override
   protected JobManager getJobManager() {
      return jobManager;
   }

   @POST
   public Response create(@RestForm String jdl, @Context UriInfo uriInfo) throws UWSException { //TODO this needs to be generalized more
      SimpleLambdaJob.Specification spec = new SimpleLambdaJob.Specification(jdl, "myrefID");
      BaseUWSJob job = jobManager.createJob(spec);
      Response retval = new ResponseBuilderImpl().location(uriInfo.getAbsolutePathBuilder()
            .path(job.getID()).build()).status(Response.Status.SEE_OTHER).build();
// IMPL quarkus doc says below should work....            
//      RestResponse retval = RestResponse.seeOther(uriInfo.getAbsolutePathBuilder()
//            .path(job.getID()).build());
      
      return retval;
   }

   @POST
   @Path("/{jobid}/phase")
   public Response setPhase(@PathParam("jobid") String jobid, @FormParam("PHASE") String phase, @Context UriInfo uriInfo) throws UWSException {
      ExecutionPhase newphase = jobManager.setPhase(jobid, phase);
      Response retval = new ResponseBuilderImpl().location(uriInfo.getAbsolutePathBuilder()
            .path(jobid).build()).status(Response.Status.SEE_OTHER).build();
      return retval;
   }

   @GET
   @Path("/{jobid}/results/{resultid}")
   public RestResponse<String> getAResult(@PathParam("jobid") String jobid, @PathParam("resultid") String resultid) throws UWSException {
     //FIXME too simplistic - want more complex mapping between job products and where and how they appear.
      Optional<ParameterValue> result = jobManager.getJobResults(jobid).stream().filter(p -> p.getId().equals(resultid)).findFirst();
      if (result.isPresent()) {
         return RestResponse.ok(result.get().getValue());
      }
      else
         return RestResponse.notFound();

   }

   @Override
   @POST
   @Path("/{jobid}/destruction")
   public Response setDestruction(@PathParam("jobid")String jobId, @FormParam("DESTRUCTION") ZonedDateTime destructionTime) throws UWSException {
      throw new UWSException("Not implemented");
   }

   @POST
   @Path("/{jobid}/executionduration")
   @Override
   public Response setExecutionDuration(@PathParam("jobid")String jobId, @FormParam("EXECUTIONDURATION") Long executionDuration) throws UWSException {
      throw new UWSException("Not implemented");
   }

   @Override
   @DELETE
   @Path("/{jobid}")
   public Response deleteJob(@PathParam("jobid")String jobid) throws UWSException {
      throw new UWSException("Not supported yet.");
   }
}
