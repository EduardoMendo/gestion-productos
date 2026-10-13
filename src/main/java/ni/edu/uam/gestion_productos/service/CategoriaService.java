package ni.edu.uam.gestion_productos.service;

import ni.edu.uam.gestion_productos.dto.CategoriaRequestDTO;
import ni.edu.uam.gestion_productos.entity.Categoria;
import ni.edu.uam.gestion_productos.repository.CategoriaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public List<Categoria> listar() {
        return categoriaRepository.findAll();
    }

    public Categoria buscarPorId(Integer id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Categoría no encontrada"));
    }

    public Categoria guardar(CategoriaRequestDTO dto) {
        Categoria categoria = new Categoria();
        categoria.setNombre(dto.getNombre().trim());
        categoria.setActiva(dto.getActiva() != null ? dto.getActiva() : true);
        return categoriaRepository.save(categoria);
    }

    public Categoria actualizar(Integer id, CategoriaRequestDTO dto) {
        Categoria categoria = buscarPorId(id);
        categoria.setNombre(dto.getNombre().trim());
        if (dto.getActiva() != null) {
            categoria.setActiva(dto.getActiva());
        }
        return categoriaRepository.save(categoria);
    }

    @Transactional
    public void eliminar(Integer id) {
        Categoria categoria = buscarPorId(id);
        if (!categoria.getProductos().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No se puede eliminar: la categoría tiene productos asociados");
        }
        categoriaRepository.delete(categoria);
    }
}