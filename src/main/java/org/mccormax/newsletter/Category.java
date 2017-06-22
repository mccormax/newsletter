package org.mccormax.newsletter;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.Index;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.springframework.data.neo4j.annotation.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@NodeEntity
public class Category {

   @GraphId
   Long id;

   @Index(unique=true, primary=true) String code;

   String title;

   @Relationship(type = "HAS_PARENT", direction = Relationship.OUTGOING)
   public Category parentCategory;

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

   public void setSuperCategoryCode(String superCategoryCode) {
      this.superCategoryCode = superCategoryCode;
   }
   public String getSuperCategoryCode() {
      return superCategoryCode;
   }

}
