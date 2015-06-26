package de.hska.wi.awp.datasource.service.persistence;

import com.liferay.portal.kernel.dao.orm.BaseActionableDynamicQuery;
import com.liferay.portal.kernel.exception.SystemException;

import de.hska.wi.awp.datasource.model.Contributor;
import de.hska.wi.awp.datasource.service.ContributorLocalServiceUtil;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
public abstract class ContributorActionableDynamicQuery
    extends BaseActionableDynamicQuery {
    public ContributorActionableDynamicQuery() throws SystemException {
        setBaseLocalService(ContributorLocalServiceUtil.getService());
        setClass(Contributor.class);

        setClassLoader(de.hska.wi.awp.datasource.service.ClpSerializer.class.getClassLoader());

        setPrimaryKeyPropertyName("contributorId");
    }
}
