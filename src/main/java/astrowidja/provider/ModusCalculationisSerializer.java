package astrowidja.provider;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * Сериализует {@link ModusCalculationis} в JSON:
 * <ul>
 *   <li>если {@code userAyanamshaOffset == 0} → только {@code int} (AWBitmask)</li>
 *   <li>если {@code != 0} → объект с полями {@code bitmask} и {@code ayanamshaOffset}</li>
 * </ul>
 */
public class ModusCalculationisSerializer extends JsonSerializer<ModusCalculationis> {

    @Override
    public void serialize(ModusCalculationis modus, JsonGenerator gen, SerializerProvider provider)
            throws IOException {

        int bitmask = modus.getAWBitmask();

        if (modus.getUserAyanamshaOffset() == 0.0) {
            // Только bitmask без offset
            gen.writeStartObject();
            gen.writeNumberField("bitmask", bitmask);
            gen.writeEndObject();
        } else {
            // bitmask + offset
            gen.writeStartObject();
            gen.writeNumberField("bitmask", bitmask);
            gen.writeNumberField("ayanamshaOffset", modus.getUserAyanamshaOffset());
            gen.writeEndObject();
        }
    }
}
