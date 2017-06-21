package org.mccormax.newsletter;

import org.springframework.data.neo4j.repository.GraphRepository;

import java.util.List;

/**
 * @author Max McCormick
 */
   public interface SubscriberRepository extends GraphRepository<Subscriber> {

      Subscriber findByEmail(String email);

   }
