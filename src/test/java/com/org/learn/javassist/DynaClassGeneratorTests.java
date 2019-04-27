package com.org.learn.javassist;

import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Test;

public class DynaClassGeneratorTests {
	
	@Test
	public void testGenGetterSetter() throws Exception {
		DynaClassAccessor accessor = DynaClassGenerator.newInstance()
		        .createClass("Student") //class name
		        .addField("name", Boolean.TRUE) //field with getter & setter
		        .addField("address", Boolean.TRUE) //field with getter & setter
		        .generate().getAccessor();
		
        Object ob = accessor.newInstance();
        
        accessor.invokeSetter(ob, "name", "Mithu");
        accessor.invokeSetter(ob, "address", "Kolkata");
        
        String name = accessor.invokeGetter(ob, "name", String.class);
        String address = accessor.invokeGetter(ob, "address", String.class);
        Assert.assertEquals("Mithu", name);
        Assert.assertEquals("Kolkata", address);
	}
	
	@Test
	public void testGenGetterSetterWithToString() throws Exception {
		DynaClassAccessor accessor = DynaClassGenerator.newInstance()
		        .createClass("Student1") // class name
		        .addField("name", Boolean.TRUE) //field with getter & setter
		        .addField("address", Boolean.TRUE) //field with getter & setter
		        .addToString() // will generate toString method
		        .generate().getAccessor();
		
		// create new Object
        Object ob = accessor.newInstance();
        // set value to the name field
        accessor.invokeSetter(ob, "name", "Mithu");
        // set value to the address field
        accessor.invokeSetter(ob, "address", "Kolkata");
        // check toString method
        Assert.assertNotNull(ob.toString());
	}
	
	@Test
	public void testAddMethod() throws Exception {
		// simple method to make string uppercase
		final String newMethod = "public String toUpper(String value) { return  value.toUpperCase(); }";
		
		DynaClassAccessor accessor = DynaClassGenerator.newInstance()
		        .createClass("Student2") // class name
		        .addMethod(newMethod) // adding new method
		        .generate().getAccessor();
		
		Object ob = accessor.newInstance();
		Method method = accessor.findMethod("toUpper", String.class);
        Object result = method.invoke(ob, "mithutokder"); // invoking the added method
        Assert.assertEquals("MITHUTOKDER", result);
	}
	
	

}
