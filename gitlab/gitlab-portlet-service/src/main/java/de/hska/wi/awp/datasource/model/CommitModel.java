package de.hska.wi.awp.datasource.model;

import com.liferay.portal.kernel.bean.AutoEscape;
import com.liferay.portal.model.BaseModel;
import com.liferay.portal.model.CacheModel;
import com.liferay.portal.service.ServiceContext;

import com.liferay.portlet.expando.model.ExpandoBridge;

import java.io.Serializable;

/**
 * The base model interface for the Commit service. Represents a row in the &quot;gitlab_Commit&quot; database table, with each column mapped to a property of this class.
 *
 * <p>
 * This interface and its corresponding implementation {@link de.hska.wi.awp.datasource.model.impl.CommitModelImpl} exist only as a container for the default property accessors generated by ServiceBuilder. Helper methods and all application logic should be put in {@link de.hska.wi.awp.datasource.model.impl.CommitImpl}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see Commit
 * @see de.hska.wi.awp.datasource.model.impl.CommitImpl
 * @see de.hska.wi.awp.datasource.model.impl.CommitModelImpl
 * @generated
 */
public interface CommitModel extends BaseModel<Commit> {
    /*
     * NOTE FOR DEVELOPERS:
     *
     * Never modify or reference this interface directly. All methods that expect a commit model instance should use the {@link Commit} interface instead.
     */

    /**
     * Returns the primary key of this commit.
     *
     * @return the primary key of this commit
     */
    public String getPrimaryKey();

    /**
     * Sets the primary key of this commit.
     *
     * @param primaryKey the primary key of this commit
     */
    public void setPrimaryKey(String primaryKey);

    /**
     * Returns the commit ID of this commit.
     *
     * @return the commit ID of this commit
     */
    @AutoEscape
    public String getCommitId();

    /**
     * Sets the commit ID of this commit.
     *
     * @param commitId the commit ID of this commit
     */
    public void setCommitId(String commitId);

    /**
     * Returns the author name of this commit.
     *
     * @return the author name of this commit
     */
    @AutoEscape
    public String getAuthorName();

    /**
     * Sets the author name of this commit.
     *
     * @param authorName the author name of this commit
     */
    public void setAuthorName(String authorName);

    /**
     * Returns the author email of this commit.
     *
     * @return the author email of this commit
     */
    @AutoEscape
    public String getAuthorEmail();

    /**
     * Sets the author email of this commit.
     *
     * @param authorEmail the author email of this commit
     */
    public void setAuthorEmail(String authorEmail);

    /**
     * Returns the title commit of this commit.
     *
     * @return the title commit of this commit
     */
    @AutoEscape
    public String getTitleCommit();

    /**
     * Sets the title commit of this commit.
     *
     * @param titleCommit the title commit of this commit
     */
    public void setTitleCommit(String titleCommit);

    /**
     * Returns the created at of this commit.
     *
     * @return the created at of this commit
     */
    @AutoEscape
    public String getCreatedAt();

    /**
     * Sets the created at of this commit.
     *
     * @param createdAt the created at of this commit
     */
    public void setCreatedAt(String createdAt);

    @Override
    public boolean isNew();

    @Override
    public void setNew(boolean n);

    @Override
    public boolean isCachedModel();

    @Override
    public void setCachedModel(boolean cachedModel);

    @Override
    public boolean isEscapedModel();

    @Override
    public Serializable getPrimaryKeyObj();

    @Override
    public void setPrimaryKeyObj(Serializable primaryKeyObj);

    @Override
    public ExpandoBridge getExpandoBridge();

    @Override
    public void setExpandoBridgeAttributes(BaseModel<?> baseModel);

    @Override
    public void setExpandoBridgeAttributes(ExpandoBridge expandoBridge);

    @Override
    public void setExpandoBridgeAttributes(ServiceContext serviceContext);

    @Override
    public Object clone();

    @Override
    public int compareTo(de.hska.wi.awp.datasource.model.Commit commit);

    @Override
    public int hashCode();

    @Override
    public CacheModel<de.hska.wi.awp.datasource.model.Commit> toCacheModel();

    @Override
    public de.hska.wi.awp.datasource.model.Commit toEscapedModel();

    @Override
    public de.hska.wi.awp.datasource.model.Commit toUnescapedModel();

    @Override
    public String toString();

    @Override
    public String toXmlString();
}