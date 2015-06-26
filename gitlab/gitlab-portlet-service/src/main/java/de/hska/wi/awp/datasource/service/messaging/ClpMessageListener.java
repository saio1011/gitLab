package de.hska.wi.awp.datasource.service.messaging;

import com.liferay.portal.kernel.messaging.BaseMessageListener;
import com.liferay.portal.kernel.messaging.Message;

import de.hska.wi.awp.datasource.service.ClpSerializer;
import de.hska.wi.awp.datasource.service.CommitLocalServiceUtil;
import de.hska.wi.awp.datasource.service.CommitServiceUtil;
import de.hska.wi.awp.datasource.service.ContributorLocalServiceUtil;
import de.hska.wi.awp.datasource.service.ContributorServiceUtil;


public class ClpMessageListener extends BaseMessageListener {
    public static String getServletContextName() {
        return ClpSerializer.getServletContextName();
    }

    @Override
    protected void doReceive(Message message) throws Exception {
        String command = message.getString("command");
        String servletContextName = message.getString("servletContextName");

        if (command.equals("undeploy") &&
                servletContextName.equals(getServletContextName())) {
            CommitLocalServiceUtil.clearService();

            CommitServiceUtil.clearService();
            ContributorLocalServiceUtil.clearService();

            ContributorServiceUtil.clearService();
        }
    }
}
