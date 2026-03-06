package org.javastro.ivoa.uws;


import jakarta.enterprise.context.ApplicationScoped;
import org.javastro.ivoa.entities.vosi.capabilities.Capabilities;
import org.javastro.ivoacore.vosi.VOSIProvider;

/*
 * Created on 28/08/2025 by Paul Harrison (paul.harrison@manchester.ac.uk).
 */
@ApplicationScoped
public class BaseUWSServer implements VOSIProvider {
   @Override
   public Capabilities getCapabilities() {
      return new Capabilities();
   }

}
