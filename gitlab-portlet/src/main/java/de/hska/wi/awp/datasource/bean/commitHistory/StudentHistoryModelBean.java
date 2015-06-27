package de.hska.wi.awp.datasource.bean.commitHistory;


import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.primefaces.json.JSONException;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.CategoryAxis;
import org.primefaces.model.chart.LineChartModel;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import de.hska.wi.awp.datasource.model.Contributor;
import de.hska.wi.awp.datasource.service.CommitLocalServiceUtil;
import de.hska.wi.awp.datasource.service.ContributorLocalServiceUtil;

/**
 * 
 * @author Mihai Sava
 */

@ManagedBean(name = "studentHistoryModelBean")
@SessionScoped
public class StudentHistoryModelBean implements Serializable{
	
	 /**
	 * Serializable
	 */
	private static final long serialVersionUID = -1342820644463262367L;
	
	/**
	 * Logger Util
	 */
	private static Log log = LogFactoryUtil.getLog(StudentHistoryModelBean.class);
	
	private LineChartModel lineCommitsHistory;
	private String nameStudent;
	private int numberOfCommits;
	private int locAdditions;
	private int locDeletions;
	private int numberOfContributors;
	private int numberOfTotalCommits;
	private int numberOfTotalAdditions;
	private int numberOfTotalDeletions;
	private String studenthskaId;
	private String projecthskaId;
	private String noContributor;

	 
	public int getNumberOfContributors() throws Exception {
		this.createHeaderModel(this.getStudenthskaId(), this.getProjecthskaId());
		return numberOfContributors;
	}

	public int getNumberOfCommits() {
		return numberOfCommits;
	}

	public int getLocAdditions() {
		return locAdditions;
	}

	public int getLocDeletions() {
		return locDeletions;
	}
	public String getNameStudent() {
		return nameStudent;
	}

	public int getNumberOfTotalCommits() {
		return numberOfTotalCommits;
	}

	public int getNumberOfTotalAdditions() {
		return numberOfTotalAdditions;
	}

	public int getNumberOfTotalDeletions() {
		return numberOfTotalDeletions;
	}
	public String getStudenthskaId() {
		return studenthskaId;
	}

	public void setStudenthskaId(String studenthskaId) {
		this.studenthskaId = studenthskaId;
	}

	public String getProjecthskaId() {
		return projecthskaId;
	}

	public void setProjecthskaId(String projecthskaId) {
		this.projecthskaId = projecthskaId;
	} 
	
	
	public String getNoContributor() {
		return noContributor;
	}

	public LineChartModel getLineCommitsHistory() throws SystemException, IOException, JSONException {
		this.createLineCommitsHistoryModel(this.getStudenthskaId(), this.getProjecthskaId());
		return lineCommitsHistory;
	}

	
	
	/**
	 * create Commit History Model
	 * 
	 * @param String studenName - this is the hs name
	 * @return void
	 * @throws SystemException, IOException, JSONException
	 * @author Mihai Sava
	 */
	private void createLineCommitsHistoryModel(String studentName, String projectId) throws SystemException, IOException, JSONException{
		log.info("LineCommitsHistoryModel will be created");
		
		lineCommitsHistory = CommitLocalServiceUtil.initCommitHistoryModel(studentName, projectId);
		lineCommitsHistory.setTitle("Commit History");
		lineCommitsHistory.setLegendPosition("e");
		lineCommitsHistory.setShowPointLabels(true);
		lineCommitsHistory.getAxes().put(AxisType.X, new CategoryAxis("Zeitraum"));
        Axis yAxis = lineCommitsHistory.getAxis(AxisType.Y);
        yAxis.setLabel("Anzahl Commits");
        yAxis.setMin(0);
        
        log.info("LineCommitsHistoryModel was created");
	}

	/**
     * create model for header values
     * 
     * @param String studentName - this is the hs name
     * @param String projectId - this is the project id (for example "AWP")
     * @return void
	 * @author Mihai Sava
	 * @throws Exception 
     */
    private void createHeaderModel(String studentName, String projectId) throws Exception{
    	log.info("HeaderModel will be created");
    	
    	//get all Contributors for all projects
		List<Contributor> allContributors = ContributorLocalServiceUtil.getContributors(0, ContributorLocalServiceUtil.getContributorsCount());
		
		//get only the contributors for this project
		List<Contributor> allContributorsForActualProject = new ArrayList<Contributor>();
		for(Contributor contributor : allContributors){
			if(contributor.getProjectName().equals(projectId)){
				allContributorsForActualProject.add(contributor);
			}
		}
		
		numberOfContributors = allContributorsForActualProject.size();
		
    	int totalLocAdditions = 0;
    	int tatalLocDeletions = 0;;
    	int totalCommits = 0;
    	boolean isCurrentUser = false;
    	//One Student can push into many repositories
    	//therfore I can have here a List of many "current user"
    	List<Contributor> currentUser = new ArrayList<Contributor>();
    	for (Contributor contributor : allContributorsForActualProject){
    		totalLocAdditions += contributor.getLocAdditions();
    		tatalLocDeletions += contributor.getLocDeletions();
    		totalCommits += contributor.getCommits();
    		
    		if(contributor.getName().equals(studentName)){ 
    			currentUser.add(contributor);
    			isCurrentUser = true;
    		}
    	}
    	
    	if(isCurrentUser){
    		int nrOfCommits = 0;
    		int nrlocAdditions = 0;
    		int nrlocDeletions = 0;
    		for(Contributor currentUsr : currentUser){
    			nrOfCommits += currentUsr.getCommits();
    			nrlocDeletions += currentUsr.getLocDeletions();
    			nrlocAdditions += currentUsr.getLocAdditions();
    		}

    		numberOfCommits = nrOfCommits;
    		locAdditions = nrlocAdditions;
    		locDeletions = nrlocDeletions;
    		
    		noContributor = "";
    	}else{
    		noContributor = studentName + " ist kein Contributor";
    		numberOfCommits = 0;
    		locAdditions = 0;
    		locDeletions = 0;
    	}
    	
    	numberOfTotalAdditions = totalLocAdditions;
    	numberOfTotalDeletions = tatalLocDeletions;
    	numberOfTotalCommits = totalCommits;
    	
    	log.info("HeaderModel was created");
    }
}
