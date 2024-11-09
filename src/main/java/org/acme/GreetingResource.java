package org.acme;

import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

import java.util.List;

@Path("/hello")
public class GreetingResource {

    @Inject
    EntityManager entityManager;

    @GET
    public List<Person> hello() {
        var all = Person.<Person>listAll();

        Log.info("### " + all.get(0).getJson());

        var el = new MyJson.Element();
        el.setCode("inner");


        // Native SQL query
        String sql = "SELECT * FROM person p, jsonb_array_elements(p.json->'elements') as elem " +
            "WHERE elem->>'code' = :fieldValue";
        //        String sql = "SELECT * FROM person p WHERE p.json->'elements'->>'code' = :fieldValue";
        //        String sql = "SELECT * FROM person p WHERE p.json->'element'->>'code' = :fieldValue";
        //        String sql = "SELECT * FROM person p WHERE p.json->>'value' = 'another'";

        var test = entityManager.createNativeQuery(sql, Person.class)
                                .setParameter("fieldValue", el.getCode())
                                .getResultList();
        Log.info("### test: " + test);

        return Person.listAll();
    }
}
