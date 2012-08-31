package rails.common.parser;

import java.lang.reflect.Constructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rails.common.LocalText;
import rails.game.RailsItem;
import rails.game.state.Creatable;

/**
 * Configure provides static methods that come along with the Configurable and Creatable Interfaces
 * 
 * Remark: Collects code from various mathods in ComponentManager, GameManager and other places in Rails1.x
 */
public class Configure {
    final static Logger log = LoggerFactory.getLogger(Configure.class);
    
    public static <T extends Creatable> Class<? extends T> getClassForName(Class<T> clazz, String className)
            throws ConfigurationException {
        Class<? extends T> subClazz;
        try {
             subClazz = (Class<? extends T>) Class.forName(className).asSubclass(clazz);
        } catch (Exception e) {
            throw new ConfigurationException(LocalText.getText(
                    "ComponentHasNoClass", className), e);
        }
        return subClazz;
    }
    
    public static <T extends Creatable> T create(Class<T> clazz, String className, RailsItem parent, String id)
        throws ConfigurationException {
        Class<? extends T> subClazz = getClassForName(clazz, className);
        return create(subClazz, RailsItem.class, parent, id);
    }
    
    public static <T extends Creatable, P extends RailsItem> T create(Class<T> clazz, String className, Class<P> parentClazz, P parent, String id)
        throws ConfigurationException {
        Class<? extends T> subClazz = getClassForName(clazz, className);
        return create(subClazz, parentClazz, parent, id );
    }
    
    public static <T extends Creatable> T create(Class<T> clazz, RailsItem parent, String id)
            throws ConfigurationException {
        return create(clazz, RailsItem.class, parent, id);
    }
    
    public static <T extends Creatable, P extends RailsItem> T create(
        Class<T> clazz, Class<P> parentClazz, P parent, String id)
        throws ConfigurationException {
        T component;
        try {
            Constructor<? extends T> subConstructor = clazz.getConstructor(parentClazz, String.class);
            component = subConstructor.newInstance(parent, id);
        } catch (Exception ex) {
            throw new ConfigurationException(LocalText.getText(
                    "ComponentHasNoClass", clazz.getName()), ex);
        }
        return component;
    }
    
}
