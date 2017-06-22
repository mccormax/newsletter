package org.mccormax.newsletter.repository;

import org.mccormax.newsletter.domain.Subscriber;
import org.mccormax.newsletter.domain.SubscriberBooksResult;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

import java.util.Collection;

/**
 * @author Max McCormick
 */
public interface SubscriberRepository extends GraphRepository<Subscriber> {

   Subscriber findByEmail(String email);

   @Query("MATCH (s:Subscriber), (b:Book), (s)-[:SUBSCRIBES_TO]->(c:Category)<-[*]-(b) return s.email as email, collect(distinct id(b)) as books")
   Collection<SubscriberBooksResult> getSubscriberBooks();

}
