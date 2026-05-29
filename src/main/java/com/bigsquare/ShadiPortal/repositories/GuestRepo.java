package com.bigsquare.ShadiPortal.repositories;

import com.bigsquare.ShadiPortal.entities.Family;
import com.bigsquare.ShadiPortal.entities.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.xml.crypto.dsig.spec.XSLTTransformParameterSpec;
import java.util.Optional;

@Repository
public interface GuestRepo extends JpaRepository<Guest, Integer>
{



}
