package cl.veterinary.client;

import cl.veterinary.model.RolEvent;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "eventProducerClient", url = "https://event-producer-function.azurewebsites.net")
public interface EventProducerClient {

    @GetMapping("/api/RoleCrudFunction")
    String eventGet(
            @RequestParam("code") String code,
            @RequestParam("operation") String operation,
            @RequestParam("id") Long id
    );


    @PostMapping(value = "/api/RoleCrudFunction", consumes = "application/json")
    void eventPost(
            @RequestParam("code") String code,
            @RequestParam("operation") String operation,
            @RequestBody RolEvent dto
    );

    @PutMapping(value = "/api/RoleCrudFunction", consumes = "application/json")
    void eventPut(
            @RequestParam("code") String code,
            @RequestParam("operation") String operation,
            @RequestBody RolEvent dto
    );

    @DeleteMapping(value = "/api/RoleCrudFunction", consumes = "application/json")
    void eventDelete(
            @RequestParam("code") String code,
            @RequestParam("operation") String operation,
            @RequestBody RolEvent dto
    );



}
