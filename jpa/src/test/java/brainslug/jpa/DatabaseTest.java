package brainslug.jpa;

import com.mysema.query.jpa.JPQLTemplates;
import org.junit.Test;

import javax.persistence.EntityManager;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class DatabaseTest {

    /**
     * only flushed entities will be found be JPA queries,
     * so if e.g. we are getting the instance at the end of creation
     * it will have the inserted tokens, properties
     */
    @Test
    public void shouldFlushAfterPersist() {
        // given:
        JPQLTemplates templates = mock(JPQLTemplates.class);
        EntityManager entityManager = mock(EntityManager.class);

        // when:
        Object entity = new Object();
        new Database(entityManager, templates).insertOrUpdate(entity);

        // then:
        verify(entityManager).persist(entity);
        verify(entityManager).flush();
    }
}