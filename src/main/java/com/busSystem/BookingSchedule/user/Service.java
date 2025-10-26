package com.busSystem.BookingSchedule.user;

import java.util.List;
import java.util.Optional;

/**
 * Base Service class for all service layer classes in the MVC pattern.
 * This provides common CRUD operations that all services can inherit.
 *
 * @param <T> The entity type
 * @param <ID> The ID type (typically Long)
 */
public abstract class Service<T, ID> {

    /**
     * Template method for service initialization if needed.
     * Override this method in child classes if custom initialization is required.
     */
    protected void init() {
        // Default implementation - can be overridden by child classes
    }

    /**
     * Get all entities
     *
     * @return list of all entities
     */
    public abstract List<T> getAll();

    /**
     * Get an entity by its ID
     *
     * @param id the entity ID
     * @return Optional containing the entity if found
     */
    public abstract Optional<T> getById(ID id);

    /**
     * Save or update an entity
     *
     * @param entity the entity to save
     * @return the saved entity
     */
    public abstract T save(T entity);

    /**
     * Delete an entity by its ID
     *
     * @param id the entity ID to delete
     */
    public abstract void delete(ID id);

    /**
     * Check if an entity exists by its ID
     *
     * @param id the entity ID
     * @return true if exists, false otherwise
     */
    public abstract boolean exists(ID id);
}
