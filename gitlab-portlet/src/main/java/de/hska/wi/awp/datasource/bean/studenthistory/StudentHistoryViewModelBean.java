package de.hska.wi.awp.datasource.bean.studenthistory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

import org.primefaces.json.JSONException;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.CategoryAxis;
import org.primefaces.model.chart.LineChartModel;

import com.liferay.portal.kernel.exception.SystemException;

import de.hska.wi.awp.datasource.NoSuchContributorException;
import de.hska.wi.awp.datasource.model.Contributor;
import de.hska.wi.awp.datasource.service.CommitLocalServiceUtil;
import de.hska.wi.awp.datasource.service.ContributorLocalServiceUtil;
import de.hska.wi.awp.datasource.service.ContributorServiceUtil;
import de.hska.wi.awp.datasource.service.persistence.ContributorUtil;

@ManagedBean
public class StudentHistoryViewModelBean implements Serializable{
	
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
	 
//	 @PostConstruct
//	 public void init(){
//		 String projectName = "arcsolution";
//		 String privateTocken="private_token=3iwVxh3v71GzUCDz_Azm";
//		 String studentName = "saio1011";
//		 String projectId = "blabla";
////			 String projectId = CommitLocalServiceUtil.getProjectId(projectName, privateTocken);
//	 }
	 
	public int getNumberOfContributors() throws Exception {
		this.createHeaderModel(this.getStudenthskaId(), "291216", "private_token=3iwVxh3v71GzUCDz_Azm");
		System.out.println("Student id:"+this.getStudenthskaId());
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
		this.createLineCommitsHistoryModel(this.getStudenthskaId(), "291216", "private_token=3iwVxh3v71GzUCDz_Azm");
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
	private void createLineCommitsHistoryModel(String studentName, String projectId, String privateTocken) throws SystemException, IOException, JSONException{
//		List<String> commitsAsJsonStrings = CommitLocalServiceUtil.getAllCommitsAsJsonString(projectId, privateTocken);
//		CommitLocalServiceUtil.ParseCommitsFromJson(commitsAsJsonStrings);
		
		lineCommitsHistory = CommitLocalServiceUtil.initCommitHistoryModel(studentName);
		lineCommitsHistory.setTitle("Commit History");
		lineCommitsHistory.setLegendPosition("e");
		lineCommitsHistory.setShowPointLabels(true);
		lineCommitsHistory.getAxes().put(AxisType.X, new CategoryAxis("Zeitraum"));
        Axis yAxis = lineCommitsHistory.getAxis(AxisType.Y);
        yAxis.setLabel("Anzahl Commits");
        yAxis.setMin(0);
	}

	/**
     * create model for header values
     * 
     * @param String studentName - this is the hs name
     * @return void
	 * @author Mihai Sava
	 * @throws Exception 
     */
    private void createHeaderModel(String studentName, String projectId, String privateTocken) throws Exception{
//    	String contributorsAsJsonString = ContributorLocalServiceUtil.getContributors(projectId, privateTocken);
//		ContributorLocalServiceUtil.ParseContributorsFromJson(contributorsAsJsonString);
		
		numberOfContributors = ContributorLocalServiceUtil.getContributorsCount();
		
		List<Contributor> allContributors = ContributorLocalServiceUtil.getContributors(0, ContributorLocalServiceUtil.getContributorsCount());
		
    	int totalLocAdditions = 0;
    	int tatalLocDeletions = 0;;
    	int totalCommits = 0;
    	boolean isCurrentUser = false;
    	for (Contributor contributor : allContributors){
    		totalLocAdditions += contributor.getLocAdditions();
    		tatalLocDeletions += contributor.getLocDeletions();
    		totalCommits += contributor.getCommits();
    		
    		if(contributor.getName().equals(studentName)){
    			isCurrentUser = true;
    		}
    	}
    	
    	if(isCurrentUser){
    		Contributor currentStudent = ContributorLocalServiceUtil.getCurrentUser(studentName);
    		
    		if(currentStudent == null){
    			numberOfCommits = 00; 
    			locAdditions = 00;
    			locDeletions = 00;
    		}

    		numberOfCommits = currentStudent.getCommits();
    		locAdditions = currentStudent.getLocAdditions();
    		locDeletions = currentStudent.getLocDeletions();
    		
    		noContributor = "";
    	}else{
    		noContributor = studentName + " ist kein Contributor";
    	}
    	
    	numberOfTotalAdditions = totalLocAdditions;
    	numberOfTotalDeletions = tatalLocDeletions;
    	numberOfTotalCommits = totalCommits;
    }
}
