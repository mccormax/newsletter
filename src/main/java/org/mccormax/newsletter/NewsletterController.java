package org.mccormax.newsletter;

import org.mccormax.newsletter.service.NewsletterService;
import org.springframework.beans.factory.annotation.Autowired;
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
 * TODO:  proper error handling and messages (currently just status codes).
 *
 * @author Max McCormick
 */
@EnableAutoConfiguration
@RestController
public class NewsletterController {

   @Autowired
   private NewsletterService newsletterService;

   @Autowired
   private CategoryRepository categoryRepository;

   @Autowired
   private SubscriberRepository subscriberRepository;

   @Autowired
   private BookRepository bookRepository;

   @RequestMapping(method = RequestMethod.POST, value = "/categories")
   public void addCategory(HttpServletResponse response, @RequestBody Category category) {

      if (category.getSuperCategoryCode() != null) {
         Category parentCategory = categoryRepository.findByCode(category.getSuperCategoryCode());
         if (parentCategory == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
         }
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

   @RequestMapping(method = RequestMethod.POST, value = "/books")
   public void addBook(HttpServletResponse response, @RequestBody Book book) {
      List<Category> categories = categoryRepository.findByCodeIn(book.getCategoryCodes());

      // ensure that the specified category codes exist
      for (String code : book.getCategoryCodes()) {
         if (categoryRepository.findByCode(code) == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
         }
      }
      book.filesUnder(categories);

      bookRepository.save(book);

      response.setStatus(HttpServletResponse.SC_CREATED);
   }

   @RequestMapping(method = RequestMethod.POST, value = "/subscribers")
   public void addSubscriber(HttpServletResponse response, @RequestBody Subscriber subscriber) {
      List<Category> categories = categoryRepository.findByCodeIn(subscriber.getCategoryCodes());

      // ensure that the specified category codes exist
      for (String code : subscriber.getCategoryCodes()) {
         if (categoryRepository.findByCode(code) == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
         }
      }
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

   @RequestMapping(method = RequestMethod.GET, value = "/newsletters", produces = "application/json")
   public List<RecipientNotifications> newsletters() {

      return newsletterService.buildNewsletters();
   }

}