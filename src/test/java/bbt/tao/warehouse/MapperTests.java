package bbt.tao.warehouse;

import bbt.tao.warehouse.dto.audit.AuditLogDTO;
import bbt.tao.warehouse.dto.category.CategoryDTO;
import bbt.tao.warehouse.dto.customer.CustomerDTO;
import bbt.tao.warehouse.dto.inventory.*;
import bbt.tao.warehouse.dto.location.LocationDTO;
import bbt.tao.warehouse.dto.location.LocationSummaryDTO;
import bbt.tao.warehouse.dto.permission.PermissionDTO;
import bbt.tao.warehouse.dto.product.ProductDTO;
import bbt.tao.warehouse.dto.product.ProductSummaryDTO;
import bbt.tao.warehouse.dto.role.RoleDTO;
import bbt.tao.warehouse.dto.supplier.SupplierDTO;
import bbt.tao.warehouse.dto.user.UserDTO;
import bbt.tao.warehouse.dto.warehouse.WarehouseDTO;
import bbt.tao.warehouse.mapper.*;
import bbt.tao.warehouse.model.*;
import bbt.tao.warehouse.model.enums.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class MapperTests {

    private final AuditLogMapper auditLogMapper;
    private final CategoryMapper categoryMapper;
    private final CustomerMapper customerMapper;
    private final InventoryMapper inventoryMapper;
    private final InventoryCountMapper inventoryCountMapper;
    private final InventoryTransactionMapper inventoryTransactionMapper;
    private final LocationMapper locationMapper;
    private final PermissionMapper permissionMapper;
    private final ProductMapper productMapper;
    private final RoleMapper roleMapper;
    private final SupplierMapper supplierMapper;
    private final UserMapper userMapper;
    private final WarehouseMapper warehouseMapper;

    @Test
    public void testAuditLogMapper() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        AuditLog auditLog = new AuditLog();
        auditLog.setId(100L);
        auditLog.setUser(user);
        auditLog.setActionType(ActionType.CREATE);
        auditLog.setEntityType("USER");
        auditLog.setEntityId(1L);
        auditLog.setActionDetails("Successful login");
        auditLog.setIpAddress("127.0.0.1");
        auditLog.setActionTimestamp(LocalDateTime.now());

        AuditLogDTO dto = auditLogMapper.toDTO(auditLog);
        log.info("DTO: {}", dto);
        assertNotNull(dto);
        assertEquals(auditLog.getId(), dto.getId());
        assertEquals(auditLog.getActionType(), dto.getActionType());
        assertEquals(user.getId(), dto.getUserID());

        AuditLog entity = auditLogMapper.toEntity(dto);
        log.info("Entity: {}", entity);
        assertNotNull(entity);
        assertEquals(dto.getId(), entity.getId());
    }

    @Test
    public void testCategoryMapper() {
        Category parent = new Category();
        parent.setId(1L);
        parent.setName("Parent");
        parent.setDescription("Parent category");

        Category child = new Category();
        child.setId(2L);
        child.setName("Child");
        child.setDescription("Child category");

        Category category = new Category();
        category.setId(3L);
        category.setName("Electronics");
        category.setDescription("Electronics category");
        category.setParent(parent);
        category.setChildren(List.of(child));

        CategoryDTO dto = categoryMapper.toDTO(category);
        log.info("DTO: {}", dto);
        assertNotNull(dto);
        assertEquals(category.getId(), dto.getId());
        assertNotNull(dto.getParent());
        assertEquals(parent.getId(), dto.getParent().getId());
        // Преобразуем обратно
        Category entity = categoryMapper.toEntity(dto);
        log.info("Entity: {}", entity);
        assertNotNull(entity);
        assertEquals(dto.getId(), entity.getId());
    }

    @Test
    public void testCustomerMapper() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("MegaMarket");
        customer.setContactPerson("John Doe");
        customer.setPhone("123456");
        customer.setEmail("test@customer.com");
        customer.setAddress("Test Address");
        customer.setTaxId("123456789");
        customer.setIsActive(true);

        CustomerDTO dto = customerMapper.toDTO(customer);
        assertNotNull(dto);
        assertEquals(customer.getId(), dto.getId());

        Customer entity = customerMapper.toEntity(dto);
        assertNotNull(entity);
        assertEquals(dto.getId(), entity.getId());
    }

    @Test
    public void testInventoryMapper() {
        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);
        warehouse.setName("Main Warehouse");
        warehouse.setIsActive(true);

        User user = new User();
        user.setId(2L);
        user.setUsername("manager");

        Inventory inventory = new Inventory();
        inventory.setId(10L);
        inventory.setInventoryNumber("INV-001");
        inventory.setStatus(InventoryStatus.COMPLETED);
        inventory.setStartDate(LocalDateTime.now().minusHours(5));
        inventory.setEndDate(LocalDateTime.now());
        inventory.setWarehouse(warehouse);
        inventory.setCreatedBy(user);

        InventoryDTO dto = inventoryMapper.toDTO(inventory);
        log.info("DTO: {}", dto);
        assertNotNull(dto);
        assertEquals(inventory.getId(), dto.getId());
        assertNotNull(dto.getWarehouse());
        assertNotNull(dto.getCreatedBy());

        InventorySummeryDTO summeryDTO = inventoryMapper.toSummeryDTO(inventory);
        log.info("Summery DTO: {}", summeryDTO);
        assertNotNull(summeryDTO);
        assertEquals(inventory.getInventoryNumber(), summeryDTO.getInventoryNumber());
    }

    @Test
    public void testInventoryCountMapper() {
        // Создаем сущности-зависимости
        Inventory inventory = new Inventory();
        inventory.setId(10L);
        inventory.setInventoryNumber("INV-001");

        Product product = new Product();
        product.setId(5L);
        product.setName("Test Product");

        Location location = new Location();
        location.setId(3L);
        location.setCode("L-001");

        User countedBy = new User();
        countedBy.setId(4L);
        countedBy.setUsername("warehouseUser");

        InventoryCount count = new InventoryCount();
        count.setId(20L);
        count.setInventory(inventory);
        count.setProduct(product);
        count.setLocation(location);
        count.setExpectedQuantity(100.0);
        count.setActualQuantity(98.0);
        count.setBatchNumber("BATCH-01");
        count.setCountDate(LocalDateTime.now());
        count.setCountedBy(countedBy);
        count.setNotes("Test note");

        InventoryCountDTO dto = inventoryCountMapper.toDTO(count);
        log.info("DTO: {}", dto);
        assertNotNull(dto);
        assertEquals(count.getId(), dto.getId());
        assertNotNull(dto.getInventory());
        assertNotNull(dto.getProduct());
        assertNotNull(dto.getLocation());
        assertNotNull(dto.getCountedBy());

        InventoryCountSummaryDTO summaryDTO = inventoryCountMapper.toSummaryDTO(count);
        log.info("Summary DTO: {}", summaryDTO);
        assertNotNull(summaryDTO);
        assertEquals(count.getInventory().getId(), summaryDTO.getInventoryId());
    }

    @Test
    public void testInventoryTransactionMapper() {
        Product product = new Product();
        product.setId(5L);
        product.setName("Test Product");

        Location sourceLocation = new Location();
        sourceLocation.setId(3L);
        sourceLocation.setCode("L-001");

        Location destinationLocation = new Location();
        destinationLocation.setId(4L);
        destinationLocation.setCode("L-002");

        Supplier supplier = new Supplier();
        supplier.setId(7L);
        supplier.setName("Test Supplier");

        Customer customer = new Customer();
        customer.setId(8L);
        customer.setName("Test Customer");

        User user = new User();
        user.setId(9L);
        user.setUsername("manager");

        InventoryTransaction transaction = new InventoryTransaction();
        transaction.setId(30L);
        transaction.setTransactionType(TransactionType.RECEIVING);
        transaction.setProduct(product);
        transaction.setDestinationLocation(destinationLocation);
        transaction.setQuantity(50.0);
        transaction.setBatchNumber("BATCH-02");
        transaction.setExpirationDate(null);
        transaction.setDocumentNumber("DOC-001");
        transaction.setSupplier(supplier);
        transaction.setCustomer(null);
        transaction.setUser(user);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setNotes("Test transaction");

        InventoryTransactionDTO dto = inventoryTransactionMapper.toDTO(transaction);
        log.info("DTO: {}", dto);
        assertNotNull(dto);
        assertEquals(transaction.getId(), dto.getId());
        assertNotNull(dto.getProduct());
        assertNotNull(dto.getDestinationLocation());

        InventoryTransactionSummaryDTO summaryDTO = inventoryTransactionMapper.toSummaryDTO(transaction);
        log.info("Summary DTO: {}", summaryDTO);
        assertNotNull(summaryDTO);
        assertEquals(transaction.getProduct().getId(), summaryDTO.getProductId());
    }

    @Test
    public void testLocationMapper() {
        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);
        warehouse.setName("Main Warehouse");

        Location location = new Location();
        location.setId(3L);
        location.setCode("L-001");
        location.setDescription("Test Location");
        location.setWarehouse(warehouse);
        location.setMaxWeight(1000.0);
        location.setMaxVolume(5.0);

        LocationDTO dto = locationMapper.toDTO(location);
        log.info("DTO: {}", dto);
        assertNotNull(dto);
        assertEquals(location.getId(), dto.getId());
        assertNotNull(dto.getWarehouse());
        assertEquals(warehouse.getId(), dto.getWarehouse().getId());

    }

    @Test
    public void testPermissionMapper() {
        Permission permission = new Permission();
        permission.setId(2L);
        permission.setName(PermissionType.PRODUCT_READ);
        permission.setDescription("Read products");

        PermissionDTO dto = permissionMapper.toDTO(permission);
        log.info("DTO: {}", dto);
        assertNotNull(dto);
        assertEquals(permission.getId(), dto.getId());
        assertEquals(permission.getName(), dto.getPermission());

        Permission entity = permissionMapper.toEntity(dto);
        log.info("Entity: {}", entity);
        assertNotNull(entity);
        assertEquals(dto.getId(), entity.getId());
    }

    @Test
    public void testProductMapper() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");
        category.setDescription("Electronics category");

        Product product = new Product();
        product.setId(10L);
        product.setName("Test Product");
        product.setDescription("Detailed description");
        product.setSku("SKU-123");
        product.setBarcode("BAR-123");
        product.setCategory(category);
        product.setUnitOfMeasure("pcs");
        product.setMinStockLevel(10.0);
        product.setImageUrl("/image/test.png");
        product.setWeight(1.5);
        product.setVolume(0.5);
        product.setIsActive(true);
        product.setCreatedAt(LocalDateTime.now().minusDays(1));
        product.setUpdatedAt(LocalDateTime.now());
        product.setPrice(100000);

        ProductDTO dto = productMapper.toDTO(product);
        log.info("DTO: {}", dto);
        assertNotNull(dto);
        assertEquals(product.getId(), dto.getId());
        assertNotNull(dto.getCategory());
        assertEquals(category.getId(), dto.getCategory().getId());
    }

    @Test
    public void testRoleMapper() {
        Permission permission = new Permission();
        permission.setId(1L);
        permission.setName(PermissionType.INVENTORY_CREATE);
        permission.setDescription("Create inventory");

        Role role = new Role();
        role.setId(5L);
        role.setRole(RoleType.MANAGER);
        role.setDescription("Manager role");
        role.setPermissions(Arrays.asList(permission));

        RoleDTO dto = roleMapper.toDTO(role);
        log.info("DTO: {}", dto);
        assertNotNull(dto);
        assertEquals(role.getId(), dto.getId());
        assertNotNull(dto.getPermissions());
        assertFalse(dto.getPermissions().isEmpty());
    }

    @Test
    public void testSupplierMapper() {
        Supplier supplier = new Supplier();
        supplier.setId(7L);
        supplier.setName("Test Supplier");
        supplier.setContactPerson("Supplier Contact");
        supplier.setPhone("111222");
        supplier.setEmail("supplier@test.com");
        supplier.setAddress("Supplier Address");
        supplier.setTaxId("TAX-001");
        supplier.setIsActive(true);

        SupplierDTO dto = supplierMapper.toDTO(supplier);
        log.info("DTO: {}", dto);
        assertNotNull(dto);
        assertEquals(supplier.getId(), dto.getId());

        Supplier entity = supplierMapper.toEntity(dto);
        log.info("Entity: {}", entity);
        assertNotNull(entity);
        assertEquals(dto.getId(), entity.getId());
    }

    @Test
    public void testUserMapper() {
        Role role = new Role();
        role.setId(3L);
        role.setRole(RoleType.WAREHOUSE_WORKER);
        role.setDescription("Warehouse worker");

        User user = new User();
        user.setId(9L);
        user.setUsername("warehouseUser");
        user.setFirstName("Warehouse");
        user.setLastName("User");
        user.setEmail("warehouse@test.com");
        user.setPhone("555666");
        user.setIsActive(true);
        user.setLastLogin(LocalDateTime.now());
        user.setRoles(Arrays.asList(role));

        UserDTO dto = userMapper.toDTO(user);
        log.info("DTO: {}", dto);
        assertNotNull(dto);
        assertEquals(user.getId(), dto.getId());
        assertNotNull(dto.getRoles());
        assertFalse(dto.getRoles().isEmpty());

        User entity = userMapper.toEntity(dto);
        log.info("Entity: {}", entity);
        assertNotNull(entity);
        assertEquals(dto.getId(), entity.getId());
    }

    @Test
    public void testWarehouseMapper() {
        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);
        warehouse.setName("Main Warehouse");
        warehouse.setDescription("Central storage");
        warehouse.setAddress("Test Address");
        warehouse.setIsActive(true);

        WarehouseDTO dto = warehouseMapper.toDTO(warehouse);
        log.info("DTO: {}", dto);
        assertNotNull(dto);
        assertEquals(warehouse.getId(), dto.getId());

        Warehouse entity = warehouseMapper.toEntity(dto);
        log.info("Entity: {}", entity);
        assertNotNull(entity);
        assertEquals(dto.getId(), entity.getId());
    }
}
