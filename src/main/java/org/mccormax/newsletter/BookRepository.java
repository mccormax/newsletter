package org.mccormax.newsletter;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.annotation.QueryResult;
import org.springframework.data.neo4j.repository.GraphRepository;

import java.util.Collection;
import java.util.List;

/**
 * @author Max McCormick
 */
public interface BookRepository extends GraphRepository<Book> {

   Book findByTitle(String title);
   @Query("MATCH (b:Book), path = (n)<-[*]-(b) WHERE NOT (n)-[:HAS_PARENT]->() " +
           "RETURN id(b) as id, b.title as book, collect([x IN nodes(path) WHERE (x:Category) | x.title]) as categoryPaths")
   Collection<BookMeta> getBookMeta();

}
