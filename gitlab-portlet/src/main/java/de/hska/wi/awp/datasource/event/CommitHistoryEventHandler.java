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
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import de.hska.wi.awp.datasource.bean.commitHistory.ProjectHistoryModelBean;
import de.hska.wi.awp.datasource.bean.commitHistory.StudentHistoryModelBean;
import de.hska.wi.awp.datasource.infosys.model.Project;
import de.hska.wi.awp.datasource.infosys.model.Student;
import de.hska.wi.awp.datasource.infosys.service.ProjectLocalServiceUtil;
import de.hska.wi.awp.datasource.infosys.service.StudentLocalServiceUtil;

/**
 * EventHandler of the commitHistory - Portlet
 * @author Mihai Sava
 */
public class CommitHistoryEventHandler implements BridgeEventHandler{
	
	/**
	 * Logger Util
	 */
	private static Log log = LogFactoryUtil.getLog(CommitHistoryEventHandler.class);
	
	/**
	 * Handles the retrieved Event
	 * Either student event ipc.studentSelected or project event ipc.projectSelected
	 */
	@Override
	public EventNavigationResult handleEvent(FacesContext facesContext, Event event) {
		 EventNavigationResult eventNavigationResult = null;
         String eventQName = event.getQName().toString();

         if (eventQName.equals("{http://liferay.com/events}ipc.studentSelected")) {
             System.out.print("EVENT RECIVED STUDENT");
             log.info("Event Student recived");
             
             Serializable value = event.getValue();
             
            if (value instanceof EventPayloadWrapper) {
 				value = ((EventPayloadWrapper) value).getWrapped();
 			}

            //Finds the Student id and sets it in the StudentHistoryViewModelBean
            //Finds the Project id and sets it in the StudentHistoryViewModelBean
            //Sets the Porject id null in the ProjectHistoryModelBean
            //This way we can choose between the views in one portlet
 			String hskaId = (String) value;
 			StudentHistoryModelBean studentHistoryModelBean = getStudentHistoryModelBean(facesContext);
 			studentHistoryModelBean.setStudenthskaId(hskaId);
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
			studentHistoryModelBean.setProjecthskaId(project.getProjecthskaId());
 			ProjectHistoryModelBean projectHistoryModelBean = getProjectHistoryModelBean(facesContext);
 			projectHistoryModelBean.setProjectId(null);

 			String fromAction = null;
 			String outcome = "ipc.studentSelected";
 			eventNavigationResult = new EventNavigationResult(fromAction, outcome);
 			
 			log.info("Event Student passed to the class");
             
         }

         if(eventQName.equals("{http://liferay.com/events}ipc.projectSelected")) {
        	 System.out.print("EVENT RECIVED PROJECT");
        	 log.info("Event Project recived");
        	 
        	 Serializable value = event.getValue();
             
             if (value instanceof EventPayloadWrapper) {
  				value = ((EventPayloadWrapper) value).getWrapped();
  			}

             //Finds the Project id and sets it in the ProjectHistoryModelBean
             //Sets the Student null in the StudentHistoryViewModelBean
             //This way we can choose between the views in one portlet
  			String hskaId = (String) value;
  			ProjectHistoryModelBean projectHistoryModelBean = getProjectHistoryModelBean(facesContext);
  			projectHistoryModelBean.setProjectId(hskaId);
  			StudentHistoryModelBean studentHistoryBean = getStudentHistoryModelBean(facesContext);
  			studentHistoryBean.setStudenthskaId(null);

  			String fromAction = null;
  			String outcome = "ipc.projectSelected";
  			eventNavigationResult = new EventNavigationResult(fromAction, outcome);
  			
  			log.info("Event Project passed to the class");
         }
         
         return eventNavigationResult;
	}
	
	/**
	 * get StudentHistoryModelBean
	 */
	protected StudentHistoryModelBean getStudentHistoryModelBean(FacesContext facesContext) {
		String elExpression = "#{studentHistoryModelBean}";
		ELContext elContext = facesContext.getELContext();
		ValueExpression valueExpression = facesContext.getApplication().getExpressionFactory().createValueExpression(
				elContext, elExpression, StudentHistoryModelBean.class);

		return (StudentHistoryModelBean) valueExpression.getValue(elContext);
	}
	
	/**
	 * get ProjectHistoryModelBean
	 */
	protected ProjectHistoryModelBean getProjectHistoryModelBean(FacesContext facesContext) {
		String elExpression = "#{projectHistoryModelBean}";
		ELContext elContext = facesContext.getELContext();
		ValueExpression valueExpression = facesContext.getApplication().getExpressionFactory().createValueExpression(
				elContext, elExpression, ProjectHistoryModelBean.class);

		return (ProjectHistoryModelBean) valueExpression.getValue(elContext);
	}
}
