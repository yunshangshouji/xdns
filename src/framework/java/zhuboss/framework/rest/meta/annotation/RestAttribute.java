package zhuboss.framework.rest.meta.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RestAttribute {
	
	String name();
	
	int len();
	
	boolean notnull();
	
	String remark();
	
}
