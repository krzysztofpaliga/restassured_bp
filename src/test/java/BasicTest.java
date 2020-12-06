import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.filter.log.ErrorLoggingFilter;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import model.Dataset;
import model.Feature;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

import static io.restassured.RestAssured.given;

public class BasicTest {

    private static String MAPBOX_API_TOKEN = "sk.eyJ1Ijoic3RlYW1ub2lkIiwiYSI6ImNraWJuYndicjExZmwydGxjOTc4Z3J1eW4ifQ.VdJl9atmEqm7DIXRHfRF5A";

    private static Dataset myDataset;

    private static ObjectMapper jackson = new ObjectMapper();

    @BeforeClass
    public static void beforeClass() throws Exception {
        setBaseHost();
        createDataset();
    }

    @AfterClass
    public static void afterClass() {
        deleteDataset();
    }

    @Test
    public void getRequestReturns200() {
        given().when().get().then().statusCode(200);
    }

    @Test
    public void putFeatureRequestReturns200() {
        Feature feature = Feature.generateFeature();
        String json = toJson(feature);
        given()
                .header("Content-Type", "application/json; charset=utf-8")
                .queryParam("access_token", MAPBOX_API_TOKEN)
                .body(json)
                .when()
                .put("/datasets/v1/steamnoid/" + myDataset.id + "/features/" + feature.id)
                .then()
                .statusCode(200);
    }

    @Test
    public void putInvalidFeatureRequestReturns422() {
        Feature feature = Feature.generateFeature();
        feature.type = "invalid";
        String json = toJson(feature);
        given()
                .header("Content-Type", "application/json; charset=utf-8")
                .queryParam("access_token", MAPBOX_API_TOKEN)
                .body(json)
                .when()
                .put("/datasets/v1/steamnoid/" + myDataset.id + "/features/" + feature.id)
                .then()
                .statusCode(422);
    }

    @Test
    public void putIncorrectFormatFeatureRequestReturns400() {
        Feature feature = Feature.generateFeature();
        String json = toJson(feature);
        json = json.substring(1);
        given()
                .header("Content-Type", "application/json; charset=utf-8")
                .queryParam("access_token", MAPBOX_API_TOKEN)
                .body(json)
                .when()
                .put("/datasets/v1/steamnoid/" + myDataset.id + "/features/" + feature.id)
                .then()
                .statusCode(400);
    }

    @Test
    public void deleteFeatureReturns204() {
        Feature feature = Feature.generateFeature();
        String json = toJson(feature);
        given()
                .header("Content-Type", "application/json; charset=utf-8")
                .queryParam("access_token", MAPBOX_API_TOKEN)
                .body(json)
                .when()
                .put("/datasets/v1/steamnoid/" + myDataset.id + "/features/" + feature.id)
                .then()
                .statusCode(200);
        given()
                .queryParam("access_token", MAPBOX_API_TOKEN)
                .when()
                .delete("/datasets/v1/steamnoid/"+myDataset.id+"/features/" + feature.id)
                .then()
                .statusCode(204);
    }

    @Test
    public void updatingFeaturePerformsUpdateAndReturns200() {
        Feature feature = Feature.generateFeature();
        String json = toJson(feature);
        given()
                .header("Content-Type", "application/json; charset=utf-8")
                .queryParam("access_token", MAPBOX_API_TOKEN)
                .body(json)
                .when()
                .put("/datasets/v1/steamnoid/" + myDataset.id + "/features/" + feature.id)
                .then()
                .statusCode(200);
        feature.properties.prop0 = "abcdefghijk";
        json = toJson(feature);
        given()
                .header("Content-Type", "application/json; charset=utf-8")
                .queryParam("access_token", MAPBOX_API_TOKEN)
                .body(json)
                .when()
                .put("/datasets/v1/steamnoid/" + myDataset.id + "/features/" + feature.id)
                .then()
                .body("properties.prop0", equalTo("abcdefghijk"))
                .statusCode(200);
    }

    @Test
    public void deletingNonExistentFeatureReturns404() {
        given()
                .queryParam("access_token", MAPBOX_API_TOKEN)
                .when()
                .delete("/datasets/v1/steamnoid/"+myDataset.id+"/features/nonexistent")
                .then()
                .statusCode(404);
    }

    @Test
    public void invalidTokenRequestReturns401() {
        given()
                .queryParam("access_token", "invalid_token")
                .when()
                .post("/datasets/v1/steamnoid")
                .then()
                .statusCode(401);
    }

    @Test
    public void invalidUserRequestResponseContainsNoSuchUserAndReturns404(){
        given()
                .queryParam("access_token", MAPBOX_API_TOKEN)
                .when()
                .post("/datasets/v1/steamnoidsteamnoid")
                .then()
                .statusCode(404)
                .body("message", equalTo("No such user"));
    }

    private static void setBaseHost() {
        String baseHost = System.getProperty("server.host");
        if (baseHost == null) {
            baseHost = "https://api.mapbox.com";
        }
        RestAssured.baseURI = baseHost;
        RestAssured.filters(new ResponseLoggingFilter(), new RequestLoggingFilter(), new ErrorLoggingFilter());
    }

    private static void createDataset() throws Exception {
        myDataset = given()
                .queryParam("access_token", MAPBOX_API_TOKEN)
                .when()
                .post("/datasets/v1/steamnoid")
                .then()
                .statusCode(200)
                .extract()
                .as(Dataset.class);
        assert (myDataset.id.length() != 0);
    }

    private static void deleteDataset() {
        given()
                .queryParam("access_token", MAPBOX_API_TOKEN)
                .when()
                .delete("/datasets/v1/steamnoid/" + myDataset.id)
                .then()
                .statusCode(204);
    }

    private static String toJson(Object object) {
        String json = "";
        try {
            json = jackson.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }
}

