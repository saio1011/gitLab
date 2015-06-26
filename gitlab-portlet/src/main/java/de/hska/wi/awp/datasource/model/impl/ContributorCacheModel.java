package de.hska.wi.awp.datasource.model.impl;

import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.model.CacheModel;

import de.hska.wi.awp.datasource.model.Contributor;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * The cache model class for representing Contributor in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @see Contributor
 * @generated
 */
public class ContributorCacheModel implements CacheModel<Contributor>,
    Externalizable {
    public long contributorId;
    public String name;
    public String email;
    public int commits;
    public int locAdditions;
    public int locDeletions;

    @Override
    public String toString() {
        StringBundler sb = new StringBundler(13);

        sb.append("{contributorId=");
        sb.append(contributorId);
        sb.append(", name=");
        sb.append(name);
        sb.append(", email=");
        sb.append(email);
        sb.append(", commits=");
        sb.append(commits);
        sb.append(", locAdditions=");
        sb.append(locAdditions);
        sb.append(", locDeletions=");
        sb.append(locDeletions);
        sb.append("}");

        return sb.toString();
    }

    @Override
    public Contributor toEntityModel() {
        ContributorImpl contributorImpl = new ContributorImpl();

        contributorImpl.setContributorId(contributorId);

        if (name == null) {
            contributorImpl.setName(StringPool.BLANK);
        } else {
            contributorImpl.setName(name);
        }

        if (email == null) {
            contributorImpl.setEmail(StringPool.BLANK);
        } else {
            contributorImpl.setEmail(email);
        }

        contributorImpl.setCommits(commits);
        contributorImpl.setLocAdditions(locAdditions);
        contributorImpl.setLocDeletions(locDeletions);

        contributorImpl.resetOriginalValues();

        return contributorImpl;
    }

    @Override
    public void readExternal(ObjectInput objectInput) throws IOException {
        contributorId = objectInput.readLong();
        name = objectInput.readUTF();
        email = objectInput.readUTF();
        commits = objectInput.readInt();
        locAdditions = objectInput.readInt();
        locDeletions = objectInput.readInt();
    }

    @Override
    public void writeExternal(ObjectOutput objectOutput)
        throws IOException {
        objectOutput.writeLong(contributorId);

        if (name == null) {
            objectOutput.writeUTF(StringPool.BLANK);
        } else {
            objectOutput.writeUTF(name);
        }

        if (email == null) {
            objectOutput.writeUTF(StringPool.BLANK);
        } else {
            objectOutput.writeUTF(email);
        }

        objectOutput.writeInt(commits);
        objectOutput.writeInt(locAdditions);
        objectOutput.writeInt(locDeletions);
    }
}
