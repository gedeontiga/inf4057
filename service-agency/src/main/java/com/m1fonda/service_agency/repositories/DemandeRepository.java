package com.m1fonda.service_agency.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.m1fonda.service_agency.entities.Demande;

@Repository
public interface DemandeRepository extends JpaRepository<Demande, Long> {
}
