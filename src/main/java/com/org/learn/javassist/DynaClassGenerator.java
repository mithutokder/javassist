package com.org.learn.javassist;

import java.beans.IntrospectionException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtNewMethod;

/**
 * This class generates java class Runtime using Javassist.
 *
 */
public class DynaClassGenerator {
	
	private Generator generator;
	
	private Set<String> fields = new HashSet<String>();
	
	private Class<?> clazz;

	private DynaClassGenerator (Generator generator) {
		this.generator = generator;
	}
	
	public static DynaClassGenerator newInstance() {
		return new DynaClassGenerator(new Generator());
	}
	
	public DynaClassGenerator createClass(String className) {
		generator.createClass(className);
		return this;
	}
	
	public DynaClassGenerator addField(Set<String> fields, boolean generateGetterSetter) throws CannotCompileException {
		for(String field : fields) {
			addField(field, generateGetterSetter);
		}
		return this;
	}
	
	public DynaClassGenerator addField(String field, boolean generateGetterSetter) throws CannotCompileException {
		if(!fields.add(field)) {
			throw new IllegalArgumentException(String.format("field [%s] already present", field));
		}
		generator.addField(field, generateGetterSetter);
		return this;
	}
	
	public DynaClassGenerator addMethod(String methodDesc) throws CannotCompileException {
		generator.addMethod(methodDesc);
		return this;
	}
	
	public DynaClassGenerator addToString() throws CannotCompileException {
		generator.addToString();
		return this;
	}
	
	public DynaClassGenerator generate() throws Exception {
		this.clazz = generator.toClass();
		return this;
	}
	
	public Class<?> clazz() {
		return clazz;
	}
	
	public DynaClassAccessor getAccessor() throws IntrospectionException {
		return new DynaClassAccessor(clazz);
	}
	
	private static class Generator {
		
		private final String FIELD_TEMPLATE = "private String %s;";
		private final String GETTER_FUNC_TEMPLATE = "public String get%s () { return %s ; }";
		private final String SETTER_FUNC_TEMPLATE = "public void set%s (String value) { this.%s = value ; }";
		
		private StringBuilder toString = new StringBuilder();
				
		
		private CtClass evalClass;
		
		private void createClass(String className) {
			ClassPool pool = ClassPool.getDefault();
			evalClass = pool.makeClass(className);
			toString.append("\"" +className + " : \"");
		}
		
		//return "Hello : roll [+this.field+] address [+this.field+] name [+this.field+] ;
		
		private void addField(String field, boolean generateGetterSetter) throws CannotCompileException {
			evalClass.addField(CtField.make(String.format(FIELD_TEMPLATE, field), evalClass));
			toString.append("\"" + field + "[\" + this." + field + " + \"] \" ");
			if(generateGetterSetter) {
				// add getter method
				evalClass.addMethod(
				        CtNewMethod.make(String.format(GETTER_FUNC_TEMPLATE, StringUtils.capitalize(field), field),
				            evalClass));
				// add setter method
				evalClass.addMethod(
				        CtNewMethod.make(String.format(SETTER_FUNC_TEMPLATE, StringUtils.capitalize(field), field),
				            evalClass));
			}
		}
		
		private void addMethod(String methodDesc) throws CannotCompileException {
			evalClass.addMethod(CtNewMethod.make(methodDesc, evalClass));
		}
		
		private void addToString() throws CannotCompileException {
			evalClass.addMethod(CtNewMethod.make("public String toString() { return " + toString.toString() + "; }", evalClass));
		}
		
		private Class<?> toClass() throws Exception {
			return evalClass.toClass();
		}
		
	}

}
