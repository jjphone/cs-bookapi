package cs.step_definitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;

import cs.utils.ParamHelper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.not;

public class BooksApiSteps {

    private Response response;
    

    @Given("access to granted to the API")
    public void access_to_granted_to_the_api() {
        RestAssured.baseURI = "http://simple-books-api.glitch.me";
    }

    @When("^GET request is sent to '(.+)'$")
    public void a_get_request_is_sent_to(String url) {
        response = RestAssured.get(url);
    }

    @Then("the response status code should be {int}")
    public void the_response_status_code_should_be(int expectedStatus) {
        assertThat(response.getStatusCode(), equalTo(expectedStatus));
    }

    @Then ("the response header should match with {string}")
    public void the_response_header_should_match_with(String expectedAttribs) {


        HashMap<String, String>[] expectings = ParamHelper.parseParams(expectedAttribs);
        for( int i = 0; i < expectings.length; i++) {
            HashMap<String, String> item = expectings[i];
            boolean result = cs.utils.ResponseAttribute.compareAttributes(item, response);
            if (!result) {
                String reson = item.get("reason");
                String expected = "expected: " + item.get("value");
                // String actual = 
                System.out.println("Response header attribute check failed for: " + item);
            }
            Assert.assertTrue(item.get("reason"), result);
        }
        



    }

    @Then("the response time should be less than {int} ms")
    public void the_response_time_should_be_less_than_ms(int maxMillis) {
        long responseTime = response.getTimeIn(TimeUnit.MILLISECONDS);
        assertThat(responseTime, lessThan((long) maxMillis));
    }

    @Then("the response body should not be empty")
    public void the_response_body_should_not_be_empty() {
        List<Map<String, Object>> books = response.jsonPath().getList("$");
        assertThat(books, is(not(empty())));
    }

    @Then("each book in the response should have an {string} and a {string}")
    public void each_book_in_the_response_should_have(String firstField, String secondField) {
        JsonPath jsonPath = response.jsonPath();
        List<Map<String, Object>> books = jsonPath.getList("$");
        for (Map<String, Object> book : books) {
            assertThat(book, hasKey(firstField));
            assertThat(book, hasKey(secondField));
        }
    }
}
