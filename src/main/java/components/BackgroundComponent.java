package components;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.ViewComponent;
import javafx.scene.layout.AnchorPane;

public class BackgroundComponent extends Component {
    private final AnchorPane root;

    public BackgroundComponent(AnchorPane root) {
        this.root = root;
    }

    @Override
    public void onAdded() {
        ViewComponent viewComponent = entity.getViewComponent();
        viewComponent.addChild(root);
    }
}