package bbt.tao.warehouse.conf.init;

import bbt.tao.warehouse.model.*;
import bbt.tao.warehouse.model.enums.*;
import bbt.tao.warehouse.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializerToDB implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final SupplierRepository supplierRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final LocationRepository locationRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryCountRepository inventoryCountRepository;
    private final AuditLogRepository auditLogRepository;
    private final TaskRepository taskRepository;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Initializing database with default data");
        Map<PermissionType, Permission> permissions = initPermissions();
        Map<RoleType, Role> roles = initRoles(permissions);
        initUsers(roles);
        initSuppliers();
        initCustomers();
        Map<String, Category> categories = initCategories();
        initProducts(categories);
        initWarehouses();
        Map<String, Warehouse> warehouses = getWarehousesMap();
        initLocations(warehouses);
        Map<String, Location> locations = getLocationsMap();
        initInventoryItems(locations);
        initInventoryTransactions(locations);
        initInventories(warehouses);
        initInventoryCounts();
        initAuditLogs();
        initTasks();
    }

    private void initTasks() {
        if (taskRepository.count() == 0) {
            log.info("Creating default tasks");

            List<User> users = userRepository.findAll();
            User admin = users.stream().filter(u -> u.getUsername().equals("admin")).findFirst().orElse(null);
            User manager = users.stream().filter(u -> u.getUsername().equals("manager")).findFirst().orElse(null);
            User warehouseUser = users.stream().filter(u -> u.getUsername().equals("warehouse")).findFirst().orElse(null);

            Task t1 = new Task();
            t1.setTitle("Inventory Count - Electronics");
            t1.setDescription("Count items in the Electronics section.");
            t1.setAssignedUser(warehouseUser);
            t1.setStatus(TaskStatus.PENDING);
            t1.setPriority(TaskPriority.HIGH);
            t1.setDueDate(LocalDateTime.now().plusDays(1));
            t1.setCreatedAt(LocalDateTime.now());

            Task t2 = new Task();
            t2.setTitle("Restock Office Supplies");
            t2.setDescription("Check and restock office supplies in storage.");
            t2.setAssignedUser(warehouseUser);
            t2.setStatus(TaskStatus.IN_PROGRESS);
            t2.setPriority(TaskPriority.MEDIUM);
            t2.setDueDate(LocalDateTime.now().plusDays(2));
            t2.setCreatedAt(LocalDateTime.now());

            Task t3 = new Task();
            t3.setTitle("Inspect Damaged Goods");
            t3.setDescription("Inspect and report damaged goods from the last shipment.");
            t3.setAssignedUser(warehouseUser);
            t3.setStatus(TaskStatus.COMPLETED);
            t3.setPriority(TaskPriority.LOW);
            t3.setDueDate(LocalDateTime.now().plusDays(3));
            t3.setCreatedAt(LocalDateTime.now());

            Task t4 = new Task();
            t4.setTitle("Prepare Shipment");
            t4.setDescription("Prepare shipment for dispatch and verify all items.");
            t4.setAssignedUser(manager);
            t4.setStatus(TaskStatus.CANCELED);
            t4.setPriority(TaskPriority.MEDIUM);
            t4.setDueDate(LocalDateTime.now().plusDays(1));
            t4.setCreatedAt(LocalDateTime.now());

            Task t5 = new Task();
            t5.setTitle("Update Inventory Records");
            t5.setDescription("Review and update inventory records for accuracy.");
            t5.setAssignedUser(warehouseUser);
            t5.setStatus(TaskStatus.EXPIRED);
            t5.setPriority(TaskPriority.HIGH);
            t5.setDueDate(LocalDateTime.now().minusDays(1));
            t5.setCreatedAt(LocalDateTime.now().minusDays(2));

            Task t6 = new Task();
            t6.setTitle("Check Low Stock Items");
            t6.setDescription("Review products with low stock levels and notify the manager.");
            t6.setAssignedUser(admin);
            t6.setStatus(TaskStatus.PENDING);
            t6.setPriority(TaskPriority.LOW);
            t6.setDueDate(LocalDateTime.now().plusDays(4));
            t6.setCreatedAt(LocalDateTime.now());


            Task t7 = new Task();
            t7.setTitle("Inventory Year-End Count");
            t7.setDescription("Perform a complete inventory count of all electronics items.");
            t7.setAssignedUser(warehouseUser);
            t7.setStatus(TaskStatus.PENDING);
            t7.setPriority(TaskPriority.HIGH);
            t7.setDueDate(LocalDateTime.now().plusDays(7));
            t7.setCreatedAt(LocalDateTime.now());

            Task t8 = new Task();
            t8.setTitle("Equipment Maintenance");
            t8.setDescription("Schedule regular maintenance for warehouse equipment.");
            t8.setAssignedUser(manager);
            t8.setStatus(TaskStatus.PENDING);
            t8.setPriority(TaskPriority.MEDIUM);
            t8.setDueDate(LocalDateTime.now().plusDays(14));
            t8.setCreatedAt(LocalDateTime.now());

            taskRepository.saveAll(List.of(t1, t2, t3, t4, t5, t6, t7, t8));
        }
    }

    private Map<PermissionType, Permission> initPermissions() {
        if (permissionRepository.count() == 0) {
            log.info("Creating default permissions");
            List<Permission> permissionsList = Arrays.asList(
                    createPermission(PermissionType.INVENTORY_CREATE, "Создание инвентаризации"),
                    createPermission(PermissionType.INVENTORY_READ, "Просмотр инвентаризаций"),
                    createPermission(PermissionType.INVENTORY_UPDATE, "Обновление инвентаризаций"),
                    createPermission(PermissionType.PRODUCT_CREATE, "Создание товаров"),
                    createPermission(PermissionType.PRODUCT_READ, "Просмотр товаров"),
                    createPermission(PermissionType.PRODUCT_UPDATE, "Обновление товаров"),
                    createPermission(PermissionType.TRANSACTION_CREATE, "Создание операций"),
                    createPermission(PermissionType.TRANSACTION_READ, "Просмотр операций")
            );
            permissionRepository.saveAll(permissionsList);
            return permissionsList.stream()
                    .collect(Collectors.toMap(Permission::getName, permission -> permission));
        } else {
            log.info("Permissions already exist");
            return permissionRepository.findAll().stream()
                    .collect(Collectors.toMap(Permission::getName, permission -> permission));
        }
    }

    private Permission createPermission(PermissionType name, String description) {
        Permission permission = new Permission();
        permission.setName(name);
        permission.setDescription(description);
        return permission;
    }

    private Map<RoleType, Role> initRoles(Map<PermissionType, Permission> permissions) {
        if (roleRepository.count() == 0) {
            log.info("Creating default roles");
            Role adminRole = new Role();
            adminRole.setRole(RoleType.ADMIN);
            adminRole.setDescription("Администратор системы");
            adminRole.setPermissions(new ArrayList<>(permissions.values()));

            Role managerRole = new Role();
            managerRole.setRole(RoleType.MANAGER);
            managerRole.setDescription("Менеджер склада");
            List<Permission> managerPermissions = new ArrayList<>(Arrays.asList(
                    permissions.get(PermissionType.INVENTORY_CREATE),
                    permissions.get(PermissionType.INVENTORY_READ),
                    permissions.get(PermissionType.INVENTORY_UPDATE),
                    permissions.get(PermissionType.PRODUCT_CREATE),
                    permissions.get(PermissionType.TRANSACTION_CREATE),
                    permissions.get(PermissionType.TRANSACTION_READ)
            ));
            managerRole.setPermissions(managerPermissions);

            Role warehouseRole = new Role();
            warehouseRole.setRole(RoleType.WAREHOUSE_WORKER);
            warehouseRole.setDescription("Работник склада");
            List<Permission> warehousePermissions = new ArrayList<>(Arrays.asList(
                    permissions.get(PermissionType.INVENTORY_READ),
                    permissions.get(PermissionType.PRODUCT_READ),
                    permissions.get(PermissionType.TRANSACTION_READ)
            ));
            warehouseRole.setPermissions(warehousePermissions);

            roleRepository.saveAll(Arrays.asList(adminRole, managerRole, warehouseRole));

            return Map.of(
                    RoleType.ADMIN, adminRole,
                    RoleType.MANAGER, managerRole,
                    RoleType.WAREHOUSE_WORKER, warehouseRole
            );
        } else {
            log.info("Roles already exist");
            return roleRepository.findAll().stream()
                    .collect(Collectors.toMap(Role::getRole, role -> role));
        }
    }

    private void initUsers(Map<RoleType, Role> roles) {
        if (userRepository.count() == 0) {
            log.info("Creating default users");
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFirstName("Администратор");
            admin.setLastName("Системы");
            admin.setEmail("admin@warehouse.local");
            admin.setPhone("+7 (900) 123-45-67");
            admin.setIsActive(true);
            admin.setLastLogin(LocalDateTime.parse("2024-02-15T08:30:00"));
            admin.setRoles(new ArrayList<>(List.of(roles.get(RoleType.ADMIN))));

            User manager = new User();
            manager.setUsername("manager");
            manager.setPassword(passwordEncoder.encode("manager123"));
            manager.setFirstName("Дмитрий");
            manager.setLastName("Менеджеров");
            manager.setEmail("manager@warehouse.local");
            manager.setPhone("+7 (900) 234-56-78");
            manager.setIsActive(true);
            manager.setLastLogin(LocalDateTime.parse("2024-02-15T09:15:00"));
            manager.setRoles(new ArrayList<>(List.of(roles.get(RoleType.MANAGER))));

            User warehouseUser = new User();
            warehouseUser.setUsername("warehouse");
            warehouseUser.setPassword(passwordEncoder.encode("warehouse123"));
            warehouseUser.setFirstName("Евгений");
            warehouseUser.setLastName("Складов");
            warehouseUser.setEmail("warehouse@warehouse.local");
            warehouseUser.setPhone("+7 (900) 345-67-89");
            warehouseUser.setIsActive(true);
            warehouseUser.setLastLogin(LocalDateTime.parse("2024-02-15T08:45:00"));
            warehouseUser.setRoles(new ArrayList<>(List.of(roles.get(RoleType.WAREHOUSE_WORKER))));

            userRepository.saveAll(Arrays.asList(admin, manager, warehouseUser));
        }
    }

    private void initSuppliers() {
        if (supplierRepository.count() == 0) {
            log.info("Creating default suppliers");
            List<Supplier> suppliers = Arrays.asList(
                    createSupplier("ТехноИмпорт ООО", "Иванов Иван Иванович",
                            "+7 (495) 123-45-67", "info@technoimport.ru",
                            "г. Москва, ул. Поставщиков, д. 15", "7712345678"),
                    createSupplier("ЭлектроТрейд ЗАО", "Петров Петр Петрович",
                            "+7 (495) 987-65-43", "sales@electrotrade.ru",
                            "г. Санкт-Петербург, пр. Энергетиков, д. 10", "7809876543"),
                    createSupplier("ТехноИмпорт ООО", "Иванов Иван Иванович",
                            "+7 (495) 123-45-67", "info@technoimport.ru",
                            "г. Москва, ул. Поставщиков, д. 15", "7712345678"),
                    createSupplier("ЭлектроТрейд ЗАО", "Петров Петр Петрович",
                            "+7 (495) 987-65-43", "sales@electrotrade.ru",
                            "г. Санкт-Петербург, пр. Энергетиков, д. 10", "7809876543"),
                    createSupplier("ТехноПрогресс ООО", "Сидоров Алексей Петрович",
                            "+7 (495) 555-44-33", "orders@technoprogress.ru",
                            "г. Екатеринбург, ул. Инновационная, д. 42", "6603456789"),
                    createSupplier("ГаджетМир ИП", "Козлова Елена Игоревна",
                            "+7 (499) 222-33-44", "info@gadgetmir.ru",
                            "г. Новосибирск, ул. Техническая, д. 15", "5401234567")
            );
            supplierRepository.saveAll(suppliers);
        }
    }

    private Supplier createSupplier(String name, String contactPerson, String phone,
                                    String email, String address, String taxId) {
        Supplier supplier = new Supplier();
        supplier.setName(name);
        supplier.setContactPerson(contactPerson);
        supplier.setPhone(phone);
        supplier.setEmail(email);
        supplier.setAddress(address);
        supplier.setTaxId(taxId);
        supplier.setIsActive(true);
        return supplier;
    }

    private void initCustomers() {
        if (customerRepository.count() == 0) {
            log.info("Creating default customers");
            List<Customer> customers = Arrays.asList(
                    createCustomer("МегаМаркет ООО", "Сидоров Алексей Владимирович",
                            "+7 (495) 111-22-33", "orders@megamarket.ru",
                            "г. Москва, ул. Торговая, д. 100", "7712345679"),
                    createCustomer("ТехноШоп ООО", "Козлова Мария Игоревна",
                            "+7 (495) 444-55-66", "procurement@technoshop.ru",
                            "г. Москва, ул. Магазинная, д. 50", "7712345680")
            );
            customerRepository.saveAll(customers);
        }
    }

    private Customer createCustomer(String name, String contactPerson, String phone,
                                    String email, String address, String taxId) {
        Customer customer = new Customer();
        customer.setName(name);
        customer.setContactPerson(contactPerson);
        customer.setPhone(phone);
        customer.setEmail(email);
        customer.setAddress(address);
        customer.setTaxId(taxId);
        customer.setIsActive(true);
        return customer;
    }

    // ------------------ Методы инициализации остальных сущностей ------------------

    private Map<String, Category> initCategories() {
        Map<String, Category> categories = new HashMap<>();
        if (categoryRepository.count() == 0) {
            log.info("Creating default categories");
            // Корневые категории
            Category electronics = new Category();
            electronics.setName("Электроника");
            electronics.setDescription("Электронные устройства и комплектующие");
            electronics = categoryRepository.save(electronics);
            categories.put(electronics.getName(), electronics);

            Category household = new Category();
            household.setName("Бытовая техника");
            household.setDescription("Техника для дома");
            household = categoryRepository.save(household);
            categories.put(household.getName(), household);

            // Подкатегории
            Category smartphones = new Category();
            smartphones.setName("Смартфоны");
            smartphones.setDescription("Мобильные телефоны и смартфоны");
            smartphones.setParent(electronics);
            smartphones = categoryRepository.save(smartphones);
            categories.put(smartphones.getName(), smartphones);

            Category notebooks = new Category();
            notebooks.setName("Ноутбуки");
            notebooks.setDescription("Портативные компьютеры");
            notebooks.setParent(electronics);
            notebooks = categoryRepository.save(notebooks);
            categories.put(notebooks.getName(), notebooks);

            Category refrigerators = new Category();
            refrigerators.setName("Холодильники");
            refrigerators.setDescription("Холодильное оборудование");
            refrigerators.setParent(household);
            refrigerators = categoryRepository.save(refrigerators);
            categories.put(refrigerators.getName(), refrigerators);
        } else {
            log.info("Categories already exist");
            categoryRepository.findAll().forEach(cat -> categories.put(cat.getName(), cat));
        }
        return categories;
    }

    private void initProducts(Map<String, Category> categories) {
        if (productRepository.count() == 0) {
            log.info("Creating default products");
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

            Product p1 = new Product();
            p1.setName("iPhone 14 Pro 256GB");
            p1.setDescription("Смартфон Apple iPhone 14 Pro 256GB, черный");
            p1.setSku("APL-IP14P-256-BLK");
            p1.setBarcode("123456789012");
            p1.setCategory(categories.get("Смартфоны"));
            p1.setUnitOfMeasure("шт");
            p1.setMinStockLevel(8.0);
            p1.setImageUrl("/images/products/iphone14pro.jpg");
            p1.setWeight(0.24);
            p1.setVolume(0.0005);
            p1.setIsActive(true);
            p1.setCreatedAt(LocalDateTime.parse("2024-01-15T10:30:00", formatter));
            p1.setUpdatedAt(LocalDateTime.parse("2024-01-15T10:30:00", formatter));
            p1.setPrice(500000); 

            Product p2 = new Product();
            p2.setName("Samsung Galaxy S23 Ultra 512GB");
            p2.setDescription("Смартфон Samsung Galaxy S23 Ultra 512GB, серый");
            p2.setSku("SMS-S23U-512-GRY");
            p2.setBarcode("123456789013");
            p2.setCategory(categories.get("Смартфоны"));
            p2.setUnitOfMeasure("шт");
            p2.setMinStockLevel(5.0);
            p2.setImageUrl("/images/products/s23ultra.jpg");
            p2.setWeight(0.23);
            p2.setVolume(0.0005);
            p2.setIsActive(true);
            p2.setCreatedAt(LocalDateTime.parse("2024-01-15T11:00:00", formatter));
            p2.setUpdatedAt(LocalDateTime.parse("2024-01-15T11:00:00", formatter));
            p2.setPrice(450000);

            Product p3 = new Product();
            p3.setName("MacBook Pro 16\" M3 1TB");
            p3.setDescription("Ноутбук Apple MacBook Pro 16\" M3 1TB, серебристый");
            p3.setSku("APL-MBP16-M3-1TB-SLV");
            p3.setBarcode("123456789014");
            p3.setCategory(categories.get("Ноутбуки"));
            p3.setUnitOfMeasure("шт");
            p3.setMinStockLevel(10.0);
            p3.setImageUrl("/images/products/macbookpro16.jpg");
            p3.setWeight(2.15);
            p3.setVolume(0.003);
            p3.setIsActive(true);
            p3.setCreatedAt(LocalDateTime.parse("2024-01-16T09:45:00", formatter));
            p3.setUpdatedAt(LocalDateTime.parse("2024-01-16T09:45:00", formatter));
            p3.setPrice(1200000);

            Product p4 = new Product();
            p4.setName("LG Side-by-Side RS-26FTIH");
            p4.setDescription("Холодильник LG Side-by-Side RS-26FTIH, нержавеющая сталь");
            p4.setSku("LG-RS26FTIH-SS");
            p4.setBarcode("123456789015");
            p4.setCategory(categories.get("Холодильники"));
            p4.setUnitOfMeasure("шт");
            p4.setMinStockLevel(2.0);
            p4.setImageUrl("/images/products/lg-rs26ftih.jpg");
            p4.setWeight(110.0);
            p4.setVolume(0.75);
            p4.setIsActive(true);
            p4.setCreatedAt(LocalDateTime.parse("2024-01-17T14:20:00", formatter));
            p4.setUpdatedAt(LocalDateTime.parse("2024-01-17T14:20:00", formatter));
            p4.setPrice(350000);

            Product p5 = new Product();
            p5.setName("Dell XPS 15 i9 32GB 1TB");
            p5.setDescription("Ноутбук Dell XPS 15, Intel Core i9, 32GB RAM, 1TB SSD");
            p5.setSku("DLL-XPS15-I9-32-1TB");
            p5.setBarcode("123456789016");
            p5.setCategory(categories.get("Ноутбуки"));
            p5.setUnitOfMeasure("шт");
            p5.setMinStockLevel(3.0);
            p5.setImageUrl("/images/products/dell-xps15.jpg");
            p5.setWeight(1.8);
            p5.setVolume(0.0025);
            p5.setIsActive(true);
            p5.setCreatedAt(LocalDateTime.parse("2024-01-18T13:45:00", formatter));
            p5.setUpdatedAt(LocalDateTime.parse("2024-01-18T13:45:00", formatter));
            p5.setPrice(950000);

            Product p6 = new Product();
            p6.setName("Sony WH-1000XM5");
            p6.setDescription("Беспроводные наушники Sony WH-1000XM5 с шумоподавлением");
            p6.setSku("SNY-WH1000XM5-BLK");
            p6.setBarcode("123456789017");
            p6.setCategory(categories.get("Электроника"));
            p6.setUnitOfMeasure("шт");
            p6.setMinStockLevel(5.0);
            p6.setImageUrl("/images/products/sony-wh1000xm5.jpg");
            p6.setWeight(0.25);
            p6.setVolume(0.0007);
            p6.setIsActive(true);
            p6.setCreatedAt(LocalDateTime.parse("2024-01-19T10:30:00", formatter));
            p6.setUpdatedAt(LocalDateTime.parse("2024-01-19T10:30:00", formatter));
            p6.setPrice(85000);

            productRepository.saveAll(Arrays.asList(p1, p2, p3, p4, p5, p6));
        }
    }

    private void initWarehouses() {
        if (warehouseRepository.count() == 0) {
            log.info("Creating default warehouses");
            Warehouse w1 = new Warehouse();
            w1.setName("Основной склад");
            w1.setDescription("Центральный склад компании");
            w1.setAddress("г. Москва, ул. Складская, д. 10");
            w1.setIsActive(true);

            Warehouse w2 = new Warehouse();
            w2.setName("Склад электроники");
            w2.setDescription("Специализированный склад для электроники");
            w2.setAddress("г. Москва, ул. Технологическая, д. 5");
            w2.setIsActive(true);

            warehouseRepository.saveAll(Arrays.asList(w1, w2));
        }
    }

    private Map<String, Warehouse> getWarehousesMap() {
        Map<String, Warehouse> map = new HashMap<>();
        warehouseRepository.findAll().forEach(w -> map.put(w.getName(), w));
        return map;
    }

    private void initLocations(Map<String, Warehouse> warehouses) {
        if (locationRepository.count() == 0) {
            log.info("Creating default locations");
            Warehouse mainWarehouse = warehouses.get("Основной склад");
            Warehouse elecWarehouse = warehouses.get("Склад электроники");

            Location l1 = new Location();
            l1.setCode("A-01-01");
            l1.setDescription("Стеллаж A, секция 01, полка 01");
            l1.setWarehouse(mainWarehouse);
            l1.setMaxWeight(500.0);
            l1.setMaxVolume(1.5);

            Location l2 = new Location();
            l2.setCode("A-01-02");
            l2.setDescription("Стеллаж A, секция 01, полка 02");
            l2.setWarehouse(mainWarehouse);
            l2.setMaxWeight(500.0);
            l2.setMaxVolume(1.5);

            Location l3 = new Location();
            l3.setCode("B-02-01");
            l3.setDescription("Стеллаж B, секция 02, полка 01");
            l3.setWarehouse(elecWarehouse);
            l3.setMaxWeight(300.0);
            l3.setMaxVolume(1.0);

            Location l4 = new Location();
            l4.setCode("B-02-02");
            l4.setDescription("Стеллаж B, секция 02, полка 02");
            l4.setWarehouse(elecWarehouse);
            l4.setMaxWeight(300.0);
            l4.setMaxVolume(1.0);

            locationRepository.saveAll(Arrays.asList(l1, l2, l3, l4));
        }
    }

    private Map<String, Location> getLocationsMap() {
        Map<String, Location> map = new HashMap<>();
        locationRepository.findAll().forEach(l -> map.put(l.getCode(), l));
        return map;
    }

    private void initInventoryItems(Map<String, Location> locations) {
        if (inventoryItemRepository.count() == 0) {
            log.info("Creating default inventory items");
            // Получаем товары по их sku, так как они были созданы в initProducts()
            List<Product> products = productRepository.findAll();
            Map<String, Product> productMap = products.stream()
                    .collect(Collectors.toMap(Product::getSku, p -> p));

            InventoryItem item1 = new InventoryItem();
            item1.setProduct(productMap.get("APL-IP14P-256-BLK"));
            item1.setLocation(locations.get("B-02-01"));
            item1.setQuantity(15.0);
            item1.setBatchNumber("APL-2024-0123");
            item1.setExpirationDate(null);
            item1.setLastInventoryDate(LocalDateTime.parse("2024-02-01T10:00:00"));

            InventoryItem item2 = new InventoryItem();
            item2.setProduct(productMap.get("SMS-S23U-512-GRY"));
            item2.setLocation(locations.get("B-02-01"));
            item2.setQuantity(8.0);
            item2.setBatchNumber("SMS-2024-0056");
            item2.setExpirationDate(null);
            item2.setLastInventoryDate(LocalDateTime.parse("2024-02-01T10:20:00"));

            InventoryItem item3 = new InventoryItem();
            item3.setProduct(productMap.get("APL-MBP16-M3-1TB-SLV"));
            item3.setLocation(locations.get("B-02-02"));
            item3.setQuantity(5.0);
            item3.setBatchNumber("APL-2024-0078");
            item3.setExpirationDate(null);
            item3.setLastInventoryDate(LocalDateTime.parse("2024-02-01T11:05:00"));

            InventoryItem item4 = new InventoryItem();
            item4.setProduct(productMap.get("LG-RS26FTIH-SS"));
            item4.setLocation(locations.get("A-01-01"));
            item4.setQuantity(3.0);
            item4.setBatchNumber("LG-2024-0012");
            item4.setExpirationDate(null);
            item4.setLastInventoryDate(LocalDateTime.parse("2024-02-02T09:30:00"));

            // Дополнительные позиции
            InventoryItem item5 = new InventoryItem();
            item5.setProduct(productMap.get("DLL-XPS15-I9-32-1TB"));
            item5.setLocation(locations.get("A-01-02"));
            item5.setQuantity(2.0);
            item5.setBatchNumber("DLL-2024-0034");
            item5.setExpirationDate(null);
            item5.setLastInventoryDate(LocalDateTime.parse("2024-02-15T11:15:00"));

            InventoryItem item6 = new InventoryItem();
            item6.setProduct(productMap.get("SNY-WH1000XM5-BLK"));
            item6.setLocation(locations.get("B-02-02"));
            item6.setQuantity(10.0);
            item6.setBatchNumber("SNY-2024-0045");
            item6.setExpirationDate(null);
            item6.setLastInventoryDate(null); // Еще не проверялось

            inventoryItemRepository.saveAll(Arrays.asList(item1, item2, item3, item4, item5, item6));
        }
    }

    private void initInventoryTransactions(Map<String, Location> locations) {
        if (inventoryTransactionRepository.count() == 0) {
            log.info("Creating default inventory transactions");
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

            Map<String, Product> productMap = productRepository.findAll().stream()
                    .collect(Collectors.toMap(Product::getSku, p -> p));
            List<Supplier> suppliers = supplierRepository.findAll();
            List<Customer> customers = customerRepository.findAll();
            List<User> users = userRepository.findAll();
            // Выбираем пользователей по username
            User manager = users.stream().filter(u -> u.getUsername().equals("manager")).findFirst().orElse(null);
            User admin = users.stream().filter(u -> u.getUsername().equals("admin")).findFirst().orElse(null);

            // Приемка товаров
            InventoryTransaction t1 = new InventoryTransaction();
            t1.setTransactionType(TransactionType.RECEIVING);
            t1.setProduct(productMap.get("APL-IP14P-256-BLK"));
            t1.setDestinationLocation(locations.get("B-02-01"));
            t1.setQuantity(20.0);
            t1.setBatchNumber("APL-2024-0123");
            t1.setDocumentNumber("ПН-2024-0001");
            t1.setSupplier(suppliers.get(0));
            t1.setUser(manager);
            t1.setTransactionDate(LocalDateTime.parse("2024-01-20T11:30:00", formatter));
            t1.setNotes("Первая поставка iPhone 14 Pro");

            InventoryTransaction t2 = new InventoryTransaction();
            t2.setTransactionType(TransactionType.RECEIVING);
            t2.setProduct(productMap.get("SMS-S23U-512-GRY"));
            t2.setDestinationLocation(locations.get("B-02-01"));
            t2.setQuantity(10.0);
            t2.setBatchNumber("SMS-2024-0056");
            t2.setDocumentNumber("ПН-2024-0002");
            t2.setSupplier(suppliers.get(1));
            t2.setUser(manager);
            t2.setTransactionDate(LocalDateTime.parse("2024-01-21T10:15:00", formatter));
            t2.setNotes("Поставка Samsung Galaxy S23 Ultra");

            // Отгрузка товаров
            InventoryTransaction t3 = new InventoryTransaction();
            t3.setTransactionType(TransactionType.SHIPPING);
            t3.setProduct(productMap.get("APL-IP14P-256-BLK"));
            t3.setSourceLocation(locations.get("B-02-01"));
            t3.setQuantity(5.0);
            t3.setBatchNumber("APL-2024-0123");
            t3.setDocumentNumber("РН-2024-0001");
            t3.setCustomer(customers.get(0));
            t3.setUser(manager);
            t3.setTransactionDate(LocalDateTime.parse("2024-01-25T14:20:00", formatter));
            t3.setNotes("Отгрузка iPhone для МегаМаркет");

            // Приемка товаров
            InventoryTransaction t4 = new InventoryTransaction();
            t4.setTransactionType(TransactionType.RECEIVING);
            t4.setProduct(productMap.get("APL-MBP16-M3-1TB-SLV"));
            t4.setDestinationLocation(locations.get("B-02-02"));
            t4.setQuantity(5.0);
            t4.setBatchNumber("APL-2024-0078");
            t4.setDocumentNumber("ПН-2024-0003");
            t4.setSupplier(suppliers.get(0));
            t4.setUser(manager);
            t4.setTransactionDate(LocalDateTime.parse("2024-01-26T09:45:00", formatter));
            t4.setNotes("Поставка MacBook Pro");

            // Отгрузка товаров
            InventoryTransaction t5 = new InventoryTransaction();
            t5.setTransactionType(TransactionType.SHIPPING);
            t5.setProduct(productMap.get("SMS-S23U-512-GRY"));
            t5.setSourceLocation(locations.get("B-02-01"));
            t5.setQuantity(2.0);
            t5.setBatchNumber("SMS-2024-0056");
            t5.setDocumentNumber("РН-2024-0002");
            t5.setCustomer(customers.get(1));
            t5.setUser(manager);
            t5.setTransactionDate(LocalDateTime.parse("2024-01-28T16:10:00", formatter));
            t5.setNotes("Отгрузка Samsung для ТехноШоп");

            // Перемещение товаров
            InventoryTransaction t6 = new InventoryTransaction();
            t6.setTransactionType(TransactionType.TRANSFER);
            t6.setProduct(productMap.get("APL-IP14P-256-BLK"));
            t6.setSourceLocation(locations.get("B-02-01"));
            t6.setDestinationLocation(locations.get("A-01-02"));
            t6.setQuantity(3.0);
            t6.setBatchNumber("APL-2024-0123");
            t6.setDocumentNumber("ПЕР-2024-0001");
            t6.setUser(manager);
            t6.setTransactionDate(LocalDateTime.parse("2024-01-30T11:25:00", formatter));
            t6.setNotes("Перемещение iPhone на другой склад");

            // Корректировка товаров
            InventoryTransaction t7 = new InventoryTransaction();
            t7.setTransactionType(TransactionType.ADJUSTMENT);
            t7.setProduct(productMap.get("SMS-S23U-512-GRY"));
            t7.setSourceLocation(locations.get("B-02-01"));
            t7.setQuantity(1.0);
            t7.setBatchNumber("SMS-2024-0056");
            t7.setDocumentNumber("КОР-2024-0001");
            t7.setUser(manager);
            t7.setTransactionDate(LocalDateTime.parse("2024-02-02T09:30:00", formatter));
            t7.setNotes("Корректировка после инвентаризации");

            // Дополнительные транзакции для новых товаров
            // Приемка наушников Sony
            InventoryTransaction t8 = new InventoryTransaction();
            t8.setTransactionType(TransactionType.RECEIVING);
            t8.setProduct(productMap.get("SNY-WH1000XM5-BLK"));
            t8.setDestinationLocation(locations.get("B-02-02"));
            t8.setQuantity(12.0);
            t8.setBatchNumber("SNY-2024-0045");
            t8.setDocumentNumber("ПН-2024-0004");
            t8.setSupplier(suppliers.get(2));
            t8.setUser(admin);
            t8.setTransactionDate(LocalDateTime.parse("2024-02-05T13:10:00", formatter));
            t8.setNotes("Поставка наушников Sony");

            // Отгрузка наушников Sony
            InventoryTransaction t9 = new InventoryTransaction();
            t9.setTransactionType(TransactionType.SHIPPING);
            t9.setProduct(productMap.get("SNY-WH1000XM5-BLK"));
            t9.setSourceLocation(locations.get("B-02-02"));
            t9.setQuantity(2.0);
            t9.setBatchNumber("SNY-2024-0045");
            t9.setDocumentNumber("РН-2024-0003");
            t9.setCustomer(customers.get(0));
            t9.setUser(manager);
            t9.setTransactionDate(LocalDateTime.parse("2024-02-10T11:30:00", formatter));
            t9.setNotes("Отгрузка наушников для МегаМаркет");

            // Приемка ноутбуков Dell
            InventoryTransaction t10 = new InventoryTransaction();
            t10.setTransactionType(TransactionType.RECEIVING);
            t10.setProduct(productMap.get("DLL-XPS15-I9-32-1TB"));
            t10.setDestinationLocation(locations.get("A-01-02"));
            t10.setQuantity(2.0);
            t10.setBatchNumber("DLL-2024-0034");
            t10.setDocumentNumber("ПН-2024-0005");
            t10.setSupplier(suppliers.get(3));
            t10.setUser(admin);
            t10.setTransactionDate(LocalDateTime.parse("2024-02-12T09:45:00", formatter));
            t10.setNotes("Поставка ноутбуков Dell");

            inventoryTransactionRepository.saveAll(Arrays.asList(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10));
        }
    }

    private void initInventories(Map<String, Warehouse> warehouses) {
        if (inventoryRepository.count() == 0) {
            log.info("Creating default inventories");
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

            // Выбираем склады
            Warehouse elecWarehouse = warehouses.get("Склад электроники");
            Warehouse mainWarehouse = warehouses.get("Основной склад");

            User manager = userRepository.findFirstByUsername("manager").orElse(null);
            User admin = userRepository.findFirstByUsername("admin").orElse(null);

            Inventory inv1 = new Inventory();
            inv1.setInventoryNumber("ИНВ-2024-001");
            inv1.setStartDate(LocalDateTime.parse("2024-02-01T09:00:00", formatter));
            inv1.setEndDate(LocalDateTime.parse("2024-02-01T17:00:00", formatter));
            inv1.setStatus(InventoryStatus.COMPLETED);
            inv1.setWarehouse(elecWarehouse);
            inv1.setCreatedBy(manager);

            Inventory inv2 = new Inventory();
            inv2.setInventoryNumber("ИНВ-2024-002");
            inv2.setStartDate(LocalDateTime.parse("2024-02-15T10:00:00", formatter));
            inv2.setStatus(InventoryStatus.IN_PROGRESS);
            inv2.setWarehouse(mainWarehouse);
            inv2.setCreatedBy(admin);

            // Третья инвентаризация (запланированная)
            Inventory inv3 = new Inventory();
            inv3.setInventoryNumber("ИНВ-2024-003");
            inv3.setStartDate(LocalDateTime.parse("2024-03-01T09:00:00", formatter));
            inv3.setStatus(InventoryStatus.PLANNED);
            inv3.setWarehouse(elecWarehouse);
            inv3.setCreatedBy(manager);

            inventoryRepository.saveAll(Arrays.asList(inv1, inv2, inv3));
        }
    }
    private void initInventoryCounts() {
        if (inventoryCountRepository.count() == 0) {
            log.info("Creating default inventory counts");
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

            // Получаем все инвентаризации
            List<Inventory> inventories = inventoryRepository.findAll();
            Map<String, Inventory> inventoryMap = inventories.stream()
                    .collect(Collectors.toMap(Inventory::getInventoryNumber, i -> i));

            // Получаем все продукты и локации
            List<Product> products = productRepository.findAll();
            Map<String, Product> productMap = products.stream()
                    .collect(Collectors.toMap(Product::getSku, p -> p));
            Map<String, Location> locations = getLocationsMap();

            // Получаем пользователей
            User warehouseUser = userRepository.findByUsername("warehouse").orElse(null);
            User manager = userRepository.findByUsername("manager").orElse(null);

            // Инвентаризация 1 (завершенная)
            InventoryCount count1 = new InventoryCount();
            count1.setInventory(inventoryMap.get("ИНВ-2024-001"));
            count1.setProduct(productMap.get("APL-IP14P-256-BLK"));
            count1.setLocation(locations.get("B-02-01"));
            count1.setExpectedQuantity(15.0);
            count1.setActualQuantity(15.0);
            count1.setBatchNumber("APL-2024-0123");
            count1.setCountDate(LocalDateTime.parse("2024-02-01T10:00:00", formatter));
            count1.setCountedBy(warehouseUser);
            count1.setNotes("Соответствует учетным данным");

            InventoryCount count2 = new InventoryCount();
            count2.setInventory(inventoryMap.get("ИНВ-2024-001"));
            count2.setProduct(productMap.get("SMS-S23U-512-GRY"));
            count2.setLocation(locations.get("B-02-01"));
            count2.setExpectedQuantity(8.0);
            count2.setActualQuantity(8.0);
            count2.setBatchNumber("SMS-2024-0056");
            count2.setCountDate(LocalDateTime.parse("2024-02-01T10:20:00", formatter));
            count2.setCountedBy(warehouseUser);
            count2.setNotes("Соответствует учетным данным");

            InventoryCount count3 = new InventoryCount();
            count3.setInventory(inventoryMap.get("ИНВ-2024-001"));
            count3.setProduct(productMap.get("APL-MBP16-M3-1TB-SLV"));
            count3.setLocation(locations.get("B-02-02"));
            count3.setExpectedQuantity(5.0);
            count3.setActualQuantity(5.0);
            count3.setBatchNumber("APL-2024-0078");
            count3.setCountDate(LocalDateTime.parse("2024-02-01T11:05:00", formatter));
            count3.setCountedBy(warehouseUser);
            count3.setNotes("Соответствует учетным данным");

            // Инвентаризация 2 (в процессе)
            InventoryCount count4 = new InventoryCount();
            count4.setInventory(inventoryMap.get("ИНВ-2024-002"));
            count4.setProduct(productMap.get("LG-RS26FTIH-SS"));
            count4.setLocation(locations.get("A-01-01"));
            count4.setExpectedQuantity(3.0);
            count4.setActualQuantity(2.0);
            count4.setBatchNumber("LG-2024-0012");
            count4.setCountDate(LocalDateTime.parse("2024-02-15T10:30:00", formatter));
            count4.setCountedBy(manager);
            count4.setNotes("Обнаружена недостача 1 шт.");

            InventoryCount count5 = new InventoryCount();
            count5.setInventory(inventoryMap.get("ИНВ-2024-002"));
            count5.setProduct(productMap.get("DLL-XPS15-I9-32-1TB"));
            count5.setLocation(locations.get("A-01-02"));
            count5.setExpectedQuantity(2.0);
            count5.setActualQuantity(3.0);
            count5.setBatchNumber("DLL-2024-0034");
            count5.setCountDate(LocalDateTime.parse("2024-02-15T11:15:00", formatter));
            count5.setCountedBy(manager);
            count5.setNotes("Обнаружен излишек 1 шт.");

            inventoryCountRepository.saveAll(Arrays.asList(count1, count2, count3, count4, count5));
        }
    }

    private void initAuditLogs() {
        if (auditLogRepository.count() == 0) {
            log.info("Creating default audit logs");
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            List<User> users = userRepository.findAll();
            User admin = users.stream().filter(u -> u.getUsername().equals("admin")).findFirst().orElse(null);
            User manager = users.stream().filter(u -> u.getUsername().equals("manager")).findFirst().orElse(null);

            AuditLog log1 = new AuditLog();
            log1.setUser(admin);
            log1.setActionType("LOGIN");
            log1.setEntityType("USER");
            log1.setEntityId(1L);
            log1.setActionDetails("Успешный вход в систему");
            log1.setIpAddress("192.168.1.100");
            log1.setActionTimestamp(LocalDateTime.parse("2024-02-15T08:30:00", formatter));

            AuditLog log2 = new AuditLog();
            log2.setUser(manager);
            log2.setActionType("LOGIN");
            log2.setEntityType("USER");
            log2.setEntityId(2L);
            log2.setActionDetails("Успешный вход в систему");
            log2.setIpAddress("192.168.1.101");
            log2.setActionTimestamp(LocalDateTime.parse("2024-02-15T09:15:00", formatter));

            AuditLog log3 = new AuditLog();
            log3.setUser(manager);
            log3.setActionType("CREATE");
            log3.setEntityType("INVENTORY");
            log3.setEntityId(1L);
            log3.setActionDetails("Создание инвентаризации #ИНВ-2024-001");
            log3.setIpAddress("192.168.1.101");
            log3.setActionTimestamp(LocalDateTime.parse("2024-02-01T08:45:00", formatter));

            AuditLog log4 = new AuditLog();
            log4.setUser(manager);
            log4.setActionType("UPDATE");
            log4.setEntityType("INVENTORY");
            log4.setEntityId(1L);
            log4.setActionDetails("Завершение инвентаризации #ИНВ-2024-001");
            log4.setIpAddress("192.168.1.101");
            log4.setActionTimestamp(LocalDateTime.parse("2024-02-01T17:05:00", formatter));

            auditLogRepository.saveAll(Arrays.asList(log1, log2, log3, log4));
        }
    }
}
