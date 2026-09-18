package com.example.demo.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.Messaggio;

public interface MessaggioRepository extends JpaRepository<Messaggio, UUID> {

    List<Messaggio> findByChat_IdOrderByCreatedAtDesc(UUID chatId, Pageable pageable);

    long countByMittente_Id(UUID mittenteId);

    @Query("""
            select count(m) from Messaggio m
            where (m.chat.user1.id = :utenteId or m.chat.user2.id = :utenteId)
            and m.mittente.id <> :utenteId
            """)
    long countMessaggiRicevuti(@Param("utenteId") UUID utenteId);

    List<Messaggio> findByChat_IdAndMittente_IdNotAndLettoFalse(UUID chatId, UUID mittenteId);

    long countByChat_IdAndMittente_IdNotAndLettoFalse(UUID chatId, UUID mittenteId);

    Optional<Messaggio> findFirstByChat_IdOrderByCreatedAtDesc(UUID chatId);

    @Modifying
    @Query("""
            update Messaggio m set m.letto = true
            where m.chat.id = :chatId and m.mittente.id <> :lettoreId and m.letto = false
            """)
    int segnaComeLetti(@Param("chatId") UUID chatId, @Param("lettoreId") UUID lettoreId);
}
