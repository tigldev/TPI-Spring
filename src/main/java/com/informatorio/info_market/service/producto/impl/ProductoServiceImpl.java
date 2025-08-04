package com.informatorio.info_market.service.producto.impl;

import com.informatorio.info_market.domain.Producto;
import com.informatorio.info_market.dto.producto.ProductoCreateDto;
import com.informatorio.info_market.dto.producto.ProductoDto;
import com.informatorio.info_market.dto.DescuentoRequestDTO; // <-- ¡Asegúrate de importar este DTO!
import com.informatorio.info_market.exception.badrequest.StockInsuficienteException;
import com.informatorio.info_market.exception.notfound.NotFoundException;
import com.informatorio.info_market.mapper.producto.ProductoCreateMapper;
import com.informatorio.info_market.mapper.producto.ProductoMapper;
import com.informatorio.info_market.repository.producto.ProductoRepository;
import com.informatorio.info_market.service.producto.ProductoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // <-- ¡Asegúrate de importar esta anotación!

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors; // Puede que ya lo tengas o lo necesites para otros métodos


@Service
@AllArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final ProductoMapper productoMapper;
    private final ProductoCreateMapper productoCreateMapper;


    @Override
    public List<ProductoDto> getAllProductos(int minStock, Double minPrice, Double maxPrice) {
        List<Producto> productos; //Entidades

        if(minStock == 0 && maxPrice == 0 ){
            productos = productoRepository.findAll();
        } else if (minStock > 0 && maxPrice > 0) {
            productos = productoRepository.findAllByStockIsGreaterThanAndPrecioIsBetween(minStock, minPrice, maxPrice);
        } else if (maxPrice > 0) {
            //Filtrar por maxPrice
            productos = productoRepository.findAllByPrecioIsLessThan(maxPrice);
        }else {
            productos = productoRepository.findAllByStockIsGreaterThan(minStock);
        }

        return productos.stream()
                .map( producto -> productoMapper.productoToProductoDto( producto ) )
                .toList();
    }

    @Override
    public ProductoDto getProductoById(UUID id) {
        Optional<Producto> producto = productoRepository.findById(id);
        if (producto.isPresent()) {
            return productoMapper.productoToProductoDto( producto.get() );
        }else{
            throw new NotFoundException("No se encontro el producto con id : " + id);
        }
    }

    @Override
    public Producto getProductoEntityById(UUID id) {
        Optional<Producto> producto = productoRepository.findById(id);
        if (producto.isPresent()) {
            return  producto.get() ;
        }else{
            throw new NotFoundException("No se encontro el producto con id : " + id);
        }
    }

    @Override
    public ProductoDto createProducto(ProductoCreateDto producto) {
        Producto productoToCreate = productoCreateMapper.productDtoCreateToProducto(producto);

        // No necesitas setear fechaDeCreacion ni fechaActualizacion aquí
        // tus métodos @PrePersist y @PreUpdate en la entidad Producto ya lo hacen.
        // Solo asegúrate de que esos métodos en Producto.java estén funcionando correctamente.
        // productoToCreate.setFechaDeCreacion(LocalDate.now());
        // productoToCreate.setFechaActualizacion(LocalDate.now());

        return productoMapper.productoToProductoDto( productoRepository.save(productoToCreate) ) ;
    }

    @Override
    public ProductoDto updateProducto(ProductoCreateDto producto, UUID idProducto) {

        Optional<Producto> productoToUpdate = productoRepository.findById(idProducto);

        if (productoToUpdate.isPresent()){
            Producto existingProducto = productoToUpdate.get(); // Obtener el producto existente

            // Actualizar campos del producto existente con los datos del DTO
            existingProducto.setNombre(producto.getNombre());
            existingProducto.setDescripcion(producto.getDescripcion());
            existingProducto.setPrecio(producto.getPrecio());
            existingProducto.setStock(producto.getStock());
            existingProducto.setMarca(producto.getMarca()); // Asegúrate de que el DTO tenga este campo si lo actualizas

            // No necesitas setear fechaDeCreacion (ya está seteada)
            // La fechaActualizacion se maneja con @PreUpdate en la entidad Producto

            // Si tienes categorías en el DTO de actualización, deberías manejarlas aquí también
            // existingProducto.setCategorias(producto.getCategorias());

            productoRepository.save(existingProducto); // Guardar el producto actualizado
            return productoMapper.productoToProductoDto(existingProducto);

        }else{
            throw new NotFoundException("No se encontro el producto con id : " + idProducto);
        }

    }

    @Override
    public void descontarStock(Producto producto, int cantidad) {
        if ( producto.getStock() < cantidad ){
            //Ejecutar excepcion
            throw new StockInsuficienteException("No existe stock suficiente del producto");
        }else {
            producto.setStock(producto.getStock() - cantidad);
            productoRepository.save(producto);
        }
    }

    @Override
    public void deleteProducto(UUID id) {
        if (productoRepository.existsById( id )){
            productoRepository.deleteById(id);
        } else { // Añadido un else para asegurar que la excepción se lance solo si no existe
            throw new NotFoundException("No se encontro el producto con id : " + id);
        }
    }

    @Override
    public List<ProductoDto> testProductsQueries() {
        List<Producto> productos;
        productos = productoRepository.obtenerTodosLosProductosConNombreCategoriaNative("Deportes");
        return productos.stream()
                .map(productoMapper::productoToProductoDto)
                .toList();
    }

    // --- IMPLEMENTACIÓN DEL NUEVO MÉTODO PARA APLICAR DESCUENTO ---
    @Override // Sobreescribe el método de la interfaz
    @Transactional // Asegura que toda la operación sea transaccional
    public List<Producto> aplicarDescuentoPorMarca(DescuentoRequestDTO descuentoRequest) {
        String marca = descuentoRequest.getMarca();
        Double porcentaje = descuentoRequest.getPorcentajeDescuento();

        List<Producto> productosAModificar = productoRepository.findByMarcaIgnoreCase(marca);

        if (productosAModificar.isEmpty()) {
            throw new NotFoundException("No se encontraron productos para la marca: " + marca);
            // Cambié a NotFoundException para que sea más coherente con tu manejo de errores
            // Puedes crear una excepción personalizada si lo prefieres, o manejarlo de otra forma.
        }

        for (Producto producto : productosAModificar) {
            producto.aplicarDescuento(porcentaje); // Llama al método que ya existe en la entidad Producto
        }

        // Guarda todos los productos modificados en una sola operación de base de datos
        List<Producto> productosGuardados = productoRepository.saveAll(productosAModificar);

        // Si necesitas devolver DTOs, deberías mapearlos aquí:
        // return productosGuardados.stream()
        //         .map(productoMapper::productoToProductoDto)
        //         .toList();
        // Pero como la interfaz ProductoService.java pide List<Producto>, devolvemos las entidades directamente.
        return productosGuardados;
    }
}