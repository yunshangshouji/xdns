package zhuboss.framework.util.serialize;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JAXBParserUtil {
	private static Logger logger = LoggerFactory.getLogger(JAXBParserUtil.class);
	/**
	 * 将对象序列化到xml
	 * 
	 * @param obj
	 * @return
	 * @throws JAXBException
	 */
	public static String serializeToXml(Object obj, Class<?>... clazz)
			throws JAXBException {
		return serializeToXml(obj, "UTF_8", clazz);
	}

	/**
	 * 将对象序列化到xml
	 * 
	 * @param obj
	 * @return
	 * @throws JAXBException
	 */
	public static String serializeToXml(Object obj, String encode,
			Class<?>... clazz) throws JAXBException {
		Set<Class<?>> classes = new HashSet<Class<?>>();
		if (clazz != null && clazz.length > 0) {
			for (int i = 0; i < clazz.length; i++) {
				classes.add(clazz[i]);
			}
		} else {
			extractClasses(obj, classes);
		}
		JAXBContext context = JAXBContext.newInstance(classes
				.toArray(new Class<?>[classes.size()]));
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_ENCODING, encode);
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, false);
		StringWriter writer = new StringWriter();
		marshaller.marshal(obj, writer);
		return writer.toString();
	}

	/**
	 * 将xml反序列化到对象
	 * 
	 * @param xml
	 * @param clazz
	 * @return
	 * @throws JAXBException
	 */
	public static Object unserializeFromXml(Reader reader, Class<?>... clazz)
			throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(clazz);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		Object obj = unmarshaller.unmarshal(reader);
		return obj;
	}

	/**
	 * 将xml反序列化到对象
	 * 
	 * @param xml
	 * @param clazz
	 * @return
	 * @throws JAXBException
	 */
	public static Object unserializeFromXml(String xml, Class<?>... clazz)
			throws JAXBException {
		StringReader reader = new StringReader(xml);
		Object obj = unserializeFromXml(reader, clazz);
		return obj;
	}
	
	private static void extractClasses(Object object, Set<Class<?>> classes) {
		Class<? extends Object> clazz = object.getClass();
		if (clazz.isInterface()) {
			return;
		}
		XmlAccessorType annotation = clazz.getAnnotation(XmlAccessorType.class);
		if (annotation != null) {
			XmlAccessType value = annotation.value();
			if (XmlAccessType.PUBLIC_MEMBER.equals(value)
					|| XmlAccessType.PROPERTY.equals(value)) {
				Method[] declaredMethods = clazz.getDeclaredMethods();
				for (Method method : declaredMethods) {
					if (Modifier.isPublic(method.getModifiers())) {
						Class<?>[] parameterTypes = method.getParameterTypes();
						// FIXME:对有参数的如set方法的处理
						if (parameterTypes.length == 0) {
							try {
								Object methodValue = method.invoke(object);
								if (methodValue != null) {
									extractCollectionClasses(methodValue,
											classes);
								}
							} catch (Exception e) {
								e.printStackTrace();
								logger.error("Extract class methods error");
							}
						}
					}
				}

			} else if (XmlAccessType.FIELD.equals(value)) {
				Field[] declaredFields = clazz.getDeclaredFields();
				for (Field field : declaredFields) {
					field.setAccessible(true);
					try {
						Object propertyValue = field.get(object);
						if (propertyValue != null) {
							extractCollectionClasses(propertyValue, classes);
						}
					} catch (Exception e) {
						e.printStackTrace();
						logger.error("Extract class properties error");
					}
				}
			}
		}

		extractCollectionClasses(object, classes);
	}
	
	private static void extractCollectionClasses(Object object,
			Set<Class<?>> classes) {
		try {
			if (object instanceof Collection) {
				for (Object item : ((Collection<?>) object)) {
					extractClasses(item, classes);
				}
			} else if (object instanceof Map) {
				Iterator<?> keys = ((Map<?, ?>) object).keySet().iterator();
				while (keys.hasNext()) {
					Object key = (Object) keys.next();
					Object value = ((Map<?, ?>) object).get(key);
					extractClasses(key, classes);
					extractClasses(value, classes);
				}
			} else {
				Class<? extends Object> class1 = object.getClass();
				if (class1.getName().contains("$$EnhancerByCGLIB$$")) {
					classes.add(class1.getSuperclass());
				} else if (!class1.isInterface()) {
					classes.add(class1);
				}
			}
		} catch (Exception exp) {
			exp.printStackTrace();
			logger.error("Extract property classes error");
		}
	}

}
