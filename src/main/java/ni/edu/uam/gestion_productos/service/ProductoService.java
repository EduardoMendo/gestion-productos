package ni.edu.uam.gestion_productos.service;

import ni.edu.uam.gestion_productos.dto.ProductoRequestDTO;
import ni.edu.uam.gestion_productos.entity.Categoria;
import ni.edu.uam.gestion_productos.entity.Etiqueta;
import ni.edu.uam.gestion_productos.entity.Producto;
import ni.edu.uam.gestion_productos.entity.Proveedor;
import ni.edu.uam.gestion_productos.repository.CategoriaRepository;
import ni.edu.uam.gestion_productos.repository.EtiquetaRepository;
import ni.edu.uam.gestion_productos.repository.ProductoRepository;
import ni.edu.uam.gestion_productos.repository.ProveedorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProveedorRepository proveedorRepository;
    private final EtiquetaRepository etiquetaRepository;

    public ProductoService(ProductoRepository productoRepository,
                           CategoriaRepository categoriaRepository,
                           ProveedorRepository proveedorRepository,
                           EtiquetaRepository etiquetaRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.proveedorRepository = proveedorRepository;
        this.etiquetaRepository = etiquetaRepository;
    }

    public List<Producto> listar() {
        return productoRepository.findAll();
    }

    public Producto buscarPorId(Integer id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Producto no encontrado"));
    }

    public Producto guardar(ProductoRequestDTO dto) {
        if (productoRepository.existsByCodigo(dto.getCodigo())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya existe un producto con ese código");
        }
        Producto producto = new Producto();
        copiarDatos(producto, dto);
        return productoRepository.save(producto);
    }

    public Producto actualizar(Integer id, ProductoRequestDTO dto) {
        Producto producto = buscarPorId(id);
        if (productoRepository.existsByCodigoAndIdNot(dto.getCodigo(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya existe otro producto con ese código");
        }
        copiarDatos(producto, dto);
        return productoRepository.save(producto);
    }

    @Transactional
    public void eliminar(Integer id) {
        Producto producto = buscarPorId(id);
        producto.getEtiquetas().clear();
        productoRepository.delete(producto);
    }

    public List<Producto> listarPorCategoria(Integer categoriaId) {
        return productoRepository.findByCategoriaId(categoriaId);
    }

    public List<Producto> listarPorEtiqueta(Integer etiquetaId) {
        return productoRepository.findByEtiquetasId(etiquetaId);
    }

    @Transactional
    public Producto agregarEtiqueta(Integer productoId, Integer etiquetaId) {
        Producto producto = buscarPorId(productoId);
        Etiqueta etiqueta = buscarEtiqueta(etiquetaId);

        boolean yaAsociada = producto.getEtiquetas().stream()
                .anyMatch(e -> e.getId().equals(etiquetaId));
        if (!yaAsociada) {
            producto.getEtiquetas().add(etiqueta);
        }
        return productoRepository.save(producto);
    }

    @Transactional
    public Producto quitarEtiqueta(Integer productoId, Integer etiquetaId) {
        Producto producto = buscarPorId(productoId);
        buscarEtiqueta(etiquetaId);

        producto.getEtiquetas().removeIf(e -> e.getId().equals(etiquetaId));
        return productoRepository.save(producto);
    }

    private Etiqueta buscarEtiqueta(Integer etiquetaId) {
        return etiquetaRepository.findById(etiquetaId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Etiqueta no encontrada"));
    }

    private void copiarDatos(Producto producto, ProductoRequestDTO dto) {
        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Categoría no encontrada"));

        producto.setCodigo(dto.getCodigo().trim());
        producto.setNombre(dto.getNombre().trim());
        producto.setPrecioVenta(dto.getPrecioVenta());
        producto.setExistencia(dto.getExistencia() != null ? dto.getExistencia() : 0);
        producto.setCategoria(categoria);

        if (dto.getDescripcion() != null) {
            producto.setDescripcion(dto.getDescripcion());
        }

        if (dto.getProveedorId() != null) {
            Proveedor proveedor = proveedorRepository.findById(dto.getProveedorId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Proveedor no encontrado"));
            producto.setProveedor(proveedor);
        }
    }
}