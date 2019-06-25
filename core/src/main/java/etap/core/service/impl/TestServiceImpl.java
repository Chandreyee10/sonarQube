package etap.core.service.impl;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.osgi.service.metatype.annotations.Option;

import etap.core.service.TestService;

@Component(service = TestService.class, immediate = true)
@Designate(ocd = TestServiceImpl.Config.class)
public class TestServiceImpl implements TestService {

	@ObjectClassDefinition(name = "Test Config", description = "Test Config Descr")
	public static @interface Config {
		@AttributeDefinition(name = "Boolean Property", description = "Sample boolean value", type = AttributeType.BOOLEAN)
		boolean servicename_propertyname_boolean() default true;

		@AttributeDefinition(name = "String Property", description = "Sample String property", type = AttributeType.STRING)
		String servicename_propertyname_string() default "foo";

		@AttributeDefinition(name = "Dropdown property", description = "Sample dropdown property", options = {
				@Option(label = "DAYS", value = "DAYS"), @Option(label = "HOURS", value = "HOURS"),
				@Option(label = "MILLISECONDS", value = "MILLISECONDS"), @Option(label = "MINUTES", value = "MINUTES"),
				@Option(label = "SECONDS", value = "SECONDS") })
		String servicename_propertyname_dropdown() default StringUtils.EMPTY;

		@AttributeDefinition(name = "String Array Property", description = "Sample String array property", type = AttributeType.STRING)
		String[] servicename_propertyname_string_array() default { "foo", "bar" };

		/*
		 * To create password field, either set the AttributeType or have the property
		 * name end with "*.password" (or both).
		 */
		@AttributeDefinition(name = "Password Property", description = "Sample password property", type = AttributeType.PASSWORD)
		String servicename_propertyname_password() default StringUtils.EMPTY;

		@AttributeDefinition(name = "Long Property", description = "Sample long property", type = AttributeType.LONG)
		long servicename_propertyname_long() default 0L;
	}

	@Reference
	private ResourceResolverFactory resolverFactory;

	boolean booleanProp;
	String stringProp;
	String dropdownProp;
	String[] stringArrayProp;
	char[] passwordProp;
	long longProp;

	@Override
	public String getSettings() {
		StringBuilder sb = new StringBuilder();
		sb.append("Sample OSGi Service:\n");
		sb.append("booleanProp: " + booleanProp + "\n");
		sb.append("stringProp: " + stringProp + "\n");
		sb.append("dropdownProp: " + dropdownProp + "\n");
		sb.append("stringArrayProp: " + ArrayUtils.toString(stringArrayProp) + "\n");
		sb.append("passwordProp: " + String.valueOf(passwordProp) + "\n");
		sb.append("longProp: " + longProp + "\n");

		return sb.toString();
	}

	@Activate
	@Modified
	protected final void activate(Config config) {
		booleanProp = config.servicename_propertyname_boolean();
		stringProp = config.servicename_propertyname_string();
		dropdownProp = config.servicename_propertyname_dropdown();
		stringArrayProp = config.servicename_propertyname_string_array();
		passwordProp = config.servicename_propertyname_password().toCharArray();
		longProp = config.servicename_propertyname_long();
	}

	@Deactivate
	protected void deactivate() {
	}
}
