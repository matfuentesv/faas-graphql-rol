package cl.veterinary.service.impl;


import cl.veterinary.model.Rol;
import cl.veterinary.repository.RolRepository;
import cl.veterinary.service.RolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RolServiceImpl implements RolService {

    @Autowired
    private RolRepository rolRepository;

    @Override
    public List<Rol> findRolAll() {
        return rolRepository.findAll();
    }

    @Override
    public Optional<Rol> finRolById(Long id) {
        return rolRepository.findById(id);
     }

    @Override
    public Rol saveRol(Rol rol) {
        return rolRepository.save(rol);
    }

    @Override
    public Rol updateRol(Rol rol) {
        return rolRepository.save(rol);
    }

    @Override
    public void deleteRol(Long id) {
        rolRepository.deleteById(id);
    }
}
