package active_lead.servers;

public interface Component {

    default <Q extends Component> void setField(Q object) {
        throw new IllegalArgumentException();
    }

    default String getName() {
        return this.getClass().getSimpleName();
    }
}
