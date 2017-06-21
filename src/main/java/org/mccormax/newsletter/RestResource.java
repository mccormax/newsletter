package org.mccormax.newsletter;

 import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
 import org.springframework.web.bind.annotation.RequestBody;
 import org.springframework.web.bind.annotation.RequestMapping;
 import org.springframework.web.bind.annotation.RequestMethod;
 import org.springframework.web.bind.annotation.RestController;

 import javax.servlet.http.HttpServletResponse;
 import java.util.List;

/**
 * REST API resource for newsletter service
 *
 * @author Max McCormick
 */
@EnableAutoConfiguration
@RestController
public class RestResource {

   private CategoryRepository categoryRepository;
   private SubscriberRepository subscriberRepository;
   private BookRepository bookRepository;

   public RestResource(CategoryRepository categoryRepository,
                       SubscriberRepository subscriberRepository,
                       BookRepository bookRepository) {
      this.categoryRepository = categoryRepository;
      this.subscriberRepository = subscriberRepository;
      this.bookRepository = bookRepository;
   }

   @RequestMapping(method = RequestMethod.POST, value = "/categories")
   public void addCategory(HttpServletResponse response, @RequestBody Category category) {
      if (category.getSuperCategoryCode() != null) {
         Category parentCategory = categoryRepository.findByCode(category.getSuperCategoryCode());
         // FIXME:  what if null
         category.hasParent(parentCategory);
      }
      categoryRepository.save(category);
      response.setStatus(HttpServletResponse.SC_CREATED);
   }

   @RequestMapping(method = RequestMethod.GET, value = "/categories")
   public Iterable<Category> getCategories(HttpServletResponse response) {
      response.setStatus(HttpServletResponse.SC_OK);
      return categoryRepository.findAll();
   }

   @RequestMapping(method = RequestMethod.POST, value = "/books")
   public void addBook(HttpServletResponse response, @RequestBody Book book) {
      List<Category> categories = categoryRepository.findByCodeIn(book.getCategoryCodes());
      // fixme:  no check here
      book.filesUnder(categories);
      bookRepository.save(book);

      response.setStatus(HttpServletResponse.SC_CREATED);
   }

   @RequestMapping(method = RequestMethod.GET, value = "/books")
   public Iterable<BookMeta> getBooks(HttpServletResponse response) {
      response.setStatus(HttpServletResponse.SC_OK);
      Iterable<BookMeta> result = bookRepository.getBookMeta();
      return result;
   }

   @RequestMapping(method = RequestMethod.POST, value = "/subscribers")
   public void addSubscriber(HttpServletResponse response, @RequestBody Subscriber subscriber) {
      List<Category> categories = categoryRepository.findByCodeIn(subscriber.getCategoryCodes());
      // fixme:  no check here
      subscriber.subscribesTo(categories);
      subscriberRepository.save(subscriber);

      response.setStatus(HttpServletResponse.SC_CREATED);
   }

   @RequestMapping(method = RequestMethod.GET, value = "/subscribers")
   public Iterable<Subscriber> getSubscribers(HttpServletResponse response) {
      response.setStatus(HttpServletResponse.SC_OK);
      return subscriberRepository.findAll();
   }

   @RequestMapping(method = RequestMethod.GET, value = "/newsletters")
   public String newsletters() {
      return "HERE YA GO!";
   }

}