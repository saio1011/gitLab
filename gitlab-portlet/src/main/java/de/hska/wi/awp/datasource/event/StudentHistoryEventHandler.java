package de.hska.wi.awp.datasource.event;

import java.io.Serializable;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;
import javax.portlet.Event;
import javax.portlet.faces.BridgeEventHandler;
import javax.portlet.faces.event.EventNavigationResult;

import com.liferay.faces.bridge.event.EventPayloadWrapper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;

import de.hska.wi.awp.datasource.bean.studenthistory.ProjectHistoryModelBean;
import de.hska.wi.awp.datasource.bean.studenthistory.StudentHistoryViewModelBean;
import de.hska.wi.awp.datasource.infosys.model.Project;
import de.hska.wi.awp.datasource.infosys.model.Student;
import de.hska.wi.awp.datasource.infosys.service.ProjectLocalServiceUtil;
import de.hska.wi.awp.datasource.infosys.service.StudentLocalServiceUtil;

/**
 * EventHandler of the commitHistory - Portlet
 * @author Mihai Sava
 */
public class StudentHistoryEventHandler implements BridgeEventHandler{
	@Override
	public EventNavigationResult handleEvent(FacesContext facesContext, Event event) {
		 EventNavigationResult eventNavigationResult = null;
         String eventQName = event.getQName().toString();

         if (eventQName.equals("{http://liferay.com/events}ipc.studentSelected")) {
             System.out.print("EVENT RECIVED STUDENT");
             
             Serializable value = event.getValue();
             
            if (value instanceof EventPayloadWrapper) {
 				value = ((EventPayloadWrapper) value).getWrapped();
 			}

            //Finds the Student id and sets it in the StudentHistoryViewModelBean
            //Finds the Project id and sets it in the StudentHistoryViewModelBean
            //Sets the Porject id null in the ProjectHistoryModelBean
            //This way we can choose between the views in one portlet
 			String hskaId = (String) value;
 			StudentHistoryViewModelBean studentHistoryViewModelBean = getStudentHistoryViewModelBean(facesContext);
 			studentHistoryViewModelBean.setStudenthskaId(hskaId);
 			Student student = StudentLocalServiceUtil.findByStudenthskaId(hskaId);
 			System.out.println("Print Student from handler ->" + student.getProject_id());
 	
 			Project project = null;
			try {
				project = ProjectLocalServiceUtil.getProject(student.getProject_id());
				System.out.println("Print Project from handler ->"+ project.getProjecthskaId());
			} catch (SystemException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (PortalException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			studentHistoryViewModelBean.setProjecthskaId(project.getProjecthskaId());
 			ProjectHistoryModelBean projectHistoryModelBean = getProjecttHistoryModelBean(facesContext);
 			projectHistoryModelBean.setProjectId(null);

 			String fromAction = null;
 			String outcome = "ipc.studentSelected";
 			eventNavigationResult = new EventNavigationResult(fromAction, outcome);
             
         }

         if(eventQName.equals("{http://liferay.com/events}ipc.projectSelected")) {
        	 System.out.print("EVENT RECIVED PROJECT");
        	 
        	 Serializable value = event.getValue();
             
             if (value instanceof EventPayloadWrapper) {
  				value = ((EventPayloadWrapper) value).getWrapped();
  			}

             //Finds the Project id and sets it in the ProjectHistoryModelBean
             //Sets the Student null in the StudentHistoryViewModelBean
             //This way we can choose between the views in one portlet
  			String hskaId = (String) value;
  			StudentHistoryViewModelBean studentHistoryViewModelBean = getStudentHistoryViewModelBean(facesContext);
  			studentHistoryViewModelBean.setProjecthskaId(hskaId);
  			StudentHistoryViewModelBean studentHistoryBean = getStudentHistoryViewModelBean(facesContext);
  			studentHistoryBean.setStudenthskaId(null);

  			String fromAction = null;
  			String outcome = "ipc.projectSelected";
  			eventNavigationResult = new EventNavigationResult(fromAction, outcome);
         }
         
         return eventNavigationResult;
	}
	
	protected StudentHistoryViewModelBean getStudentHistoryViewModelBean(FacesContext facesContext) {
		String elExpression = "#{studentHistoryViewModelBean}";
		ELContext elContext = facesContext.getELContext();
		ValueExpression valueExpression = facesContext.getApplication().getExpressionFactory().createValueExpression(
				elContext, elExpression, StudentHistoryViewModelBean.class);

		return (StudentHistoryViewModelBean) valueExpression.getValue(elContext);
	}
	
	protected ProjectHistoryModelBean getProjecttHistoryModelBean(FacesContext facesContext) {
		String elExpression = "#{projectHistoryModelBean}";
		ELContext elContext = facesContext.getELContext();
		ValueExpression valueExpression = facesContext.getApplication().getExpressionFactory().createValueExpression(
				elContext, elExpression, ProjectHistoryModelBean.class);

		return (ProjectHistoryModelBean) valueExpression.getValue(elContext);
	}

}
