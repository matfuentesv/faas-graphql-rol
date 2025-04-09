package cl.veterinary.service;

import cl.veterinary.model.Rol;

import java.util.List;
import java.util.Optional;

public interface RolService {

    List<Rol>findRolAll();
    Optional<Rol> finRolById(Long id);
}
