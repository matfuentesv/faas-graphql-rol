package cl.veterinary.model;

public class RolEvent {

    private Long id;
    private String descripcion;


    public RolEvent() {
    }

    public RolEvent(Long id) {
        this.id = id;
    }

    public RolEvent(Long id, String descripcion ) {
        this.id = id;
        this.descripcion = descripcion;

    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }



}
