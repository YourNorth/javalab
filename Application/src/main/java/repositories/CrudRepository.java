package repositories;


import active_lead.Component;

import java.util.List;
import java.util.Optional;

public interface CrudRepository<T, ID>  extends Component {

    boolean save(T model);
    void update(T model);
    void delete(ID id);
    Optional<T> find(ID id);

    List<T> findAll();
}
