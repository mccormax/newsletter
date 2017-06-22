package org.mccormax.newsletter.domain;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.Index;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;


/**
 * @author Max McCormick
 */
@NodeEntity
public class Category {

   @Relationship(type = "HAS_PARENT", direction = Relationship.OUTGOING)
   public Category parentCategory;
   @GraphId
   Long id;
   @Index(unique=true, primary=true) String code;
   String title;
   private transient String superCategoryCode;

   public Category() { }

   public String getCode() {
      return code;
   }

   public String getTitle() {
      return title;
   }

   public void hasParent(Category parentCategory) {
      this.parentCategory = parentCategory;
   }

   public String getSuperCategoryCode() {
      return superCategoryCode;
   }

   public void setSuperCategoryCode(String superCategoryCode) {
      this.superCategoryCode = superCategoryCode;
   }

}
