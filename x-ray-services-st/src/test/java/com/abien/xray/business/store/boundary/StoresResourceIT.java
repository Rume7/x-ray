/*
 */
package com.abien.xray.business.store.boundary;

import com.abien.xray.business.RESTSupport;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Assert;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author adam-bien.com
 */
public class StoresResourceIT {

    public static final String STATISTICS_STORE = "STATISTICS";
    private static final String ROOT_TARGET = "http://localhost:8080/x-ray/resources/stores/";
    private WebTarget tut;

    @Before
    public void initClient() {
        Client client = ClientBuilder.newClient();
        this.tut = client.target(ROOT_TARGET);
    }

    @Test
    public void allStores() {
        JsonArray result = RESTSupport.convertToArrayFrom(this.tut.request(MediaType.TEXT_PLAIN).get(String.class));
        int nbrOfStores = result.size();
        Assert.assertThat(nbrOfStores, is(3));
    }

    @Test
    public void fetchUnknownStore() {
        Response response = this.tut.path("SHOULD-NOT-EXIST").
                request(MediaType.TEXT_PLAIN).
                get(Response.class);
        int status = response.getStatus();
        assertThat(status, is(204));
    }

    @Test
    public void fetchContentsFromAllStores() {
        JsonArray result = RESTSupport.convertToArrayFrom(this.tut.request(MediaType.TEXT_PLAIN).get(String.class));
        for (int i = 0; i < result.size(); i++) {
            String storeName = result.getJsonString(i).getString();
            JsonObject store = storeContents(storeName);
            Assert.assertNotNull(store);
        }
    }

    @Test
    public void put() {
        final String key = "42";
        final long expected = 42;
        JsonObjectBuilder builder = Json.createObjectBuilder().add(key, expected);
        Response response = this.tut.path(STATISTICS_STORE).request(MediaType.TEXT_PLAIN).put(Entity.json(builder.build()));
        assertThat(response.getStatus(), is(200));
        JsonObject cache = storeContents(STATISTICS_STORE);
        long actual = cache.getJsonNumber(key).longValue();
        assertThat(actual, is(expected));
    }

    JsonObject storeContents(String storeName) {
        String rawContent = this.tut.path(storeName).
                request(MediaType.TEXT_PLAIN).
                get(String.class);
        return RESTSupport.convertToObjectFrom(rawContent);
    }
}