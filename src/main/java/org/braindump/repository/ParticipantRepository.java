package org.braindump.repository;

import org.braindump.model.ParticipantEntity;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ParticipantRepository implements PanacheRepository<ParticipantEntity> {

}
