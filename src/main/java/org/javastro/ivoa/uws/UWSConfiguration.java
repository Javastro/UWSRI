package org.javastro.ivoa.uws;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.javastro.ivoacore.uws.JobFactoryAggregator;
import org.javastro.ivoacore.uws.JobManager;
import org.javastro.ivoacore.uws.SimpleLambdaJob;
import org.javastro.ivoacore.uws.environment.DefaultExecutionEnvironment;
import org.javastro.ivoacore.uws.environment.DefaultExecutionPolicy;
import org.javastro.ivoacore.uws.persist.MemoryBasedJobStore;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/*
 * Created on 05/03/2026 by Paul Harrison (paul.harrison@manchester.ac.uk).
 */
@ApplicationScoped
public class UWSConfiguration {
   @Produces
   JobManager uws() {
      File tmpdir = null;
      try {
         tmpdir = Files.createTempDirectory("uwsserver").toFile();
      } catch (IOException e) {
         throw new RuntimeException("temporary directory not available",e);
      }
      JobFactoryAggregator agg = new JobFactoryAggregator();
      agg.addFactory(new SimpleLambdaJob.JobFactory(s-> {
         try {
            Thread.sleep(2300);
         } catch (InterruptedException e) {
            throw new RuntimeException(e); //TODO review exception handling
         }
         return "hello "+s;}));

      DefaultExecutionEnvironment env = new DefaultExecutionEnvironment(tmpdir);
      MemoryBasedJobStore store = new MemoryBasedJobStore();
      DefaultExecutionPolicy policy = new DefaultExecutionPolicy();
      return new JobManager(env, agg,store,policy);
   }
}
