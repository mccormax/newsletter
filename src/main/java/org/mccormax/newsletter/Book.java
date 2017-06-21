package org.mccormax.newsletter;


import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Collection;
import java.util.List;

/**
 * @author Max McCormick
 */
@NodeEntity
public class Book {

   @GraphId Long id;

   private String title;

   @Relationship(type = "FILES_UNDER")
   Collection<Category> categories;

   public List<String> getCategoryCodes() {
      return categoryCodes;
   }

   public void setCategoryCodes(List<String> categoryCodes) {
      this.categoryCodes = categoryCodes;
   }

   private transient List<String> categoryCodes;

   public Book() { }

   public String getTitle() {
      return title;
   }

   public void filesUnder(List<Category> categories) {
      this.categories = categories;
   }

   public Collection<Category> getCategories() {
      return categories;
   }
}
