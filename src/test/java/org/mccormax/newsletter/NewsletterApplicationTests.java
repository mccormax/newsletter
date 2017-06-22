package org.mccormax.newsletter;

import static com.sun.org.apache.xerces.internal.util.PropertyState.is;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class NewsletterApplicationTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@LocalServerPort
	int port;

	//use spec to reuse common request parameter
	private static RequestSpecification spec;

	@BeforeClass
	public static void initSpec(){
		spec = new RequestSpecBuilder()
				.setContentType(ContentType.JSON)
				.addFilter(new ResponseLoggingFilter())
				.addFilter(new RequestLoggingFilter())
				.build();
	}

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

	@Test
	public void contextLoads() {
	}

	@Ignore
	@Test
	public void createSubscriberAndCheckResponseStatus(){
		TestSubscriber subscriber = new TestSubscriber();
		subscriber.email = "user"+Math.random()+"@test.com";
		subscriber.categoryCodes = Arrays.asList("engineering", "functional_programming", "cats");
		createResource("/subscribers", subscriber);
	}

	@Ignore
	@Test
	public void createBookAndCheckResponseStatus(){
		TestBook book = new TestBook();
		book.title = "Coding For Dummies";
		book.categoryCodes = Arrays.asList("object_oriented_programming", "functional_programming");
		createResource("/books", book);
	}

	@Test
	public void getNewsletterAndCheckResponseStatus() {
		addCategories();
		addBooks();
		addSubscribers();
		getNewsletters();
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

		createResource("/books", TestBook.create("Don't Forget Aboout Beautiful Ada",
				Arrays.asList("software", "object_oriented_programming")));

		createResource("/books", TestBook.create("Rockin' it with Haskell",
				Arrays.asList("software", "functional_programming")));

		createResource("/books", TestBook.create("Great Cats of the Serengeti",
				Arrays.asList("cats")));

	}
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

	private <T> T getResource(String locationHeader, Class<T> responseClass) {
		return given()
				.spec(spec)
				.port(port)
				.when()
				.get(locationHeader)
				.then()
				.statusCode(200)
				.extract().as(responseClass);
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
//				.extract().asString();
	}

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
		public List<List<String>> categoryPaths;
	}
}
