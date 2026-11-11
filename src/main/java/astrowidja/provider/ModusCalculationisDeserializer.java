package astrowidja.provider;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * Десериализует {@link ModusCalculationis} из JSON:
 * <ul>
 *   <li>число → только bitmask (offset = 0)</li>
 *   <li>объект с {@code bitmask} и опционально {@code ayanamshaOffset}</li>
 * </ul>
 */
public class ModusCalculationisDeserializer extends JsonDeserializer<ModusCalculationis> {

    @Override
    public ModusCalculationis deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ModusCalculationis modus = new ModusCalculationis();

        if (p.currentToken() == JsonToken.VALUE_NUMBER_INT) {
            // Только число-маска
            int bitmask = p.getIntValue();
            modus.setFromAWBitmask(bitmask);
        } else if (p.currentToken() == JsonToken.START_OBJECT) {
            // Объект с bitmask + ayanamshaOffset
            p.nextToken(); // moveToFieldName
            while (p.currentToken() != JsonToken.END_OBJECT) {
                if (p.currentToken() == JsonToken.FIELD_NAME) {
                    String field = p.currentName();
                    p.nextToken();
                    if ("bitmask".equals(field)) {
                        int bitmask = p.getIntValue();
                        modus.setFromAWBitmask(bitmask);
                    } else if ("ayanamshaOffset".equals(field)) {
                        modus.setUserAyanamshaOffset(p.getDoubleValue());
                    }
                } else {
                    p.nextToken();
                }
            }
        }

        return modus;
    }
}
