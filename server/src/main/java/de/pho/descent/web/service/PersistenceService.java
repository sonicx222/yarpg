package de.pho.descent.web.service;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import java.util.Collection;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 * Service for mainly CRUD operations on entities.
 *
 * @author pho
 *
 */
@Stateless
public class PersistenceService {

    @PersistenceContext
    private EntityManager em;

    /**
     * Führt einen nativen SQL Update oder Delete Befehl aus.<br>
     * Gibt die Anzahl der geupdateten oder gelöschten Entities zurück.
     *
     * @param sql
     * @return Anzahl der veränderten Entities
     */
    public int createNativeQuery(String sql) {
        Query query = em.createNativeQuery(sql);
        int retVal = query.executeUpdate();
        em.getTransaction().commit();
        return retVal;
    }

    /**
     * Save Entity by creating a new DB-Entry.
     *
     * @param entity
     */
    public <T> void create(T entity) {
        em.persist(entity);
    }

    /**
     * Find an entity by id.
     *
     * @param type type of the entity
     * @param id Id
     * @return entity with given id
     */
    public <T> T find(Class<T> type, long id) {
        return em.find(type, id);
    }

    /**
     * Update the given entity.
     *
     * @param t entity to update
     * @return the new managed entity
     */
    public <T> T update(T t) {
        return em.merge(t);
    }

    /**
     * Update the given entity.
     *
     * @param t entity to update
     * @return the new managed entity
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public <T> T updateAndCommit(T t) {

        T managedT = em.merge(t);
        em.flush();
        return managedT;
    }

    /**
     * Execute the given NamedQuery with given parameters.
     *
     * @param query name of the NamedQuery to execute
     * @param parameters parameters for the Query
     * @return number of affected rows
     */
    public int update(String query, Map<String, Object> parameters) {
        Query q = prepareQuery(em, query, parameters);
        int modified = q.executeUpdate();
        return modified;
    }

    /**
     * Delete the entity with the given id and type from the database.
     *
     * @param type type of the entity to delete
     * @param id id of the entity to delete
     */
    public <T> void delete(Class<T> type, Object id) {
        Object ref = em.getReference(type, id);
        em.remove(ref);
    }

    /**
     * Get list of entities of given type by NamedQuery.
     *
     * @param type type of the entity
     * @param namedQueryName name of the NamedQuery
     * @return list of entities found
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> findWithNamedQuery(Class<T> type, String namedQueryName) {
        return em.createNamedQuery(namedQueryName).getResultList();
    }

    /**
     * Get list of entites of given type by NamedQuery with parameters.
     *
     * @param type type of the entity
     * @param namedQueryName name of the NamedQuery
     * @param parameters parameters for the query
     * @return list of entities found
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> findWithNamedQuery(Class<T> type, String namedQueryName,
            Map<String, Object> parameters) {
        return prepareQuery(em, namedQueryName, parameters).getResultList();
    }

    /**
     * Get limited list of entites by NamedQuery.
     *
     * @param namedQueryName name of the NamedQuery
     * @param resultLimit limit of the result list
     * @return limited list of entities found
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> findWithNamedQuery(String namedQueryName, int resultLimit) {
        return em.createNamedQuery(namedQueryName).setMaxResults(resultLimit).getResultList();
    }

    /**
     * Get list of entities by NamedQuery.
     *
     * @param namedQueryName name of the NamedQuery
     * @param parameters parameters for the query
     * @return list of entities found
     */
    public <T> List<T> findWithNamedQuery(String namedQueryName, Map<String, Object> parameters) {
        return findWithNamedQuery(namedQueryName, parameters, 0);
    }

    /**
     * Get limited list of entites by NamedQuery with parameters.
     *
     * @param namedQueryName name of the NamedQuery
     * @param parameters parameters for the query
     * @param resultLimit limit of the result list
     * @return limited list of entities found
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> findWithNamedQuery(String namedQueryName, Map<String, Object> parameters,
            int resultLimit) {
        Query query = prepareQuery(em, namedQueryName, parameters);
        if (resultLimit > 0) {
            query.setMaxResults(resultLimit);
        }
        return query.getResultList();
    }

    /**
     * Get single entity of given type by NamedQuery with parameters.
     *
     * @param type type of the entity
     * @param namedQueryName name of the NamedQuery
     * @param parameters parameters for the query
     * @return entity found
     */
    public <T> T findWithNamedQueryUnique(Class<T> type, String namedQueryName,
            Map<String, Object> parameters) {
        List<T> resultList = findWithNamedQuery(type, namedQueryName, parameters);
        if (resultList == null || resultList.isEmpty()) {
            return null;
        } else if (resultList.size() > 1) {
            throw new RuntimeException("Mehr als ein Datensatz gefunden");
        }
        return resultList.get(0);
    }

    /**
     * Prepare the NamedQuery. Set parameters to the query.
     *
     * @param em EntityManager
     * @param namedQuery NamedQuery
     * @param parameters parameters to set
     * @return Query
     */
    private Query prepareQuery(EntityManager em, String namedQuery, Map<String, Object> parameters) {
        Set<Entry<String, Object>> rawParameters = parameters.entrySet();
        Query myQuery = em.createNamedQuery(namedQuery);
        for (Entry<String, Object> entry : rawParameters) {
            myQuery.setParameter(entry.getKey(), entry.getValue());
        }
        return myQuery;
    }

    /**
     * Remove all given entities.
     *
     * @param list list of entities to remove
     */
    public <T> void deleteList(List<T> list) {
        for (T t : list) {
            em.remove(t);
        }
    }

    /**
     * Remove all given entities.
     *
     * @param set set of entities to remove
     */
    public <T> void deleteSet(Set<T> set) {
        for (T t : set) {
            em.remove(t);
        }
    }

    /**
     * Remove reference of the given type with the given id.
     *
     * @param type type of the entity to remove
     * @param id id of the entity to remove
     */
    public <T> void deleteReference(Class<T> type, Object id) {
        Object ref = em.getReference(type, id);
        em.remove(ref);
    }

    public void detachAll(Collection<? extends Object> entities) {
        for (Object object : entities) {
            em.detach(object);
        }
    }
}
