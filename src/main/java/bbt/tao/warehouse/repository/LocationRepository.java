package bbt.tao.warehouse.repository;

import bbt.tao.warehouse.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    List<Location> findByWarehouse_Id(Long warehouseId);

    Optional<Location> findByCodeAndWarehouse_Id(String code, Long warehouseId);

    @Query("SELECT l FROM Location l WHERE l.maxWeight >= :weight AND l.maxVolume >= :volume AND l.warehouse.id = :warehouseId")
    List<Location> findSuitableLocations(@Param("weight") Double weight, @Param("volume") Double volume, @Param("warehouseId") Long warehouseId);
}
