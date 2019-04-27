package com.org.learn.javassist;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * This class provides accessor method to invoke the functions
 * of the dynamically generated class.
 *
 */
public class DynaClassAccessor {
	
	private Class<?> clazz;
	
	private Map<String, Method> getterMethods = new HashMap<String, Method>();
	
	private Map<String, Method> setterMethods = new HashMap<String, Method>();
	
	private Map<String, PropertyDescriptor> fieldsMap = new HashMap<String, PropertyDescriptor>();
	
	public DynaClassAccessor(Class<?> clazz) throws IntrospectionException {
		this.clazz = clazz;
		init();
	}
	
	public Object newInstance() throws Exception {
		return clazz.newInstance();
	}

	private void init() throws IntrospectionException {
		BeanInfo info = Introspector.getBeanInfo(clazz, Object.class);
	    PropertyDescriptor[] props = info.getPropertyDescriptors();
	    for (PropertyDescriptor pd : props) {
	        String field = pd.getName();
	        Method getter = pd.getReadMethod();
	        Method setter = pd.getWriteMethod();
	        if(getter != null) {
	        	getterMethods.put(field, getter);
	        }
	        if(setter != null) {
	        	setterMethods.put(field, setter);
	        }
	        fieldsMap.put(field, pd);
	    }
	}
	
	public Method findGetterMethod(String fieldName) {
		return getterMethods.get(fieldName);
	}
	
	public <T> T invokeGetter(Object ref, String fieldName, Class<T> type) throws Exception {
		T value = null;
		Method getter = findGetterMethod(fieldName);
		if(getter != null) {
			value = (T) getter.invoke(ref);
		}
		return value;
	}
	
	public Method findMethod(String methodName, Class<?>... paramTypes) throws Exception {
		return clazz.getDeclaredMethod(methodName, paramTypes);
	}
	
	public Method findSetterMethod(String fieldName) {
		return setterMethods.get(fieldName);
	}
	
	public void invokeSetter(Object ref, String fieldName, Object ... params) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Method setter = findSetterMethod(fieldName);
		if(setter != null) {
			setter.invoke(ref, params);
		}
	}
	
	public Map<String, PropertyDescriptor> getFieldsMap() {
		return fieldsMap;
	}

}
