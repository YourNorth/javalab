package repositories;

import active_lead.ConnectionWrap;
import models.User;

public interface UserRepository extends CrudRepository<User, Long> {

    String getPassword(String name);
    ConnectionWrap getConnectionWrap();

}
