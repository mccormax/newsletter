package org.mccormax.newsletter;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Newsletter application test
 *
 * TODO:  unit testing (strictly speaking this is integation testing).
 * TODO:  more testing of "edge cases", eg. bad input data
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class NewsletterApplicationTests {

   @Autowired
   private TestRestTemplate restTemplate;

   @LocalServerPort
   int port;

   private static RequestSpecification spec;

   @BeforeClass
   public static void initSpec() {
      spec = new RequestSpecBuilder()
              .setContentType(ContentType.JSON)
              .addFilter(new ResponseLoggingFilter())
              .addFilter(new RequestLoggingFilter())
              .build();
   }

   /**
    * Create a graph of categories and related books and subscribers.
    * Then read back the newsletter and compare it with expected data.
    */
   @Test
   public void createCategoriesBooksAndSubscribersAndVerifyNewsletter() {

      // set up data
      addCategories();
      addBooks();
      addSubscribers();

      // get the newsletter
      TestNewsletter[] newsletters = getNewsletters();

      // should have one result per subscriber
      assertThat(newsletters.length).isEqualTo(3);

      // check that data exists for each of the three subscribers:
      TestNewsletter bob = null, mary = null, fred = null;
      boolean found = true;
      for (TestNewsletter newsletter : newsletters) {
         switch (newsletter.recipient) {
            case "bob@duck.com":
               bob = newsletter;
               break;
            case "fred@footle.com":
               fred = newsletter;
               break;
            case "mary@bixby.org":
               mary = newsletter;
               break;
            default:
               found = false;
         }
      }
      assertThat(found).isTrue();

      // generate notifications to look for
      TestNotification scala = TestNotification.create("Functional Programming and Scala",
              new String[]{"Science", "Engineering", "Software", "Functional Programming"},
              new String[]{"Science", "Engineering", "Software", "Object Oriented Programming"});

      TestNotification haskell = TestNotification.create("Rockin' it with Haskell",
              new String[]{"Science", "Engineering", "Software", "Functional Programming"});

      TestNotification javascript = TestNotification.create("Hacking JavaScript in the 21st Century",
              new String[]{"Science", "Engineering", "Software"});

      TestNotification ada = TestNotification.create("Don't Forget About Beautiful Ada",
              new String[]{"Science", "Engineering", "Software", "Object Oriented Programming"});

      TestNotification cats = TestNotification.create("Great Cats of the Serengeti",
              new String[]{"Nature", "Animals", "Cats"});

      // check bob

      assertThat(bob.notifications.size()).isEqualTo(2);
      assertThat(bob.notifications).contains(scala);
      assertThat(bob.notifications).contains(haskell);

      // check mary

      assertThat(mary.notifications.size()).isEqualTo(1);
      assertThat(mary.notifications).contains(cats);

      // check fred

      assertThat(fred.notifications.size()).isEqualTo(4);
      assertThat(fred.notifications).contains(scala);
      assertThat(fred.notifications).contains(haskell);
      assertThat(fred.notifications).contains(javascript);
      assertThat(fred.notifications).contains(ada);

   }

   /*
    *  Test data generation
    */

   public void addCategories() {
      createResource("/categories",
              TestCategory.create("science", "Science", null));
      createResource("/categories",
              TestCategory.create("engineering", "Engineering", "science"));
      createResource("/categories",
              TestCategory.create("software", "Software", "engineering"));
      createResource("/categories",
              TestCategory.create("functional_programming", "Functional Programming", "software"));
      createResource("/categories",
              TestCategory.create("object_oriented_programming", "Object Oriented Programming", "software"));

      createResource("/categories",
              TestCategory.create("nature", "Nature", null));
      createResource("/categories",
              TestCategory.create("animals", "Animals", "nature"));
      createResource("/categories",
              TestCategory.create("cats", "Cats", "animals"));
   }

   private void addSubscribers() {
      createResource("/subscribers",
              TestSubscriber.create("fred@footle.com",
                      Arrays.asList("engineering")));
      createResource("/subscribers",
              TestSubscriber.create("bob@duck.com",
                      Arrays.asList("functional_programming")));
      createResource("/subscribers",
              TestSubscriber.create("mary@bixby.org",
                      Arrays.asList("animals")));

   }

   private void addBooks() {
      createResource("/books",
              TestBook.create("Functional Programming and Scala",
                      Arrays.asList("functional_programming", "object_oriented_programming")));

      createResource("/books", TestBook.create("Hacking JavaScript in the 21st Century",
              Arrays.asList("software")));

      createResource("/books", TestBook.create("Don't Forget About Beautiful Ada",
              Arrays.asList("software", "object_oriented_programming")));

      createResource("/books", TestBook.create("Rockin' it with Haskell",
              Arrays.asList("functional_programming")));

      createResource("/books", TestBook.create("Great Cats of the Serengeti",
              Arrays.asList("cats")));

   }

   /*
    *  Resource access methods
    */

   private String createResource(String path, Object bodyPayload) {
      return given()
              .spec(spec)
              .port(port)
              .body(bodyPayload)
              .when()
              .post(path)
              .then()
              .statusCode(201)
              .extract().header("location");
   }

   private TestNewsletter[] getNewsletters() {
      return given()
              .spec(spec)
              .port(port)
              .when()
              .get("/newsletters")
              .then()
              .statusCode(200)
              .extract().as(TestNewsletter[].class);
   }

   /*
    * Test data beans
    *
    * Note:  This ensures there is no dependence on application classes
    */

   private static class TestSubscriber {

      public String email;
      public List<String> categoryCodes;

      private static TestSubscriber create(String email, List<String> categories) {
         TestSubscriber subscriber = new TestSubscriber();
         subscriber.email = email;
         subscriber.categoryCodes = categories;
         return subscriber;
      }

   }

   private static class TestBook {

      public String title;
      public List<String> categoryCodes;

      private static TestBook create(String title, List<String> categories) {
         TestBook book = new TestBook();
         book.title = title;
         book.categoryCodes = categories;
         return book;
      }

   }

   private static class TestCategory {

      public String code;
      public String title;
      public String superCategoryCode;

      private static TestCategory create(String code, String title, String superCategoryCode) {
         TestCategory category = new TestCategory();
         category.code = code;
         category.superCategoryCode = superCategoryCode;
         category.title = title;
         return category;
      }
   }

   private static class TestNewsletter {
      public String recipient;
      public List<TestNotification> notifications;
   }

   private static class TestNotification {
      public String book;
      public Collection<List<String>> categoryPaths;

      private static TestNotification create(String book, String[]... categoryPaths) {
         TestNotification notification = new TestNotification();
         notification.book = book;
         notification.categoryPaths = new HashSet<>();
         for (String[] categoryPath : categoryPaths) {
            notification.categoryPaths.add(Arrays.asList(categoryPath));
         }
         return notification;
      }

      @Override
      public boolean equals(Object o) {
         System.out.println("Comparing " + this + " to " + o + ": " + this.hashCode() + "," + o.hashCode());
         if (this == o) return true;
         if (o == null || getClass() != o.getClass()) return false;

         TestNotification that = (TestNotification) o;

         if (!book.equals(that.book)) {
            System.out.println("Wrong book");
            return false;
         }

         return categoryPaths.containsAll(categoryPaths);
      }

      @Override
      public int hashCode() {
         int result = book.hashCode();
         result = 31 * result + categoryPaths.hashCode();
         return result;
      }

      @Override
      public String toString() {
         return "TestNotification{" +
                 "book='" + book + '\'' +
                 ", categoryPaths=" + categoryPaths +
                 '}';
      }
   }

}
