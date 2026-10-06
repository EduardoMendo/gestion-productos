package ni.edu.uam.gestion_productos.service;

import ni.edu.uam.gestion_productos.dto.ProveedorRequestDTO;
import ni.edu.uam.gestion_productos.entity.Proveedor;
import ni.edu.uam.gestion_productos.repository.ProveedorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;

    public ProveedorService(ProveedorRepository proveedorRepository) {
        this.proveedorRepository = proveedorRepository;
    }

    public List<Proveedor> listar() {
        return proveedorRepository.findAll();
    }

    public Proveedor buscarPorId(Integer id) {
        return proveedorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Proveedor no encontrado"));
    }

    public Proveedor guardar(ProveedorRequestDTO dto) {
        String correo = (dto.getCorreo() == null || dto.getCorreo().isBlank())
                ? null
                : dto.getCorreo().trim();

        if (correo != null && proveedorRepository.existsByCorreo(correo)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya existe un proveedor con ese correo");
        }

        Proveedor proveedor = new Proveedor();
        proveedor.setNombre(dto.getNombre().trim());
        proveedor.setTelefono(dto.getTelefono());
        proveedor.setCorreo(correo);
        proveedor.setActivo(dto.getActivo() != null ? dto.getActivo() : true);
        return proveedorRepository.save(proveedor);
    }
}