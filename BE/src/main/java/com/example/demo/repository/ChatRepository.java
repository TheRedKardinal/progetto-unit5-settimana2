package com.example.demo.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Chat;

public interface ChatRepository extends JpaRepository<Chat, UUID> {

    Optional<Chat> findByUser1_IdAndUser2_IdOrUser2_IdAndUser1_Id(
            UUID user1Id, UUID user2Id, UUID otherUser2Id, UUID otherUser1Id);

    long countByUser1_IdOrUser2_Id(UUID user1Id, UUID user2Id);

    List<Chat> findByUser1_IdOrUser2_Id(UUID user1Id, UUID user2Id);

    default Optional<Chat> findTraUtenti(UUID utenteAId, UUID utenteBId) {
        return findByUser1_IdAndUser2_IdOrUser2_IdAndUser1_Id(utenteAId, utenteBId, utenteAId, utenteBId);
    }

    default long countByPartecipante(UUID utenteId) {
        return countByUser1_IdOrUser2_Id(utenteId, utenteId);
    }

    default List<Chat> findByPartecipante(UUID utenteId) {
        return findByUser1_IdOrUser2_Id(utenteId, utenteId);
    }
}
