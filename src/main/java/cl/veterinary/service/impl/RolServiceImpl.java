package cl.veterinary.service.impl;


import cl.veterinary.client.EventProducerClient;
import cl.veterinary.model.Rol;
import cl.veterinary.model.RolEvent;
import cl.veterinary.repository.RolRepository;
import cl.veterinary.service.RolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class RolServiceImpl implements RolService {

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private EventProducerClient eventProducerClient;

    private static final Logger log = LoggerFactory.getLogger(RolServiceImpl.class);

    private static final String FUNCTION_CODE = "dleoJoi2alggyThI3JUnVvNziSuQcYuovsD6JMVhFUt7AzFuw1yrKQ==";

    @Override
    public List<Rol> findRolAll() {
        List<Rol> roles = rolRepository.findAll();
        eventProducerClient.eventGet(FUNCTION_CODE, "GET", 1L);
        return roles;
    }

    @Override
    public Optional<Rol> finRolById(Long id) {
        Optional<Rol> rol = rolRepository.findById(id);
        if (rol.isPresent()) {
            eventProducerClient.eventGet(FUNCTION_CODE, "GET", rol.get().getId());
        } else {
            log.info("Rol no encontrado para ID: {}", id);
        }
        return rol;
     }

    @Override
    public Rol saveRol(Rol rol) {
        Rol savedRol = rolRepository.save(rol);
        RolEvent evento = new RolEvent(savedRol.getId(), savedRol.getDescripcion());
        eventProducerClient.eventPost(FUNCTION_CODE, "CREATE", evento);
        return savedRol;
    }

    @Override
    public Rol updateRol(Rol rol) {
        Rol updatedRol = rolRepository.save(rol);
        RolEvent evento = new RolEvent(updatedRol.getId(), updatedRol.getDescripcion());
        eventProducerClient.eventPut(FUNCTION_CODE, "UPDATE", evento);
        return updatedRol;
    }

    @Override
    public void deleteRol(Long id) {
        rolRepository.deleteById(id);
        RolEvent evento = new RolEvent(id);
        eventProducerClient.eventDelete(FUNCTION_CODE, "DELETE", evento);
    }

}
