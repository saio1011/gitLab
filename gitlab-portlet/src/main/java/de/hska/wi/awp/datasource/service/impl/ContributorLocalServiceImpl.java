package de.hska.wi.awp.datasource.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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
	public Map<String,String> getContributors() throws IOException{
		
		Properties configFile = this.loadConfigFile();
		String private_token = "private_token="+configFile.getProperty("private_token");
		
		int anzahlProjekte = Integer.parseInt(configFile.getProperty("AnzahlProjekte"));
		List<String> projektNamen = new ArrayList<String>();
		for(int zl=1; zl<=anzahlProjekte; zl++){
			projektNamen.add(configFile.getProperty("ProjektName"+zl));
		}
		
		//get number of repositories for each project
		Map<String,Integer> anzahlReposProjekt = new HashMap<String, Integer>();
		for(String projektName : projektNamen){
			anzahlReposProjekt.put(projektName, Integer.parseInt(configFile.getProperty(projektName+"_AnzahlRepos")));
		}
		
		//get names of repositories
		List<String> reposNames = new ArrayList<String>();
		Map<String,String> reposNamesWithProjectName = new HashMap<String, String>();
		for (Map.Entry<String, Integer> entry : anzahlReposProjekt.entrySet()) {
			String key = entry.getKey();
		    int value = entry.getValue();
		    for(int zl = 1; zl <= value; zl++){
		    	reposNames.add(configFile.getProperty(key+"_Repo"+zl));
		    	reposNamesWithProjectName.put(configFile.getProperty(key+"_Repo"+zl), key);
		    }
		}
				
		//get repos Ids
//		List<String> reposIds = new ArrayList<String>();
//		for(String repoName : reposNames){
//			try {
//				String repoId = this.getProjectId(repoName, private_token);
//				reposIds.add(repoId);
//			} catch (JSONException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		Map<String,String> reposIdsWithProjectName = new HashMap<String, String>();
		for (Map.Entry<String, String> entry : reposNamesWithProjectName.entrySet()) {
			String repoName = entry.getKey();
			String projectName = entry.getValue();
			try {
				String repoId = this.getProjectId(repoName, private_token);
				reposIdsWithProjectName.put(repoId, projectName);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
//		//print
//		for (Map.Entry<String, String> entry : reposIdsWithProjectName.entrySet()) {
//			System.out.println("--------->");
//			System.out.println(entry.getKey()+ "->" + entry.getValue());
//		}
		
		
		//get Reguests for each repo
		List<String> responses = new ArrayList<String>();
		Map<String,String> responsesWithProjectName = new HashMap<String, String>();
		for(Map.Entry<String, String> entry : reposIdsWithProjectName.entrySet()){
			String repoId = entry.getKey();
			String projectName = entry.getValue();
			String url = de.hska.wi.awp.datasource.utils.Constants.GITLAB_PATH_PROJECTS + repoId + de.hska.wi.awp.datasource.utils.Constants.GITLAB_PATH_CONTRIBUTORS + "?" + private_token + "&per_page=100";
			
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			
			ClientResponse response = webResource
					.type("application/json").accept("application/json")
					.get(ClientResponse.class);
	
			String responseBody = response.getEntity(String.class);
			responses.add(responseBody);
			responsesWithProjectName.put(responseBody, projectName);
		}
		
//		//print
//		for (Map.Entry<String, String> entry : responsesWithProjectName.entrySet()) {
//		System.out.println("--------->");
//		System.out.println(entry.getKey()+ "->" + entry.getValue());
//	}
		return responsesWithProjectName;
//		String url = de.hska.wi.awp.datasource.utils.Constants.GITLAB_PATH_PROJECTS + projectId + de.hska.wi.awp.datasource.utils.Constants.GITLAB_PATH_CONTRIBUTORS + "?" + privateTocken;
//		
//		Client client = Client.create();
//		WebResource webResource = client.resource(url);
//
//		ClientResponse response = webResource
//				.type("application/json").accept("application/json")
//				.get(ClientResponse.class);
//
//		String responseBody = response.getEntity(String.class);
//		
//		return responseBody;
	}
	
	/**
	 * parse contributors to java objects and save the parsed objects into database
	 * 
	 * @param String jsonContributors - all contributors as json list
	 * @return void
	 * @throws JSONException, SystemException
	 * @author Mihai Sava
	 */
	public void ParseContributorsFromJson(Map<String,String> jsonContributorsResponsesWithProjectName) throws JSONException, SystemException{
		
		ContributorLocalServiceUtil.deleleAllContributors();
		
		int kreisNr = 100;
		for(Map.Entry<String, String> entry : jsonContributorsResponsesWithProjectName.entrySet()){
			String response = entry.getKey();
			String projectName = entry.getValue();
			JSONArray jsonArrayContributors = new JSONArray(response);
			
			for (int zl = 0; zl<jsonArrayContributors.length(); zl++){
				Contributor newContributor = ContributorLocalServiceUtil.createContributor(kreisNr+zl+1);
				newContributor.setCommits(jsonArrayContributors.getJSONObject(zl).getInt("commits"));
				newContributor.setEmail(jsonArrayContributors.getJSONObject(zl).getString("email"));
				newContributor.setName(jsonArrayContributors.getJSONObject(zl).getString("name"));
				newContributor.setLocAdditions(jsonArrayContributors.getJSONObject(zl).getInt("additions"));
				newContributor.setLocDeletions(jsonArrayContributors.getJSONObject(zl).getInt("deletions"));
				newContributor.setProjectName(projectName);
				ContributorLocalServiceUtil.addContributor(newContributor);
			}
			kreisNr += 100;
		}
		
//		ContributorLocalServiceUtil.deleleAllContributors();
//		
//		JSONArray jsonArrayContributors = new JSONArray(jsonContributors);
//		
//		for (int zl = 0; zl<jsonArrayContributors.length(); zl++){
//			Contributor newContributor = ContributorLocalServiceUtil.createContributor(zl+1);
//			newContributor.setCommits(jsonArrayContributors.getJSONObject(zl).getInt("commits"));
//			newContributor.setEmail(jsonArrayContributors.getJSONObject(zl).getString("email"));
//			newContributor.setName(jsonArrayContributors.getJSONObject(zl).getString("name"));
//			newContributor.setLocAdditions(jsonArrayContributors.getJSONObject(zl).getInt("additions"));
//			newContributor.setLocDeletions(jsonArrayContributors.getJSONObject(zl).getInt("deletions"));
//			
//			ContributorLocalServiceUtil.addContributor(newContributor);
//		}
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
	
	public String getProjectId(String projectName, String privateTocken) throws JSONException{
		String url = de.hska.wi.awp.datasource.utils.Constants.GITLAB_FIND_POJECT_BY_NAME + projectName + "?" + privateTocken;
		
		Client client = Client.create();
		WebResource webResource = client.resource(url);

		ClientResponse response = webResource
				.type("application/json").accept("application/json")
				.get(ClientResponse.class);

		String responseBody = response.getEntity(String.class);
		
		JSONArray jsonArrayCommits = new JSONArray(responseBody);
		String projectId = "";
		for (int zl = 0; zl<jsonArrayCommits.length(); zl++){
			if(projectName.equals(jsonArrayCommits.getJSONObject(zl).getString("name")) && projectName.equals(jsonArrayCommits.getJSONObject(zl).getString("path"))){
				projectId = jsonArrayCommits.getJSONObject(zl).getString("id");
			}
		}
		
		return projectId;
	}
	
	public Properties loadConfigFile(){
    	////
    	Properties prop = new Properties();
    	InputStream input = null;
    	
    	try {
    		 
    		String filename = "gitLab.properties";
    		input = getClass().getClassLoader().getResourceAsStream(filename);
    		if (input == null) {
    			System.out.println("Sorry, unable to find " + filename);
    			return null;
    		}
     
    		prop.load(input);
    		
    	} catch (IOException ex) {
    		ex.printStackTrace();
        } finally{
        	if(input!=null){
        		try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        	}
        }
    	
    	return prop;
    }
}
