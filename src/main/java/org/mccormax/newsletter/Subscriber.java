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
public class Subscriber {

      @GraphId Long id;

      private String email;

      @Relationship(type = "SUBSCRIBES_TO")
      Collection<Category> categories;

      public List<String> getCategoryCodes() {
            return categoryCodes;
      }

      public void setCategoryCodes(List<String> categoryCodes) {
            this.categoryCodes = categoryCodes;
      }

      private transient List<String> categoryCodes;

      public Subscriber() { }

      public String getEmail() {
         return email;
      }

      public void subscribesTo(List<Category> categories) {
            this.categories = categories;
      }

      public Collection<Category> getCategories() {
         return categories;
      }
}
