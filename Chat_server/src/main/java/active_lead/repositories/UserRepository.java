package active_lead.repositories;

import active_lead.models.User;

public interface UserRepository extends CrudRepository<User, Long> {

    String contains(String name);
    ConnectionWrap getConnectionWrap();

}
