package bbt.tao.warehouse.controller.warehouse_worker;

import bbt.tao.warehouse.dto.inventory.InventoryCountDTO;
import bbt.tao.warehouse.dto.inventory.InventoryDTO;
import bbt.tao.warehouse.dto.inventory.InventoryTransactionDTO;
import bbt.tao.warehouse.dto.location.LocationDTO;
import bbt.tao.warehouse.dto.product.ProductDTO;
import bbt.tao.warehouse.dto.product.ProductSummaryDTO;
import bbt.tao.warehouse.mapper.InventoryCountMapper;
import bbt.tao.warehouse.mapper.ProductMapper;
import bbt.tao.warehouse.model.InventoryCount;
import bbt.tao.warehouse.repository.InventoryCountRepository;
import bbt.tao.warehouse.repository.InventoryRepository;
import bbt.tao.warehouse.service.InventoryService;
import bbt.tao.warehouse.service.InventoryTransactionService;
import bbt.tao.warehouse.service.ProductService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/worker")
@PreAuthorize("hasRole('WAREHOUSE_WORKER')")
@AllArgsConstructor
public class WorkerInventoryController {

    private final ProductService productService;
    private final InventoryService inventoryService;
    private final InventoryCountRepository inventoryCountRepository;
    private final ProductMapper productMapper;
    private final InventoryTransactionService inventoryTransactionService;
    private final InventoryCountMapper inventoryCountMapper;

    @GetMapping("/stock")
    public String getStockOverview(Model model) {
        List<InventoryCount> inventoryCounts = inventoryCountRepository.findAll();
        model.addAttribute("products",inventoryCounts.stream().map(inventoryCount -> {
            ProductDTO productDTO = productMapper.toDTO(inventoryCount.getProduct());
            productDTO.setCurrentStock(inventoryCount.getActualQuantity());
            productDTO.setLocation(inventoryCount.getLocation().getWarehouse().getName());
            return productDTO;
        }).collect(Collectors.toList()));
        model.addAttribute("lowStockProducts", inventoryCounts.stream()
                .filter(inventoryCount -> inventoryCount.getActualQuantity() < inventoryCount.getProduct().getMinStockLevel() + 4)
                .map(inventoryCount -> {
                    ProductDTO productDTO = productMapper.toDTO(inventoryCount.getProduct());
                    productDTO.setCurrentStock(inventoryCount.getActualQuantity());
                    productDTO.setLocation(inventoryCount.getLocation().getWarehouse().getName());
                    return productDTO;
                })
                .collect(Collectors.toList()));
        return "worker/stock/overview";
    }

    @GetMapping("/inventory")
    public String getActiveInventories(Model model) {
        model.addAttribute("inventories", inventoryService.findAllInventories());
        return "worker/inventory/list";
    }

    @GetMapping("/inventory/{id}")
    public String getInventoryDetails(@PathVariable Long id, Model model) {
        model.addAttribute("inventory", inventoryService.findInventoryById(id).orElse(null));
        model.addAttribute("counts", inventoryService.findCountsByInventory(id));
        return "worker/inventory/details";
    }

    @GetMapping("/inventory/{id}/count")
    public String getInventoryCountForm(@PathVariable Long id, Model model) {
        model.addAttribute("inventory", inventoryService.findInventoryById(id).orElse(null));
        model.addAttribute("products", productService.findAllProducts());
        return "worker/inventory/count-form";
    }

    @GetMapping("/stock/product/{id}")
    public String getProductDetails(@PathVariable Long id, Model model) {
        ProductDTO productDTO = inventoryCountRepository.findAll().stream()
                .filter(inventoryCount -> inventoryCount.getProduct().getId().equals(id))
                .map( inventoryCount -> {ProductDTO product = productMapper.toDTO(inventoryCount.getProduct());
                    product.setCurrentStock(inventoryCount.getActualQuantity());
                    product.setLocation(inventoryCount.getLocation().getWarehouse().getName());
                    return product;
                }).findFirst().orElse(null);
        log.info("Product DTO {}", productDTO);
        model.addAttribute("product", productDTO);

        assert productDTO != null;
        List<InventoryTransactionDTO> transactions = inventoryTransactionService.findTransactionsByProduct(productDTO.getId());

        model.addAttribute("movements", transactions);
        return "worker/stock/product-details";
    }

    @PostMapping("/inventory/{id}/count")
    public String saveInventoryCount(
            @PathVariable Long id,
            @RequestParam Long productId,
            @RequestParam String location,
            @RequestParam(required = false) Double expectedQuantity,
            @RequestParam Double actualQuantity,
            @RequestParam(required = false) String batchNumber,
            @RequestParam(required = false) String notes
    ) {
        // 1) Получаем саму инвентаризацию
        InventoryDTO inventory = inventoryService.findInventoryById(id)
                .orElseThrow(() -> new IllegalArgumentException("Инвентаризация не найдена: " + id));

        // 2) Собираем DTO подсчёта
        InventoryCountDTO countDto = InventoryCountDTO.builder()
                .inventory(inventory)
                .product(ProductSummaryDTO.builder().id(productId).build())
                .location(LocationDTO.builder().code(location).build())
                .expectedQuantity(expectedQuantity)
                .actualQuantity(actualQuantity)
                .batchNumber(batchNumber)
                .notes(notes)
                .build();

        // 3) Делаем сохранение через сервис
        inventoryService.saveCount(inventoryCountMapper.toEntity(countDto));

        // 4) Редирект обратно в детали инвентаризации
        return "redirect:/worker/inventory/" + id;
    }

    @GetMapping("/stock/search")
    public String searchProducts(@RequestParam String query, Model model) {

        model.addAttribute("products", productService.findProductsBySkuOrNameOrBarcode(query));
        model.addAttribute("searchQuery", query);
        return "worker/stock/overview";
    }
}