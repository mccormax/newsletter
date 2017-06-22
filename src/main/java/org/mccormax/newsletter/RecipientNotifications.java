package org.mccormax.newsletter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Max McCormick
 */
public class RecipientNotifications {

   String recipient;
   List<BookCategories> notifications;

   public RecipientNotifications(String recipient) {
      this.recipient = recipient;
      this.notifications =  new ArrayList<BookCategories>();
   }

   public String getRecipient() {
      return recipient;
   }

   public void addNotification(BookCategories bookCategories) {
      notifications.add(bookCategories);
   }

   public List<BookCategories> getNotifications() {
      return notifications;
   }
}
