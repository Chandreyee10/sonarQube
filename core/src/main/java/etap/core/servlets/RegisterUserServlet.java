package etap.core.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.jcr.PropertyType;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import javax.jcr.security.AccessControlEntry;
import javax.jcr.security.AccessControlList;
import javax.jcr.security.AccessControlManager;
import javax.jcr.security.Privilege;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.oak.spi.security.principal.EveryonePrincipal;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.jcr.base.util.AccessControlUtil;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.replication.Replicator;

@Component(service = Servlet.class, property = { Constants.SERVICE_DESCRIPTION + "=Register User Servlet",
		"sling.servlet.methods=" + HttpConstants.METHOD_POST, "sling.servlet.paths=" + "/bin/registerServlet" })
public class RegisterUserServlet extends SlingAllMethodsServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final static Logger log = LoggerFactory.getLogger(RegisterUserServlet.class);

	ResourceResolverFactory resourceResolverFactory;

	@Override
	protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/plain");

		// get the ResourceResolverFactory directly from your own bundle.
		BundleContext bundleContext = FrameworkUtil.getBundle(this.getClass()).getBundleContext();
		resourceResolverFactory = (ResourceResolverFactory) bundleContext
				.getService(bundleContext.getServiceReference(ResourceResolverFactory.class.getName()));

		createGroupUser(request, response);
	}

	public void createGroupUser(SlingHttpServletRequest request, SlingHttpServletResponse response) {
		String userName = request.getParameter("username");
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String password = request.getParameter("password");
		String groupName = request.getParameter("groupName");

		Session session = null;
		ResourceResolver resourceResolver = null;
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put(ResourceResolverFactory.SUBSERVICE, "datawrite");
			resourceResolver = resourceResolverFactory.getServiceResourceResolver(param);
			session = resourceResolver.adaptTo(Session.class);

			// Create UserManager Object
			final UserManager userManager = AccessControlUtil.getUserManager(session);

			// Create a Group
			Group group = null;
			if (userManager.getAuthorizable(groupName) == null) {
				group = userManager.createGroup(groupName);

				ValueFactory valueFactory = session.getValueFactory();
				Value groupNameValue = valueFactory.createValue(groupName, PropertyType.STRING);
				group.setProperty("./profile/givenName", groupNameValue);
				session.save();

				log.info("---> {} Group successfully created.", group.getID());
			} else {
				log.info("---> Group already exist..");
			}

			// Create a User
			User user = null;
			if (userManager.getAuthorizable(userName) == null) {
				user = userManager.createUser(userName, password);
				String email = firstName + "." + lastName + "@gmail.com";

				ValueFactory valueFactory = session.getValueFactory();
				Value firstNameValue = valueFactory.createValue(firstName, PropertyType.STRING);
				Value lastNameValue = valueFactory.createValue(lastName, PropertyType.STRING);
				Value emailValue = valueFactory.createValue(email, PropertyType.STRING);

				user.setProperty("./profile/givenName", firstNameValue);
				user.setProperty("./profile/familyName", lastNameValue);
				user.setProperty("./profile/email", emailValue);
				session.save();

				// Add User to Group
				Group addUserToGroup = (Group) (userManager.getAuthorizable(groupName));
				addUserToGroup.addMember(userManager.getAuthorizable(userName));
				session.save();

				// set Resource-based ACLs
				String nodePath = user.getPath();
				setAclPrivileges(nodePath, session);

				response.getWriter().write("User registered successfully.");
			} else {
				response.getWriter().write("User already exist.");
			}
		} catch (Exception e) {
			log.info("---> Not able to perform User Management..");
			log.info("---> Exception.." + e.getMessage());
		} finally {
			if (session != null && session.isLive()) {
				session.logout();
			}
			if (resourceResolver != null) {
				resourceResolver.close();
			}
		}
	}

	public static void setAclPrivileges(String path, Session session) {
		try {
			AccessControlManager aMgr = session.getAccessControlManager();

			// create a privilege set
			Privilege[] privileges = new Privilege[] { aMgr.privilegeFromName(Privilege.JCR_VERSION_MANAGEMENT),
					aMgr.privilegeFromName(Privilege.JCR_MODIFY_PROPERTIES),
					aMgr.privilegeFromName(Privilege.JCR_ADD_CHILD_NODES),
					aMgr.privilegeFromName(Privilege.JCR_LOCK_MANAGEMENT),
					aMgr.privilegeFromName(Privilege.JCR_NODE_TYPE_MANAGEMENT),
					aMgr.privilegeFromName(Replicator.REPLICATE_PRIVILEGE) };

			AccessControlList acl;
			try {
				// get first applicable policy (for nodes w/o a policy)
				acl = (AccessControlList) aMgr.getApplicablePolicies(path).nextAccessControlPolicy();
			} catch (NoSuchElementException e) {
				// else node already has a policy, get that one
				acl = (AccessControlList) aMgr.getPolicies(path)[0];
			}
			// remove all existing entries
			for (AccessControlEntry e : acl.getAccessControlEntries()) {
				acl.removeAccessControlEntry(e);
			}
			// add a new one for the special "everyone" principal
			acl.addAccessControlEntry(EveryonePrincipal.getInstance(), privileges);

			// the policy must be re-set
			aMgr.setPolicy(path, acl);

			// and the session must be saved for the changes to be applied
			session.save();
		} catch (Exception e) {
			log.info("---> Not able to perform ACL Privileges..");
			log.info("---> Exception.." + e.getMessage());
		}
	}
}
