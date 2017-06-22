package org.mccormax.newsletter;

 import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
 import org.springframework.web.bind.annotation.RequestBody;
 import org.springframework.web.bind.annotation.RequestMapping;
 import org.springframework.web.bind.annotation.RequestMethod;
 import org.springframework.web.bind.annotation.RestController;

 import javax.servlet.http.HttpServletResponse;
 import java.util.*;

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
      // there is a uniqueness constraint on category code which prevents
      // duplicates, however an exception is not thrown
      if (categoryRepository.findByCode(category.getCode()) != null) {
         response.setStatus(HttpServletResponse.SC_CONFLICT);
         return;
      }

      categoryRepository.save(category);
      response.setStatus(HttpServletResponse.SC_CREATED);
   }

   @RequestMapping(method = RequestMethod.GET, value = "/categories", produces = "application/json")
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

   @RequestMapping(method = RequestMethod.GET, value = "/books", produces = "application/json")
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

      // there is a uniqueness constraint on email which prevents
      // duplicates, however an exception is not thrown
      if (subscriberRepository.findByEmail(subscriber.getEmail()) != null) {
         response.setStatus(HttpServletResponse.SC_CONFLICT);
         return;
      }

      subscriberRepository.save(subscriber);

      response.setStatus(HttpServletResponse.SC_CREATED);
   }

   @RequestMapping(method = RequestMethod.GET, value = "/subscribers", produces = "application/json")
   public Iterable<Subscriber> getSubscribers(HttpServletResponse response) {
      response.setStatus(HttpServletResponse.SC_OK);
      return subscriberRepository.findAll();
   }

   @RequestMapping(method = RequestMethod.GET, value = "/newsletters", produces = "application/json")
   public List<RecipientNotifications> newsletters() {

      // get the meta data for all the books.  since this data will be repeated over and over
      // for different subscribers, it is retrieved just once and mapped.
      Collection<BookMeta> books = bookRepository.getBookMeta();

      // map each book meta by the book id
      Map<Long, BookCategories> bookLookup = new HashMap<>();
      for (BookMeta book : books) {
         bookLookup.put(book.getId(), new BookCategories(book.getBook(), book.getCategoryPaths()));
      }

      // get the books related to each subscriber
      // Collection<SubscriberBooks> subscriber = bookRepository.getBookMeta();
      Collection<SubscriberBooks> subscribers = subscriberRepository.getSubscriberBooks();

      // generate the result list from the 'static' book list and the subscriptions
      List<RecipientNotifications> notifications = new ArrayList<>();
      for (SubscriberBooks subscriber : subscribers) {
         System.out.println("sb: "+subscriber);

         RecipientNotifications notification = new RecipientNotifications(subscriber.getEmail());
         for (Long bookId : subscriber.getBooks()) {
            notification.addNotification(bookLookup.get(bookId));
         }
         notifications.add(notification);
      }

      return notifications;
   }

}