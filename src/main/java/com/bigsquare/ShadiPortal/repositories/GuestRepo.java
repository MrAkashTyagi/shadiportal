package com.bigsquare.ShadiPortal.repositories;

import com.bigsquare.ShadiPortal.entities.Guest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuestRepo extends CrudRepository<Guest, Integer>
{

}
