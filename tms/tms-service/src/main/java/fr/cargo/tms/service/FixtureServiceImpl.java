package fr.cargo.tms.service;

import fr.cargo.tms.dao.bean.CustomsDocument;
import fr.cargo.tms.dao.bean.Goods;
import fr.cargo.tms.dao.bean.Movement;
import fr.cargo.tms.dao.bean.Warehouse;
import fr.cargo.tms.dao.bean.enums.MovementType;
import fr.cargo.tms.dao.bean.enums.RefType;
import fr.cargo.tms.dao.repository.MovementRepository;
import fr.cargo.tms.dao.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Profile({"dev","test"})
@RequiredArgsConstructor
@Slf4j
public class FixtureServiceImpl implements FixtureService {

    private final MovementRepository movementRepository;
    private final WarehouseRepository warehouseRepository;
    private final Faker faker = new Faker();

    @Override
    @Transactional
    public void cleanFixtures() {
        // ordre: enfants -> référentiels
        movementRepository.deleteAllInBatch();
        warehouseRepository.deleteAllInBatch();
        log.info("[fixtures] clean done.");
    }

    @Override
    @Transactional
    public void loadFixtures(int count) {
        int n = Math.max(1, Math.min(count, 500));
        ensureWarehouses();

        var allWh = warehouseRepository.findAll();
        var rnd = faker.random();
        var buffer = new java.util.ArrayList<Movement>(n);

        for (int i = 0; i < n; i++) {
            boolean out = faker.bool().bool();
            Warehouse declared = allWh.get(rnd.nextInt(allWh.size()));

            RefType type = faker.options().option(RefType.class);

            Movement.MovementBuilder<?,?> mb = Movement.builder()
                    .movementType(out ? MovementType.OUT : MovementType.IN)
                    .movementTime(
                            faker.date()
                                    .past(2, java.util.concurrent.TimeUnit.DAYS)
                                    .toInstant()
                                    .atOffset(java.time.ZoneOffset.UTC)
                    )
                    .createdBy(faker.internet().username())
                    .customsStatus(out ? "A" : "D")
                    .declaredIn(declared)
                    .goods(Goods.builder()
                            .refType(type)
                            .refCode(generateRefCode(type))
                            .quantity((double) faker.number().numberBetween(1, 20))
                            .weight((double) faker.number().numberBetween(5, 200))
                            .totalQuantity((double) faker.number().numberBetween(1, 20))
                            .totalWeight((double) faker.number().numberBetween(5, 200))
                            .description(faker.commerce().productName())
                            .build());

            if (out) {
                Warehouse to = declared;
                if (allWh.size() > 1) {
                    do { to = allWh.get(rnd.nextInt(allWh.size())); }
                    while (to.equals(declared));
                }
                mb.toWarehouse(to)
                        .customsDocument(CustomsDocument.builder()
                                .type("EX")
                                .ref("DOC-" + faker.number().digits(6))
                                .build());
            } else {
                Warehouse from = declared;
                if (allWh.size() > 1) {
                    do { from = allWh.get(rnd.nextInt(allWh.size())); }
                    while (from.equals(declared));
                }
                mb.fromWarehouse(from);
            }

            buffer.add(mb.build());

            // flush par batch (200)
            if (buffer.size() >= 200) {
                movementRepository.saveAll(buffer);
                buffer.clear();
            }
        }

        // sauvegarde du reliquat
        if (!buffer.isEmpty()) {
            movementRepository.saveAll(buffer);
        }

        log.info("[fixtures] {} movements loaded with faker (batch).", n);
    }


    private String generateRefCode(RefType type) {
        if (type == RefType.AWB) {
            return faker.number().digits(11); // AWB
        }
        return "REF-" + faker.number().digits(6);
    }

    private void ensureWarehouses() {
        if (warehouseRepository.count() > 0) return;
        warehouseRepository.save(Warehouse.builder()
                .code("CDG").label("Paris Charles-de-Gaulle").build());
        warehouseRepository.save(Warehouse.builder()
                .code("BRU").label("Bruxelles Zaventem").build());
        warehouseRepository.save(Warehouse.builder()
                .code("LHR").label("London Heathrow").build());
        warehouseRepository.save(Warehouse.builder()
                .code("FRA").label("Frankfurt Main").build());
         warehouseRepository.save(Warehouse.builder()
                .code("RAPIDCARGO").label("RapidCargo CDG").build());
    }
}
