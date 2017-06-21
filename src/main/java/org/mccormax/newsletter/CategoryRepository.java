package org.mccormax.newsletter;

import org.springframework.data.neo4j.repository.GraphRepository;

import java.util.List;

/**
 * @author Max McCormick
 */
public interface CategoryRepository extends GraphRepository<Category> {

      Category findByTitle(String title);
      Category findByCode(String code);
      List<Category> findByCodeIn(List<String> code);

}
