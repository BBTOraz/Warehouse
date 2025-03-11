package bbt.tao.warehouse.conf.init;

import bbt.tao.warehouse.model.Customer;
import bbt.tao.warehouse.model.Permission;
import bbt.tao.warehouse.model.Role;
import bbt.tao.warehouse.model.Supplier;
import bbt.tao.warehouse.model.User;
import bbt.tao.warehouse.model.enums.PermissionType;
import bbt.tao.warehouse.model.enums.RoleType;
import bbt.tao.warehouse.repository.CustomerRepository;
import bbt.tao.warehouse.repository.PermissionRepository;
import bbt.tao.warehouse.repository.RoleRepository;
import bbt.tao.warehouse.repository.SupplierRepository;
import bbt.tao.warehouse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final SupplierRepository supplierRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Initializing database with default data");
        Map<PermissionType, Permission> permissions = initPermissions();
        Map<RoleType, Role> roles = initRoles(permissions);
        initUsers(roles);
        initSuppliers();
        initCustomers();
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

    private Permission createPermission(PermissionType name,  String description) {
        Permission permission = new Permission();
        permission.setName(name);
        permission.setDescription(description);
        return permission;
    }

    private Map<RoleType, Role> initRoles(Map<PermissionType, Permission> permissions) {
        if (roleRepository.count() == 0) {
            log.info("Creating default roles");

            // Admin role with all permissions
            Role adminRole = new Role();
            adminRole.setRole(RoleType.ADMIN);
            adminRole.setDescription("Администратор системы");
            adminRole.setPermissions(new ArrayList<>(permissions.values()));

            // Manager role with selected permissions
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

            // Warehouse role with limited permissions
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

            // Admin user
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFirstName("Администратор");
            admin.setLastName("Системы");
            admin.setEmail("admin@warehouse.local");
            admin.setPhone("+7 (900) 123-45-67");
            admin.setIsActive(true);
            admin.setRoles(new ArrayList<>(List.of(roles.get(RoleType.ADMIN))));

            // Manager users
            User manager1 = new User();
            manager1.setUsername("manager");
            manager1.setPassword(passwordEncoder.encode("manager123"));
            manager1.setFirstName("Дмитрий");
            manager1.setLastName("Менеджеров");
            manager1.setEmail("manager@warehouse.local");
            manager1.setPhone("+7 (900) 234-56-78");
            manager1.setIsActive(true);
            manager1.setRoles(new ArrayList<>(List.of(roles.get(RoleType.MANAGER))));

            User manager2 = new User();
            manager2.setUsername("marina");
            manager2.setPassword(passwordEncoder.encode("marina123"));
            manager2.setFirstName("Марина");
            manager2.setLastName("Сергеева");
            manager2.setEmail("marina@warehouse.local");
            manager2.setPhone("+7 (900) 555-44-33");
            manager2.setIsActive(true);
            manager2.setRoles(new ArrayList<>(List.of(roles.get(RoleType.MANAGER))));

            // Warehouse users
            User warehouse1 = new User();
            warehouse1.setUsername("warehouse");
            warehouse1.setPassword(passwordEncoder.encode("warehouse123"));
            warehouse1.setFirstName("Евгений");
            warehouse1.setLastName("Складов");
            warehouse1.setEmail("warehouse@warehouse.local");
            warehouse1.setPhone("+7 (900) 345-67-89");
            warehouse1.setIsActive(true);
            warehouse1.setRoles(new ArrayList<>(List.of(roles.get(RoleType.WAREHOUSE_WORKER))));

            User warehouse2 = new User();
            warehouse2.setUsername("ivan");
            warehouse2.setPassword(passwordEncoder.encode("ivan123"));
            warehouse2.setFirstName("Иван");
            warehouse2.setLastName("Петров");
            warehouse2.setEmail("ivan@warehouse.local");
            warehouse2.setPhone("+7 (900) 777-88-99");
            warehouse2.setIsActive(true);
            warehouse2.setRoles(new ArrayList<>(List.of(roles.get(RoleType.WAREHOUSE_WORKER))));

            User warehouse3 = new User();
            warehouse3.setUsername("anna");
            warehouse3.setPassword(passwordEncoder.encode("anna123"));
            warehouse3.setFirstName("Анна");
            warehouse3.setLastName("Смирнова");
            warehouse3.setEmail("anna@warehouse.local");
            warehouse3.setPhone("+7 (900) 111-22-33");
            warehouse3.setIsActive(true);
            warehouse3.setRoles(new ArrayList<>(List.of(roles.get(RoleType.WAREHOUSE_WORKER))));

            userRepository.saveAll(Arrays.asList(admin, manager1, manager2, warehouse1, warehouse2, warehouse3));
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

                createSupplier("ГаджетСнаб ООО", "Сидоров Сергей Александрович",
                    "+7 (495) 555-66-77", "info@gadgetsnab.ru",
                    "г. Екатеринбург, ул. Гаджетная, д. 42", "6604567890"),

                createSupplier("Технологии Будущего АО", "Кузнецова Елена Владимировна",
                    "+7 (812) 333-44-55", "sales@futuretech.ru",
                    "г. Новосибирск, пр. Инноваций, д. 100", "5406789012"),

                createSupplier("Импорт-Экспорт ООО", "Николаев Дмитрий Сергеевич",
                    "+7 (495) 222-33-44", "contact@importexport.ru",
                    "г. Казань, ул. Торговая, д. 55", "1655123456")
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
                    "г. Москва, ул. Магазинная, д. 50", "7712345680"),

                createCustomer("Электроника+ ООО", "Соколов Игорь Петрович",
                    "+7 (812) 777-88-99", "purchase@electronika-plus.ru",
                    "г. Санкт-Петербург, ул. Техническая, д. 15", "7840123456"),

                createCustomer("Гаджеты и Технологии ООО", "Васильева Ольга Николаевна",
                    "+7 (343) 222-33-44", "info@gadget-tech.ru",
                    "г. Екатеринбург, пр. Цифровой, д. 25", "6670654321"),

                createCustomer("Умный Дом АО", "Макаров Антон Сергеевич",
                    "+7 (383) 555-66-77", "sales@smarthouse.ru",
                    "г. Новосибирск, ул. Умная, д. 10", "5406987654"),

                createCustomer("ТоргСеть ООО", "Федорова Елена Александровна",
                    "+7 (843) 666-77-88", "orders@torgset.ru",
                    "г. Казань, ул. Сетевая, д. 30", "1659876543")
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


}