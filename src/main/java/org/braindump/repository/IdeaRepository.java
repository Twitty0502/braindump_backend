package org.braindump.repository;

import org.braindump.model.IdeaEntity;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class IdeaRepository implements PanacheRepository<IdeaEntity> {

}
