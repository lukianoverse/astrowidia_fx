package astrowidja.core;

import astrowidja.provider.CalcMethod;
import astrowidja.model.LilithType;
import astrowidja.model.NodeType;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Settings {
    // значения по умолчанию вшиты изначально
    private final ObjectProperty<CalcMethod> calcMethod = new SimpleObjectProperty<>(CalcMethod.MOSHIER);
    private final ObjectProperty<NodeType> nodeType = new SimpleObjectProperty<>(NodeType.MEAN);
    private final ObjectProperty<LilithType> lilithType = new SimpleObjectProperty<>(LilithType.MEAN);

    // астры, идущие в расчёт
    private final ObservableList<String> astrasToCalculate = FXCollections.observableArrayList();
    public ObservableList<String> getAstrasToCalculate() {
        return astrasToCalculate;
    }

    // Properties / Getters / Setters
    public ObjectProperty<CalcMethod> calcMethodProperty() {
        return calcMethod;
    }
    public CalcMethod getCalcMethod() {
        return calcMethod.get();
    }
    public void setCalcMethod(CalcMethod method) {
        this.calcMethod.set(method);
    }

    public ObjectProperty<NodeType> nodeTypeProperty() {
        return nodeType;
    }
    public NodeType getNodeType() {
        return nodeType.get();
    }
    public void setNodeType(NodeType type) {
        this.nodeType.set(type);
    }

    public ObjectProperty<LilithType> lilithTypeProperty() {
        return lilithType;
    }
    public LilithType getLilithType() {
        return lilithType.get();
    }
    public void setLilithType(LilithType type) {
        this.lilithType.set(type);
    }
}
