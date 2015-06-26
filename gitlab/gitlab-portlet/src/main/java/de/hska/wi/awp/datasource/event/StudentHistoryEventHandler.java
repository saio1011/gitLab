package de.hska.wi.awp.datasource.event;

import java.io.Serializable;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;
import javax.portlet.Event;
import javax.portlet.faces.BridgeEventHandler;
import javax.portlet.faces.event.EventNavigationResult;

import com.liferay.faces.bridge.event.EventPayloadWrapper;

import de.hska.wi.awp.datasource.bean.studenthistory.ProjectHistoryModelBean;
import de.hska.wi.awp.datasource.bean.studenthistory.StudentHistoryViewModelBean;


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

 			String hskaId = (String) value;
 			StudentHistoryViewModelBean studentHistoryViewModelBean = getStudentHistoryViewModelBean(facesContext);
 			studentHistoryViewModelBean.setStudenthskaId(hskaId);
// 			ProjectHistoryModelBean projectHistoryModelBean = getProjecttHistoryModelBean(facesContext);
// 			projectHistoryModelBean.setTotalContributors(null);

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

  			String hskaId = (String) value;
  			StudentHistoryViewModelBean studentHistoryViewModelBean = getStudentHistoryViewModelBean(facesContext);
  			studentHistoryViewModelBean.setProjecthskaId(hskaId);

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
		String elExpression = "#{projecttHistoryModelBean}";
		ELContext elContext = facesContext.getELContext();
		ValueExpression valueExpression = facesContext.getApplication().getExpressionFactory().createValueExpression(
				elContext, elExpression, StudentHistoryViewModelBean.class);

		return (ProjectHistoryModelBean) valueExpression.getValue(elContext);
	}

}
