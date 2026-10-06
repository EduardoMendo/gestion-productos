package ni.edu.uam.gestion_productos.service;

import ni.edu.uam.gestion_productos.dto.EtiquetaRequestDTO;
import ni.edu.uam.gestion_productos.entity.Etiqueta;
import ni.edu.uam.gestion_productos.repository.EtiquetaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class EtiquetaService {

    private final EtiquetaRepository etiquetaRepository;

    public EtiquetaService(EtiquetaRepository etiquetaRepository) {
        this.etiquetaRepository = etiquetaRepository;
    }

    public List<Etiqueta> listar() {
        return etiquetaRepository.findAll();
    }

    public Etiqueta buscarPorId(Integer id) {
        return etiquetaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Etiqueta no encontrada"));
    }

    public Etiqueta guardar(EtiquetaRequestDTO dto) {
        String nombre = dto.getNombre().trim();

        if (etiquetaRepository.existsByNombre(nombre)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya existe una etiqueta con ese nombre");
        }

        Etiqueta etiqueta = new Etiqueta();
        etiqueta.setNombre(nombre);
        return etiquetaRepository.save(etiqueta);
    }
}