package org.mccormax.newsletter;

import java.util.List;

/**
 * @author Max McCormick
 */
public class BookCategories {

   String book;
   List<List<String>> categoryPaths;

   public BookCategories(String book, List<List<String>> categoryPaths) {
      this.book = book;
      this.categoryPaths = categoryPaths;
   }
   public List<List<String>> getCategoryPaths() {
      return categoryPaths;
   }

   public String getBook() {
      return book;
   }

}
