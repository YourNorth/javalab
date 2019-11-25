package active_lead;

import java.lang.reflect.Field;

public interface Component {

    default <Q extends Component> void setField(Q object) {
        for (Field field : this.getClass().getDeclaredFields()) {
            if (field.getType().isAssignableFrom(object.getClass())) {
                field.setAccessible(true);
                try {
                    field.set(this, object);
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException(e);
                }
            }
        }
    }

    default String getName() {
        return this.getClass().getSimpleName();
    }
}