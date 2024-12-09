package application;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import factories.*;

public class GameFactory implements EntityFactory {
    private final CustomerFactory customerFactory = new CustomerFactory();
    private final WaiterFactory waiterFactory = new WaiterFactory();
    private final CookFactory cookFactory = new CookFactory();
    private final TableFactory tableFactory = new TableFactory();
    private final ReceptionistFactory receptionistFactory = new ReceptionistFactory();

    @Spawns("customer")
    public Entity spawnCustomer(SpawnData data) {
        return customerFactory.create(data);
    }

    @Spawns("waiter")
    public Entity spawnWaiter(SpawnData data) {
        return waiterFactory.create(data);
    }

    @Spawns("cook")
    public Entity spawnCook(SpawnData data) {
        return cookFactory.create(data);
    }

    @Spawns("table")
    public Entity spawnTable(SpawnData data) {
        return tableFactory.create(data);
    }

    @Spawns("receptionist")
    public Entity spawnReceptionist(SpawnData data) {
        return receptionistFactory.create(data);
    }
}