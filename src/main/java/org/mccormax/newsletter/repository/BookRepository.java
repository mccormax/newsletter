package org.mccormax.newsletter.repository;

import org.mccormax.newsletter.domain.Book;
import org.mccormax.newsletter.domain.BookCategoriesResult;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

import java.util.Collection;

/**
 * @author Max McCormick
 */
public interface BookRepository extends GraphRepository<Book> {

   Book findByTitle(String title);
   @Query("MATCH (b:Book), path = (n)<-[*]-(b) WHERE NOT (n)-[:HAS_PARENT]->() " +
           "RETURN id(b) as id, b.title as book, collect([x IN nodes(path) WHERE (x:Category) | x.title]) as categoryPaths")
   Collection<BookCategoriesResult> getBookMeta();

}
