package org.mccormax.newsletter.domain;

import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

/**
 * @author Max McCormick
 */
@QueryResult
public class BookCategoriesResult {

   Long id;
   String book;
   List<List<String>> categoryPaths;

   public Long getId() {
      return id;
   }

   public List<List<String>> getCategoryPaths() {
      return categoryPaths;
   }

   public String getBook() {
      return book;
   }
}
