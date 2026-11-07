package astrowidja.core;

import astrowidja.model.LilithType;
import astrowidja.model.NodeType;
import astrowidja.provider.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Settings {
    // значения по умолчанию заданы изначально

    // --- Режим вычислений (ModusCalculationis) ---
    private final ObjectProperty<CalcMethod> calcMethod
            = new SimpleObjectProperty<>(CalcMethod.MOSHIER);
    private final ObjectProperty<ReferencePoint> referencePoint
            = new SimpleObjectProperty<>(ReferencePoint.GEOCENTRIC);
    private final ObjectProperty<AyanamshaMode> ayanamsha
            = new SimpleObjectProperty<>(AyanamshaMode.TROPICAL);
    private final ObjectProperty<ProjectionPlane> projectionPlane
            = new SimpleObjectProperty<>(ProjectionPlane.ECLIPTIC);
    private final DoubleProperty userAyanamshaOffset
            = new SimpleDoubleProperty(0.0);

    // --- Узлы и аспиды ---
    private final ObjectProperty<NodeType> nodeType
            = new SimpleObjectProperty<>(NodeType.MEAN);
    private final ObjectProperty<LilithType> lilithType
            = new SimpleObjectProperty<>(LilithType.MEAN);

    // --- Астры, идущие в расчёт ---
    private final ObservableList<String> astrasToCalculate
            = FXCollections.observableArrayList();

    /**
     * Создаёт объект режима вычислений на основе текущих настроек.
     */
    public ModusCalculationis getCurrentModus() {
        ModusCalculationis modus = new ModusCalculationis();
        modus.setCalcMethod(calcMethod.get());
        modus.setReferencePoint(referencePoint.get());
        modus.setAyanamsha(ayanamsha.get());
        modus.setProjectionPlane(projectionPlane.get());
        modus.setUserAyanamshaOffset(userAyanamshaOffset.get());
        return modus;
    }

    // ==================== Properties / Getters / Setters ====================

    public ObservableList<String> getAstrasToCalculate() {
        return astrasToCalculate;
    }

    // Метод вычислений
    public ObjectProperty<CalcMethod> calcMethodProperty() {
        return calcMethod;
    }

    public CalcMethod getCalcMethod() {
        return calcMethod.get();
    }

    public void setCalcMethod(CalcMethod method) {
        this.calcMethod.set(method);
    }

    // Точка отсчёта
    public ObjectProperty<ReferencePoint> referencePointProperty() {
        return referencePoint;
    }

    public ReferencePoint getReferencePoint() {
        return referencePoint.get();
    }

    public void setReferencePoint(ReferencePoint ref) {
        this.referencePoint.set(ref);
    }

    // Аянамша
    public ObjectProperty<AyanamshaMode> ayanamshaProperty() {
        return ayanamsha;
    }

    public AyanamshaMode getAyanamsha() {
        return ayanamsha.get();
    }

    public void setAyanamsha(AyanamshaMode ayanamsha) {
        this.ayanamsha.set(ayanamsha);
    }

    // Плоскость проекции
    public ObjectProperty<ProjectionPlane> projectionPlaneProperty() {
        return projectionPlane;
    }

    public ProjectionPlane getProjectionPlane() {
        return projectionPlane.get();
    }

    public void setProjectionPlane(ProjectionPlane plane) {
        this.projectionPlane.set(plane);
    }

    // Ручная аяманша
    public DoubleProperty userAyanamshaOffsetProperty() {
        return userAyanamshaOffset;
    }

    public double getUserAyanamshaOffset() {
        return userAyanamshaOffset.get();
    }

    public void setUserAyanamshaOffset(double offset) {
        this.userAyanamshaOffset.set(offset);
    }

    // Тип Лунных Узлов
    public ObjectProperty<NodeType> nodeTypeProperty() {
        return nodeType;
    }

    public NodeType getNodeType() {
        return nodeType.get();
    }

    public void setNodeType(NodeType type) {
        this.nodeType.set(type);
    }

    // Тип Лилит
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
