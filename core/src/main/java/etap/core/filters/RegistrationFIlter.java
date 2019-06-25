package etap.core.filters;

import java.io.IOException;

import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.engine.EngineConstants;
import org.apache.sling.jcr.base.util.AccessControlUtil;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple servlet filter component that logs incoming requests.
 */

@Component(service = Filter.class,
property = {
        Constants.SERVICE_DESCRIPTION + "=Demo to filter incoming requests",
        EngineConstants.SLING_FILTER_SCOPE + "=" + EngineConstants.FILTER_SCOPE_COMPONENT,
        Constants.SERVICE_RANKING + ":Integer=-700",
        "sling.filter.pattern="+"/bin/registerServlet"

})
public final class RegistrationFIlter implements Filter {
	
	 private final Logger logger = LoggerFactory.getLogger(getClass());

	private FilterConfig filterConfigObj = null;


	public void init(FilterConfig filterConfigObj) {

	
		this.filterConfigObj = filterConfigObj;
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException
			
			{
			   if (!(request instanceof SlingHttpServletRequest) ||
		                !(response instanceof SlingHttpServletResponse)) {
		            // Not a SlingHttpServletRequest/Response, so ignore.
		            chain.doFilter(request, response); // This line would let you proceed to the rest of the filters. 
		            return;
		        }

		        final SlingHttpServletResponse slingResponse = (SlingHttpServletResponse) response;
		        final SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) request;
		        final Resource resource = slingRequest.getResource();

		        if (resource.getPath().startsWith("/bin/registerServlet")) {
		        
	
			{
				String userName = request.getParameter("username");
				String firstName = request.getParameter("firstName");
				String lastName = request.getParameter("lastName");
				String password = request.getParameter("password");
				String groupName = request.getParameter("groupName");

			
				
				
				if(userName == request.getParameter(userName))
				{
					logger.info("This is"+ userName+" already registred ");
					
				}else
				{
					logger.info("UserName  : " +userName);
					logger.info("firstName : " +firstName);
					logger.info("LastName  : " +lastName);
					logger.info("groupName : " +groupName);
					
						
				}
				

			}

	        chain.doFilter(request, response);
			}
			}

	public void destroy() {
	}
}