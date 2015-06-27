package de.hska.wi.awp.datasource.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONException;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.DonutChartModel;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.util.Constants;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONObject;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import de.hska.wi.awp.datasource.model.Commit;
import de.hska.wi.awp.datasource.model.Contributor;
import de.hska.wi.awp.datasource.service.CommitLocalServiceUtil;
import de.hska.wi.awp.datasource.service.ContributorLocalServiceUtil;
import de.hska.wi.awp.datasource.service.base.CommitLocalServiceBaseImpl;
import de.hska.wi.awp.datasource.service.persistence.CommitUtil;
import de.hska.wi.awp.datasource.utils.Helper;

/**
 * The implementation of the commit local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the {@link de.hska.wi.awp.datasource.service.CommitLocalService} interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security checks based on the propagated JAAS credentials because this service can only be accessed from within the same VM.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see de.hska.wi.awp.datasource.service.base.CommitLocalServiceBaseImpl
 * @see de.hska.wi.awp.datasource.service.CommitLocalServiceUtil
 */
public class CommitLocalServiceImpl extends CommitLocalServiceBaseImpl {
    /*
     * NOTE FOR DEVELOPERS:
     *
     * Never reference this interface directly. Always use {@link de.hska.wi.awp.datasource.service.CommitLocalServiceUtil} to access the commit local service.
     */
	
	/**
	 * rest call to get all commits from gitlab for all projects
	 * gitlab api has pagination and max entries pro page 
	 * !!!therefore pagination does not works properly, I implemented the pagination manually
	 * 
	 * parameters in getRequest: privateTocken, per_page=100, page=pageNumber
	 * 
	 * @param -
	 * @return Map<String,String> - json responses with all commits for project name
	 * @throws - IOException
	 * @author Mihai Sava
	 */
	public Map<String,String> getAllCommitsAsJsonString() throws IOException{
		
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
		Map<String,String> reposNamesWithProjectName = new HashMap<String, String>();
		for (Map.Entry<String, Integer> entry : anzahlReposProjekt.entrySet()) {
			String key = entry.getKey();
		    int value = entry.getValue();
		    for(int zl = 1; zl <= value; zl++){
		    	reposNamesWithProjectName.put(configFile.getProperty(key+"_Repo"+zl), key);
		    }
		}
		
		//get repos Ids	
		Map<String,String> reposIdsWithProjectName = new HashMap<String, String>();
		for (Map.Entry<String, String> entry : reposNamesWithProjectName.entrySet()) {
			String repoName = entry.getKey();
			String projectName = entry.getValue();
			try {
				String repoId = Helper.getProjectId(repoName, private_token);
				reposIdsWithProjectName.put(repoId, projectName);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		//get Reguests for each repo
		Map<String,String> responsesWithProjectName = new HashMap<String, String>();
		for(Map.Entry<String, String> entry : reposIdsWithProjectName.entrySet()){
			String repoId = entry.getKey();
			String projectName = entry.getValue();
			String url = de.hska.wi.awp.datasource.utils.Constants.GITLAB_PATH_PROJECTS + repoId + de.hska.wi.awp.datasource.utils.Constants.GITLAB_PATH_COMMITS + "?" + private_token;
			
			Client client = Client.create();
			
			int pageNuber = 0;
			while(true){
				String newUrl = url+"&per_page=100&page="+pageNuber;
				
				WebResource webResource = client.resource(newUrl);

				ClientResponse response = webResource
						.type("application/json").accept("application/json")
						.get(ClientResponse.class);
				

				String responseBody = response.getEntity(String.class);
				
				pageNuber +=1;
				//check if are commits on the following page 
				if(responseBody.length()<3){
					String testUrl = url+"&per_page=100&page="+pageNuber;
					
					WebResource wResource = client.resource(testUrl);

					ClientResponse resp = wResource
							.type("application/json").accept("application/json")
							.get(ClientResponse.class);
					
					String respBody = resp.getEntity(String.class);
					
					if(respBody.length()<3){
						break;
					}
				}else{
					responsesWithProjectName.put(responseBody, projectName);
				}
			}
		}
		return responsesWithProjectName;
	}
	
	/**
	 * parse commits form json objects and save the parsed object into database
	 * 
	 * @param Map<String,String> jsonCommits - all commits as string responses and projects names
	 * 
	 * @author Mihai Sava 
	 */
	public void ParseCommitsFromJson(Map<String,String> jsonCommitsResponsesWithProjectName){
		
		try {
			CommitLocalServiceUtil.deleteAllCommits();
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(Map.Entry<String, String> entry : jsonCommitsResponsesWithProjectName.entrySet()){
			String response = entry.getKey();
			String projectName = entry.getValue();
			JSONArray jsonArrayCommits = null;
			try {
				jsonArrayCommits = new JSONArray(response);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			for (int zl = 0; zl<jsonArrayCommits.length(); zl++){
				Commit newCommit = null;
				try {
					newCommit = CommitLocalServiceUtil.createCommit(jsonArrayCommits.getJSONObject(zl).getString("id"));
					newCommit.setAuthorEmail(jsonArrayCommits.getJSONObject(zl).getString("author_email"));
					newCommit.setAuthorName(jsonArrayCommits.getJSONObject(zl).getString("author_name"));
					newCommit.setTitleCommit(jsonArrayCommits.getJSONObject(zl).getString("title"));
					newCommit.setCreatedAt(jsonArrayCommits.getJSONObject(zl).getString("created_at"));
					newCommit.setProjectName(projectName);
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				try {
					CommitLocalServiceUtil.addCommit(newCommit);
				} catch (SystemException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}	
	}
	
	/**
	 * init LineChartModel model for Commit History
	 * 	
	 * @param String studentName - this is the student hska name
	 * @param String projectName - this is the hska project id (for example "AWP")
	 * @return LineChartModel model - sorted by date
	 * @throws SystemException
	 * @author Mihai Sava 
	 */
    public LineChartModel initCommitHistoryModel(String studentName, String projectName) throws SystemException{
    	LineChartModel model = new LineChartModel();
    	
    	List<Commit> allCommitsFromThisStudent = CommitUtil.findByAuthorNameAndProjectName(studentName, projectName);
    	
    	// get dates from commits 
    		//we take the first 8 characters from the date field as string
    	ArrayList<String> dates = new ArrayList<String>();
    	for (int zl = 0; zl< allCommitsFromThisStudent.size(); zl++){
    		if (zl == 0){
    			dates.add(allCommitsFromThisStudent.get(zl).getCreatedAt().toString().substring(0, 7));
    		}else{
    			if(!allCommitsFromThisStudent.get(zl).getCreatedAt().toString().substring(0, 7).equals(dates.get(dates.size()-1))){
    				dates.add(allCommitsFromThisStudent.get(zl).getCreatedAt().toString().substring(0, 7));
	    		}
    		}
    	}
    	
    	// sort the arraylist 
    	ArrayList<Date> datesSorted = new ArrayList<Date>();
    	ArrayList<String> datesAsStringSorted = new ArrayList<String>();
    	for(String date : dates){	
    		int month = Integer.parseInt(date.substring(5, 7));
    		int year = Integer.parseInt(date.substring(0, 4));
    		
    		Calendar calendar = Calendar.getInstance();
        	calendar.clear();
        	calendar.set(Calendar.MONTH, month-1);
        	calendar.set(Calendar.YEAR, year);
        	Date dateDate = calendar.getTime();
        	
        	datesSorted.add(dateDate);
    	}
    	
    	Collections.sort(datesSorted);
    	for(Date date : datesSorted){
    		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    		String dateString = dateFormat.format(date.getTime());
    		datesAsStringSorted.add(dateString.substring(0, 7));
    	}
    		
    	// create a hash-map with the aggregated number of commits pro month
    		// !!! the entries in the hash-map are not any more sorted 
    	Map<String, Integer> commitsMap = new HashMap<String, Integer>();
    	for(String date : datesAsStringSorted){
    		int zahl = 0;
    		for (int zl = 0; zl< allCommitsFromThisStudent.size(); zl++){
    			if(allCommitsFromThisStudent.get(zl).getCreatedAt().toString().startsWith(date.substring(0, 7))){
    				zahl += 1;
    			}
    		}
    		commitsMap.put(date, zahl);
    	}
    	
    	//set model with the sorted values 
    	ChartSeries student = new ChartSeries();
    	student.setLabel(studentName);
    	for(int zl = 0; zl < datesAsStringSorted.size(); zl++){
    		student.set(datesAsStringSorted.get(zl), commitsMap.get(datesAsStringSorted.get(zl)));
    	}
        model.addSeries(student);
    	
    	return model;
    }
    
    /**
     * init Model for commitHistory - Project site
     * @param String - project name (for example "AWP")
     * @return DonutChartModel - model
     * @author Mihai Sava
     */
    public DonutChartModel initCommitDistributionModel(String projectName){
    	DonutChartModel commitDistributionModel = new DonutChartModel();
    	Map<String, Number> circle1 = new LinkedHashMap<String, Number>();
    	
    	//get all Commits from this project
    	List<Commit> allCommitsForThisProject = null;
    	try {
    		allCommitsForThisProject = CommitUtil.findByProjectName(projectName);
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	//get all Contributors for all projects
    	List<Contributor> allContributors = null;
    	try {
			allContributors = ContributorLocalServiceUtil.getContributors(0, ContributorLocalServiceUtil.getContributorsCount());
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	//get only the contributors for this project
    	List<Contributor> allContributorsForActualProject = new ArrayList<Contributor>();
    	for(Contributor contributor : allContributors){
			if(contributor.getProjectName().equals(projectName)){
				allContributorsForActualProject.add(contributor);
			}
		}
    	
    	//set values in the model
    	for (Contributor contributor : allContributorsForActualProject){
    		int numberOfCommits = 0;
    		for (Commit commit : allCommitsForThisProject){
    			if(commit.getAuthorName().equals(contributor.getName())){
    				numberOfCommits += 1;
    			}
    		}
    		circle1.put(contributor.getName(), numberOfCommits);
    	}
    	
    	commitDistributionModel.addCircle(circle1);
    	return commitDistributionModel;
    }
    
	/**
	 * get number of commits for this project
	 * @param project id as string (example "AWP")
	 * @return number of commits as int
	 * 
	 * @author Mihai Sava
	 */
    public Integer getAllCommitsForProjectId(String projectName){
    	//get all Commits from this project
    	List<Commit> allCommitsForThisProject = null;
    	try {
    		allCommitsForThisProject = CommitUtil.findByProjectName(projectName);
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	Integer anzahlCommits = allCommitsForThisProject.size();
    	return anzahlCommits;
    }
    
	/**
	 * delete all commits from the database
	 * 		this method is called before new commits are parsed and saved into database 
	 * 
	 * @param  -
	 * @return void
	 * @throws SystemException
	 * @author Mihai Sava
	 */
	public void deleteAllCommits() throws SystemException{
		List<Commit> allCommits = CommitLocalServiceUtil.getCommits(0, CommitLocalServiceUtil.getCommitsCount());
		for(Commit commit : allCommits){
			CommitLocalServiceUtil.deleteCommit(commit);
		}
	}
	
	/**
	 * load Property File
	 * @author Mihai Sava
	 */
	public Properties loadConfigFile(){
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
