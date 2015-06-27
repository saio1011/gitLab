package de.hska.wi.awp.datasource.bean.commitHistory;

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
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.json.JSONException;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.CategoryAxis;
import org.primefaces.model.chart.DonutChartModel;
import org.primefaces.model.chart.LineChartModel;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import de.hska.wi.awp.datasource.NoSuchContributorException;
import de.hska.wi.awp.datasource.model.Commit;
import de.hska.wi.awp.datasource.model.Contributor;
import de.hska.wi.awp.datasource.service.CommitLocalServiceUtil;
import de.hska.wi.awp.datasource.service.ContributorLocalServiceUtil;
import de.hska.wi.awp.datasource.service.ContributorServiceUtil;
import de.hska.wi.awp.datasource.service.persistence.CommitUtil;
import de.hska.wi.awp.datasource.service.persistence.ContributorUtil;

@ManagedBean(name = "projectHistoryModelBean")
@SessionScoped
public class ProjectHistoryModelBean implements Serializable{
	
	/**
	 * Serializable
	 */
	private static final long serialVersionUID = 3308514386036298980L;
	
	private String projectId;
	private Integer totalCommits;
	private DonutChartModel commitDistributionModel;
	
	/**
	 * Logger Util
	 */
	private static Log log = LogFactoryUtil.getLog(ProjectHistoryModelBean.class);


	public Integer getTotalCommits() {
		this.getNumberOfCommitsForThisProject(this.projectId);
		return totalCommits;
	}
	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
	
	public DonutChartModel getCommitDistributionModel() { 
		this.createCommitHistoryDistributionModel(this.projectId);
		return commitDistributionModel;
	}

	/**
	 * create Model for commit distribution 
	 * @param projectName
	 * 
	 * @author Mihai Sava
	 */
	private void createCommitHistoryDistributionModel(String projectName){
		log.info("CommitHistoryDistributionModel will be created");
		
		commitDistributionModel = CommitLocalServiceUtil.initCommitDistributionModel(projectName);
		commitDistributionModel.setTitle("Verteilung der Commits pro Student");
		commitDistributionModel.setLegendPosition("e");
		commitDistributionModel.setSliceMargin(5);
		commitDistributionModel.setShowDataLabels(true);
		commitDistributionModel.setDataFormat("value");
		commitDistributionModel.setShadow(false);
		
		log.info("CommitHistoryDistributionModel is was created");
	}
	
	/**
	 * get number of commits for this project
	 * @param project id as string (example "AWP")
	 * @return number of commits as int
	 * 
	 * @author Mihai Sava
	 */
	public void getNumberOfCommitsForThisProject(String projectName){
		log.info("getNumberOfCommitsForThisProject will be set");
		
		Integer nrOfCommits = CommitLocalServiceUtil.getAllCommitsForProjectId(projectName);
		totalCommits = nrOfCommits;
		
		log.info("getNumberOfCommitsForThisProject it was set");
	}
}
