package org.braindump.repository;

import org.braindump.model.BoardEntity;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BoardRepository implements PanacheRepository<BoardEntity> {

}
