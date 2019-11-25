package active_lead;

import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.stream.Collectors;

public class ReflectionContext implements Context {

    private ConnectionWrap connectionWrap;
    private Set<Component> childrenObjects;

    public ReflectionContext(String properties) {
        this.connectionWrap = new ConnectionWrap(properties);
        Reflections reflections = new Reflections();

        Set<Class<? extends Component>> componentChildren = reflections.getSubTypesOf(Component.class);
        componentChildren.remove(ConnectionWrap.class);
        componentChildren.forEach(i -> System.out.println("comp = " + i.getName()));
        System.out.println();

        childrenObjects = componentChildren.stream().filter(i -> !i.isInterface() && !Modifier.isAbstract(i.getModifiers())).map(i -> {
            try {
                return i.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new IllegalArgumentException();
            }
        }).collect(Collectors.toSet());
        childrenObjects.add(connectionWrap);

        childrenObjects.forEach(i -> System.out.println("objs = " + i.getClass().getSimpleName()));

        childrenObjects.forEach(i -> {
            Field[] fields = i.getClass().getDeclaredFields();
            for (Field field : fields) {
                childrenObjects.forEach(j -> {
                    if (field.getType().isAssignableFrom(j.getClass())) {
                        System.out.println("class " + i.getClass().getSimpleName() + ", field " + field.getType().getSimpleName() + ", set " + j.getClass().getSimpleName());
                        i.setField(j);
                    }
                });
            }
        });
    }

    @Override
    public Component getComponent(String name) {
        for (Component component: childrenObjects) {
            if (component.getName().equals(name)) {
                return component;
            }
        }
        throw new IllegalStateException();
    }
}


