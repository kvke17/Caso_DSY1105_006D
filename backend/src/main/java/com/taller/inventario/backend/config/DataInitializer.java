package com.taller.inventario.backend.config;

import com.taller.inventario.backend.model.Producto;
import com.taller.inventario.backend.repository.ProductoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ProductoRepository repository;

    public DataInitializer(ProductoRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.count() == 0) {
            List<Producto> iniciales = Arrays.asList(
                new Producto("ACE-CAST-5W30", "Aceite Sintético Edge 5W-30 4L", "Castrol", "Lubricantes", "Bidón", 16, 6, 28000.0, 39990.0, "A", "1", "A", "Castrol Chile", "DISPONIBLE"),
                new Producto("ACE-MOB-10W40", "Aceite Semisintético Super 2000 10W-40 4L", "Mobil", "Lubricantes", "Bidón", 3, 5, 22000.0, 31990.0, "A", "1", "B", "Mobil Distribución", "BAJO_STOCK"),
                new Producto("FIL-BOS-O102", "Filtro de Aceite Blindado P3300", "Bosch", "Filtros", "Unidad", 12, 4, 4500.0, 8990.0, "A", "2", "A", "Bosch Repuestos", "DISPONIBLE"),
                new Producto("PAS-BREM-P06", "Pastillas de Freno Delanteras Cerámicas", "Brembo", "Frenos", "Juego", 2, 4, 32000.0, 48990.0, "B", "1", "A", "Brembo Chile", "BAJO_STOCK"),
                new Producto("BUJ-NGK-IRID", "Bujía Láser Iridium LFR6AIX-11", "NGK", "Encendido", "Unidad", 24, 8, 7500.0, 12990.0, "B", "2", "A", "NGK Sparks", "DISPONIBLE"),
                new Producto("BAT-BOS-S4-60", "Batería Libre Mantención S4 60Ah 540CCA", "Bosch", "Baterías", "Unidad", 5, 3, 55000.0, 79990.0, "C", "1", "Piso", "Bosch Repuestos", "DISPONIBLE")
            );
            repository.saveAll(iniciales);
        }
    }
}
