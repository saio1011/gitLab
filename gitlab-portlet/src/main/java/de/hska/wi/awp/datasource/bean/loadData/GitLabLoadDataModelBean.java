package de.hska.wi.awp.datasource.bean.loadData;



import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import org.primefaces.json.JSONException;

import com.liferay.portal.kernel.exception.SystemException;

import de.hska.wi.awp.datasource.service.CommitLocalServiceUtil;
import de.hska.wi.awp.datasource.service.ContributorLocalServiceUtil;


@ManagedBean
@RequestScoped
public class GitLabLoadDataModelBean{
	
	public void addGitLabMockData() {
		fillDatabase();
    }
     
    public void addMessage(String summary) {
    	System.out.println(summary);
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary,  null);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

	public void fillDatabase(){
        addMessage("Datenbank GitLab wird neu geladen");
		
        //Load Contributors and Commits from GitLab
    	Map<String, String> contributorsAsJsonStringsWithProjectName = null;
    	Map<String, String> commitsAsJsonStringsWithProjectName = null;
		try {
			contributorsAsJsonStringsWithProjectName = ContributorLocalServiceUtil.getContributors();
			ContributorLocalServiceUtil.ParseContributorsFromJson(contributorsAsJsonStringsWithProjectName);
			
			commitsAsJsonStringsWithProjectName = CommitLocalServiceUtil.getAllCommitsAsJsonString();
			CommitLocalServiceUtil.ParseCommitsFromJson(commitsAsJsonStringsWithProjectName);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Database GitLab is now up to date");
        addMessage("Datenbank GitLab erfolgreich geladen");
	}

}
