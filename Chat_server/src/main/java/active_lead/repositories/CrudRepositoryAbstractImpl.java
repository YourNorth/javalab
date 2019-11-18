package active_lead.repositories;

import active_lead.servers.Component;

import java.lang.reflect.Field;

public abstract class CrudRepositoryAbstractImpl<T, ID> implements CrudRepository<T, ID> {

    private ConnectionWrap connectionWrap;

    @Override
    public <Q extends Component> void setField(Q object) {
        if(ConnectionWrap.class.isAssignableFrom(object.getClass())) {
            try {
                Field field = this.getClass().getDeclaredField("connectionWrap");
                field.setAccessible(true);
                field.set(this, object);
            } catch (IllegalAccessException | NoSuchFieldException e) {
                throw new IllegalArgumentException(e);
            }
        }
        else {
            throw new IllegalArgumentException();
        }
    }

}
