package org.braindump.resource;

import java.util.List;

import org.braindump.model.BoardEntity;
import org.braindump.model.ParticipantEntity;
import org.braindump.repository.BoardRepository;
import org.braindump.service.ParticipantService;

import jakarta.ws.rs.Produces;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.Path;

@Path("/boards")

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BoardResource {

    @Inject
    BoardRepository boardRepository;
    @Inject
    ParticipantService participantService;

    @GET
    public List<BoardEntity> getAllBoards() {
        return boardRepository.listAll();
    }

    @POST
    @Path("/create")
    @Transactional
    public Response createBoard(BoardEntity board) {
        board.setId(null); // Säkerhetsställ att id är null vid skapandet
        boardRepository.persist(board);
        return Response.status(Response.Status.CREATED)
                .entity(board)
                .build();
    }

    @POST
    @Path("/{boardId}/participants")
    @Transactional
    public Response addParticipant(@PathParam("boardId") Long boardId, ParticipantEntity participant) {
        ParticipantEntity createdParticipant = participantService.joinBoard(
                participant.getDisplayName(), boardId);

        return Response.status(Response.Status.CREATED)
                .entity(createdParticipant)
                .build();
    }

}
