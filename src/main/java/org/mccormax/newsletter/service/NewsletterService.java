package org.mccormax.newsletter.service;

import org.mccormax.newsletter.domain.BookCategories;
import org.mccormax.newsletter.domain.BookCategoriesResult;
import org.mccormax.newsletter.domain.RecipientNotifications;
import org.mccormax.newsletter.domain.SubscriberBooksResult;
import org.mccormax.newsletter.repository.BookRepository;
import org.mccormax.newsletter.repository.SubscriberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Abstraction layer between controller and repository which handles the
 * newsletter generation.
 *
 * @author Max McCormick
 */
@Component
public class NewsletterService {

   @Autowired
   private SubscriberRepository subscriberRepository;

   @Autowired
   private BookRepository bookRepository;

   public List<RecipientNotifications> buildNewsletters() {

      // get the meta data for all the books.  since this data will be repeated over and over
      // for different subscribers, it is retrieved just once and mapped.
      Collection<BookCategoriesResult> books = bookRepository.getBookMeta();

      // map each book meta by the book id
      Map<Long, BookCategories> bookLookup = new HashMap<>();
      for (BookCategoriesResult book : books) {
         bookLookup.put(book.getId(), new BookCategories(book.getBook(), book.getCategoryPaths()));
      }

      // get the books related to each subscriber
      Collection<SubscriberBooksResult> subscribers = subscriberRepository.getSubscriberBooks();

      // generate the result list from the 'static' book list and the subscriptions
      List<RecipientNotifications> notifications = new ArrayList<>();
      for (SubscriberBooksResult subscriber : subscribers) {
         RecipientNotifications notification = new RecipientNotifications(subscriber.getEmail());
         for (Long bookId : subscriber.getBooks()) {
            notification.addNotification(bookLookup.get(bookId));
         }
         notifications.add(notification);
      }

      return notifications;
   }

}
