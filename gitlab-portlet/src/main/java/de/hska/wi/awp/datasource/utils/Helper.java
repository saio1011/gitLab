package de.hska.wi.awp.datasource.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONException;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class Helper {
	/**
	 * get ProjectId for one repository name
	 * @author Mihai Sava
	 */
	public static String getProjectId(String projectName, String privateTocken) throws JSONException{
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
}
