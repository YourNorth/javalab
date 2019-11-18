package active_lead.repositories;

import active_lead.models.Message;

import java.util.ArrayList;

public interface MessageRepository extends CrudRepository<Message, Long> {

    ArrayList<Message> pagination(Integer size, Integer page);

}
