package observers;

import domain.models.Order;

public interface OrderObserver {
    void onOrderReady(Order order);
}
