package org.mccormax.newsletter;

import org.mccormax.newsletter.domain.Book;
import org.mccormax.newsletter.domain.Category;
import org.mccormax.newsletter.domain.RecipientNotifications;
import org.mccormax.newsletter.domain.Subscriber;
import org.mccormax.newsletter.repository.BookRepository;
import org.mccormax.newsletter.repository.CategoryRepository;
import org.mccormax.newsletter.repository.SubscriberRepository;
import org.mccormax.newsletter.service.NewsletterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static org.springframework.util.StringUtils.isEmpty;

/**
 * REST API resource for newsletter service
 * <p>
 * TODO:  proper validation and messages (currently just returns status codes).
 * TODO:  make it impossible to file a book under a category and an ancestor.
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

      if (isEmpty(category.getCode()) || isEmpty(category.getTitle())) {
         response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
         return;
      }
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

      if (isEmpty(book.getTitle()) || isEmpty(book.getCategoryCodes())) {
         response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
         return;
      }

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

      if (isEmpty(subscriber.getEmail()) || isEmpty(subscriber.getCategoryCodes())) {
         response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
         return;
      }

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