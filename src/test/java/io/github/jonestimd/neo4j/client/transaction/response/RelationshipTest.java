package io.github.jonestimd.neo4j.client.transaction.response;

import java.io.ByteArrayInputStream;
import java.util.Collections;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.junit.Test;

import static org.fest.assertions.Assertions.*;

public class RelationshipTest {
    private final JsonFactory jsonFactory = new JsonFactory();

    @Test
    public void defaultConstructor() throws Exception {
        Relationship relationship = new Relationship();

        assertThat(relationship.getId()).isNull();
        assertThat(relationship.getType()).isNull();
        assertThat(relationship.getStartId()).isNull();
        assertThat(relationship.getEndId()).isNull();
        assertThat(relationship.getProperties()).isEmpty();
    }

    @Test(expected = ParseResponseException.class)
    public void invalidStartPosition() throws Exception {
        JsonParser parser = jsonFactory.createParser(new ByteArrayInputStream("{\"id\":\"1\",\"startNode\":\"2\",\"endNode\":\"3\",\"type\":\"t1\"}".getBytes()));

        Relationship.read(parser);
    }

    @Test
    public void readWithoutProperties() throws Exception {
        JsonParser parser = jsonFactory.createParser(new ByteArrayInputStream("{\"ignored\":{},\"id\":\"1\",\"startNode\":\"2\",\"endNode\":\"3\",\"type\":\"t1\"}".getBytes()));
        parser.nextToken();

        Relationship relationship = Relationship.read(parser);

        assertThat(parser.getCurrentToken()).isEqualTo(JsonToken.END_OBJECT);
        assertThat(parser.nextToken()).isNull();
        assertThat(relationship.getId()).isEqualTo(1L);
        assertThat(relationship.getStartId()).isEqualTo(2L);
        assertThat(relationship.getEndId()).isEqualTo(3L);
        assertThat(relationship.getType()).isEqualTo("t1");
        assertThat(relationship.getProperties()).isEmpty();
    }

    @Test
    public void readWithEmptyProperties() throws Exception {
        JsonParser parser = jsonFactory.createParser(new ByteArrayInputStream("{\"id\":\"1\",\"type\":\"t1\",\"startNode\":\"2\",\"endNode\":\"3\",\"properties\":{}}".getBytes()));
        parser.nextToken();

        Relationship relationship = Relationship.read(parser);

        assertThat(parser.getCurrentToken()).isEqualTo(JsonToken.END_OBJECT);
        assertThat(parser.nextToken()).isNull();
        assertThat(relationship.getId()).isEqualTo(1L);
        assertThat(relationship.getType()).isEqualTo("t1");
        assertThat(relationship.getStartId()).isEqualTo(2L);
        assertThat(relationship.getEndId()).isEqualTo(3L);
        assertThat(relationship.getProperties()).isEmpty();
    }

    @Test
    public void readWithProperties() throws Exception {
        JsonParser parser = jsonFactory.createParser(new ByteArrayInputStream("{\"id\":\"1\",\"type\":\"t1\",\"startNode\":\"2\",\"endNode\":\"3\",\"properties\":{\"p1\":99}}".getBytes()));
        parser.nextToken();

        Relationship relationship = Relationship.read(parser);

        assertThat(parser.getCurrentToken()).isEqualTo(JsonToken.END_OBJECT);
        assertThat(parser.nextToken()).isNull();
        assertThat(relationship.getId()).isEqualTo(1L);
        assertThat(relationship.getType()).isEqualTo("t1");
        assertThat(relationship.getStartId()).isEqualTo(2L);
        assertThat(relationship.getEndId()).isEqualTo(3L);
        assertThat(relationship.getProperties().keySet()).containsOnly("p1");
        assertThat(relationship.getProperties().get("p1")).isEqualTo(99L);
    }

    @Test
    public void toStringFormat() throws Exception {
        Relationship relationship = new Relationship(99L, "TYPE", 88L, 77L, Collections.singletonMap("key", "value"));

        assertThat(relationship.toString()).isEqualTo("Relationship(id=99,type=TYPE,startId=88,endId=77,properties={key=value})");
    }
}