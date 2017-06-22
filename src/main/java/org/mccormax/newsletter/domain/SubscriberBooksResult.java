package org.mccormax.newsletter.domain;

import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

/**
 * @author Max McCormick
 */
@QueryResult
public class SubscriberBooksResult {

   private String email;

   private List<Long> books;

   public String getEmail() {
      return email;
   }

   public List<Long> getBooks() {
      return books;
   }

   @Override
   public String toString() {
      return "SubscriberBooks{" +
              "email='" + email + '\'' +
              ", books=" + books +
              '}';
   }
}
