package org.javastro.ivoa.uws;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;
import org.javastro.ivoacore.uws.JobFactoryAggregator;
import org.javastro.ivoacore.uws.JobManager;
import org.javastro.ivoacore.uws.SimpleLambdaJob;
import org.javastro.ivoacore.uws.environment.DefaultEnvironmentFactory;
import org.javastro.ivoacore.uws.environment.DefaultExecutionEnvironment;
import org.javastro.ivoacore.uws.environment.DefaultExecutionPolicy;
import org.javastro.ivoacore.uws.environment.EnvironmentFactory;
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
   @Singleton
   JobManager uws() {
      File tmpdir = null;
      try {
         tmpdir = Files.createTempDirectory("uwsserver").toFile();
      } catch (IOException e) {
         throw new RuntimeException("temporary directory not available",e);
      }
      EnvironmentFactory env = new DefaultEnvironmentFactory(tmpdir);
      JobFactoryAggregator agg = new JobFactoryAggregator();
      agg.addFactory(new SimpleLambdaJob.JobFactory(s-> {
         try {
            Thread.sleep(2300);
         } catch (InterruptedException e) {
            throw new RuntimeException(e); //TODO review exception handling
         }
         return "hello "+s;}, env));


      MemoryBasedJobStore store = new MemoryBasedJobStore();
      DefaultExecutionPolicy policy = new DefaultExecutionPolicy();
      return new JobManager(agg,store,policy);
   }
}
