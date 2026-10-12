package com.minifacture.backend.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Long> {

    @Query("select lower(c.email) from Client c")
    List<String> findAllEmails();
}