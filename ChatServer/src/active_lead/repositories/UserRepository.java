package active_lead.repositories;

import active_lead.models.User;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

    String contains(String name);

}
