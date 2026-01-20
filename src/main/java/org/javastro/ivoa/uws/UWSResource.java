package org.javastro.ivoa.uws;


/*
 * Created on 08/01/2026 by Paul Harrison (paul.harrison@manchester.ac.uk).
 */

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.javastro.ivoa.entities.uws.*;
import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestResponse;

@Tag(name="UWSserver", description = "The standard UWS endpoints")
@ApplicationScoped
@Produces(MediaType.APPLICATION_XML)
@Path("/jobs")
public class UWSResource {

   //FIXME remove this dummy Job after initial OpenAPI prototyping
   Job dummyJob = Job.builder().withJobId("45").withOwnerId("person").
         withPhase(ExecutionPhase.EXECUTING).build();
   @POST
   public RestResponse<Job> create(String jdl, @Context UriInfo uriInfo) {
      //FIXME need to actually create job
      long jobid = 45L; //TODO set actual value
      return RestResponse.seeOther(uriInfo.getAbsolutePathBuilder()
            .path(Long.toString(jobid)).build());
   }
   @GET
   public Jobs listJobs(){
      return new Jobs();
   }

   @GET
   @Path("/{jobid}")
   public Job getJob(@RestPath String jobid){
      return dummyJob;
   }
   @GET
   @Path("/{jobid}/phase")
   public ExecutionPhase getJobPhase(@RestPath String jobid){
      return dummyJob.getPhase();
   }
   @GET
   @Path("/{jobid}/executionduration")
   public Integer getJobExecutionDuration(@RestPath String jobid){
      return dummyJob.getExecutionDuration();
   }
   @GET
   @Path("/{jobid}/destruction")
   public String getJobDestruction(@RestPath String jobid){
      return dummyJob.getDestruction().toString();
   }
   @GET
   @Path("/{jobid}/error")
   public String getJobDError(@RestPath String jobid){
      return dummyJob.getErrorSummary().getMessage();
   }
   @GET
   @Path("/{jobid}/owner")
   public String  getJobOwner(@RestPath String jobid){
      return dummyJob.getOwnerId();
   }

   @GET
   @Path("/{jobid}/quote")
   public String getJobQuote(@RestPath String jobid){
      return dummyJob.getQuote().toString();
   }
   @GET
   @Path("/{jobid}/results")
   public Results getJobResults(@RestPath String jobid){
      return dummyJob.getResults();
   }

   @GET
   @Path("/{jobid}/parameters")
   public Parameters getJobParameters(@RestPath String jobid){
      return dummyJob.getParameters();
   }


   private ShortJobDescription shorten(Job job)
   {
      return new ShortJobDescription(job.getPhase(), job.getRunId(), job.getOwnerId(),job.getCreationTime(), job.getJobId(), "type", "href");
   }


}
