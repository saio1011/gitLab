package de.hska.wi.awp.datasource.bean.loadData;

import java.io.IOException;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.primefaces.json.JSONException;

import com.liferay.portal.kernel.exception.SystemException;

import de.hska.wi.awp.datasource.service.CommitLocalServiceUtil;
import de.hska.wi.awp.datasource.service.ContributorLocalServiceUtil;

@ManagedBean
@RequestScoped
public class GitLabLoadData{
	String bala;
	
	public String getBala() {
		return bala = "asdasda";
	}

	public void buttonAction() {
		System.out.println("fuck you");
        addMessage("Welcome to Primefaces!!");
    }
     
    public void addMessage(String summary) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary,  null);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

	public void fillDatabase(){
        addMessage("Datenbank GitLab wird neu geladen");
		
		//
		 String projectName = "arcsolution";
		 String privateTocken="private_token=3iwVxh3v71GzUCDz_Azm";
		 String projectId = "";
		try {
			projectId = CommitLocalServiceUtil.getProjectId(projectName, privateTocken);
			List<String> commitsAsJsonStrings = CommitLocalServiceUtil.getAllCommitsAsJsonString(projectId, privateTocken);
			CommitLocalServiceUtil.ParseCommitsFromJson(commitsAsJsonStrings);
			
		   	String contributorsAsJsonString = ContributorLocalServiceUtil.getContributors(projectId, privateTocken);
			ContributorLocalServiceUtil.ParseContributorsFromJson(contributorsAsJsonString);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		
 	
		System.out.println("Database GitLab is now up to date");
        addMessage("Datenbank GitLab erfolgreich geladen");
	}

}
