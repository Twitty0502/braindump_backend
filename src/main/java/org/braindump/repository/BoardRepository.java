package org.braindump.repository;

import java.util.UUID;

import org.braindump.model.BoardEntity;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BoardRepository implements PanacheRepositoryBase<BoardEntity, UUID> {

}
