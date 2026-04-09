package org.javastro.ivoa.uws;


/*
 * Created on 08/01/2026 by Paul Harrison (paul.harrison@manchester.ac.uk).
 */

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.javastro.ivoacore.common.ServiceLocator;
import org.javastro.ivoacore.uws.BaseUWSJob;
import org.javastro.ivoacore.uws.JobManager;
import org.javastro.ivoacore.uws.SimpleLambdaJob;
import org.javastro.ivoacore.uws.UWSException;
import org.javastro.ivoacore.uws.environment.execution.ParameterValue;
import org.javastro.ivoacore.uws.webapi.BaseUWSResource;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.Optional;

@Tag(name="UWS", description = "The IVOA standard UWS endpoints")
@ApplicationScoped
@Produces(MediaType.APPLICATION_XML)
@Path("/jobs")
public class UWSResource extends BaseUWSResource {

   @Inject
   JobManager  jobManager;

   @Inject
   ServiceLocator serviceLocator;

   @Override
   protected JobManager getJobManager() {
      return jobManager;
   }

   @Override
   protected Response redirectToJob(String jobid) {
      final UriBuilder urib = UriBuilder.fromUri(serviceLocator.serviceURI());
      if (jobid != null && !jobid.isEmpty()) {
         urib.path(jobid);
      }
      return Response.seeOther(urib
            .build()).build();
   }

   @POST
   public Response create(@RestForm String jdl, @Context UriInfo uriInfo) throws UWSException { //TODO this needs to be generalized more
      SimpleLambdaJob.Specification spec = new SimpleLambdaJob.Specification(jdl, "myrefID");
      BaseUWSJob job = jobManager.createJob(spec);
      Response retval = Response.seeOther(uriInfo.getAbsolutePathBuilder()
            .path(job.getID()).build()).build();
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

}
