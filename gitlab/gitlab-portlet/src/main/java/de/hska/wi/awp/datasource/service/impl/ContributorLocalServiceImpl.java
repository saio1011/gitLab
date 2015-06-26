package de.hska.wi.awp.datasource.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.commons.io.IOUtils;
import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONException;

import com.liferay.portal.kernel.exception.SystemException;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import de.hska.wi.awp.datasource.NoSuchContributorException;
import de.hska.wi.awp.datasource.model.Contributor;
import de.hska.wi.awp.datasource.service.ContributorLocalServiceUtil;
import de.hska.wi.awp.datasource.service.base.ContributorLocalServiceBaseImpl;
import de.hska.wi.awp.datasource.service.persistence.ContributorUtil;

/**
 * The implementation of the contributor local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the {@link de.hska.wi.awp.datasource.service.ContributorLocalService} interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security checks based on the propagated JAAS credentials because this service can only be accessed from within the same VM.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see de.hska.wi.awp.datasource.service.base.ContributorLocalServiceBaseImpl
 * @see de.hska.wi.awp.datasource.service.ContributorLocalServiceUtil
 */
public class ContributorLocalServiceImpl extends ContributorLocalServiceBaseImpl {
    /*
     * NOTE FOR DEVELOPERS:
     *
     * Never reference this interface directly. Always use {@link de.hska.wi.awp.datasource.service.ContributorLocalServiceUtil} to access the contributor local service.
     */
	
	/**
	 * rest call to get all contributors (from gitlab) - from one project
	 * gitlab api has pagination and max entries pro page
	 * 
	 * @param -
	 * @return String - json list with all contributors 
	 * @throws IOException
	 * @author Mihai Sava
	 */
	public String getContributors(String projectId, String privateTocken) throws IOException{		
		String url = de.hska.wi.awp.datasource.utils.Constants.GITLAB_PATH_PROJECTS + projectId + de.hska.wi.awp.datasource.utils.Constants.GITLAB_PATH_CONTRIBUTORS + "?" + privateTocken;
		
		Client client = Client.create();
		WebResource webResource = client.resource(url);

		ClientResponse response = webResource
				.type("application/json").accept("application/json")
				.get(ClientResponse.class);

		String responseBody = response.getEntity(String.class);
		
		return responseBody;
	}
	
	/**
	 * parse contributors to java objects and save the parsed objects into database
	 * 
	 * @param String jsonContributors - all contributors as json list
	 * @return void
	 * @throws JSONException, SystemException
	 * @author Mihai Sava
	 */
	public void ParseContributorsFromJson(String jsonContributors) throws JSONException, SystemException{
		
		ContributorLocalServiceUtil.deleleAllContributors();
		
		JSONArray jsonArrayContributors = new JSONArray(jsonContributors);
		
		for (int zl = 0; zl<jsonArrayContributors.length(); zl++){
			Contributor newContributor = ContributorLocalServiceUtil.createContributor(zl+1);
			newContributor.setCommits(jsonArrayContributors.getJSONObject(zl).getInt("commits"));
			newContributor.setEmail(jsonArrayContributors.getJSONObject(zl).getString("email"));
			newContributor.setName(jsonArrayContributors.getJSONObject(zl).getString("name"));
			newContributor.setLocAdditions(jsonArrayContributors.getJSONObject(zl).getInt("additions"));
			newContributor.setLocDeletions(jsonArrayContributors.getJSONObject(zl).getInt("deletions"));
			
			ContributorLocalServiceUtil.addContributor(newContributor);
		}
	}
	
	public Contributor getCurrentUser(String studentName) throws NoSuchContributorException, SystemException{
		Contributor currentStudent = ContributorUtil.findByName(studentName);
		return currentStudent;
	}
	
	/**
	 * delete all contributors from the database
	 * 		this method is called before new contributors are parsed and saved into database 
	 * 
	 * @param  -
	 * @return void
	 * @throws SystemException
	 * @author Mihai Sava
	 */
	public void deleleAllContributors() throws SystemException{
		List<Contributor> allContributors = ContributorLocalServiceUtil.getContributors(0, ContributorLocalServiceUtil.getContributorsCount());
		
		for(int zl = 0; zl < allContributors.size(); zl++){
			ContributorLocalServiceUtil.deleteContributor(allContributors.get(zl));
		}
	}
}
