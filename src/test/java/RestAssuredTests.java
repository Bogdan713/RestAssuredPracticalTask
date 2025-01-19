import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class RestAssuredTests {

    private static final String BASE_URL = "https://jsonplaceholder.typicode.com/users";

    @Test
    public void testStatusCodeIs200() {
        Response response = RestAssured.get(BASE_URL);
        assertThat("Status code is not 200", response.getStatusCode(), is(200));
    }

    @Test
    public void testContentTypeHeaderExistsAndIsValid() {
        Response response = RestAssured.get(BASE_URL);
        String contentType = response.getHeader("Content-Type");
        assertThat("Content-Type header is missing", contentType, notNullValue());
        assertThat("Content-Type value is incorrect", contentType, is("application/json; charset=utf-8"));
    }

    @Test
    public void testResponseBodyContainsArrayOf10Users() {
        Response response = RestAssured.get(BASE_URL);
        assertThat("Response body does not contain an array of 10 users", response.jsonPath().getList(""), hasSize(10));
    }

    @Test
    public void testCreateUser() {
        Response response = RestAssured.given()
                                       .header("Content-Type", "application/json")
                                       .body("{\"name\": \"New User\", \"email\": \"newuser@example.com\"}")
                .post(BASE_URL);

        assertThat("Failed to create user", response.getStatusCode(), is(201));
        assertThat("Response does not contain the new user", response.jsonPath().getString("name"), is("New User"));
    }

    @Test
    public void testReadUser() {
        Response response = RestAssured.get(BASE_URL + "/1");
        assertThat("Failed to fetch user", response.getStatusCode(), is(200));
        assertThat("User ID does not match", response.jsonPath().getInt("id"), is(1));
    }

    @Test
    public void testUpdateUser() {
        Response response = RestAssured.given()
                                       .header("Content-Type", "application/json")
                                       .body("{\"name\": \"Updated User\"}")
                                       .put(BASE_URL + "/1");

        assertThat("Failed to update user", response.getStatusCode(), is(200));
        assertThat("User name was not updated", response.jsonPath().getString("name"), is("Updated User"));
    }

    @Test
    public void testDeleteUser() {
        Response response = RestAssured.delete(BASE_URL + "/1");
        assertThat("Failed to delete user", response.getStatusCode(), is(200));
    }
}
