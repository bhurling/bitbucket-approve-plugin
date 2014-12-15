package org.jenkinsci.plugins.bitbucket_approval;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.util.logging.Logger;

public class BitbucketApprover extends Notifier {

    private static final Logger LOG = Logger.getLogger(BitbucketApprover.class
            .getName());

    private String mOwner;

    private String mSlug;

    @DataBoundConstructor
    public BitbucketApprover(String owner, String slug) {
        mOwner = owner;
        mSlug = slug;
    }

    public String getSlug() {
        return mSlug;
    }

    public String getOwner() {
        return mOwner;
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) {
        // TODO: execute something like this
        // curl -u {mUser}:{mPassword} -X POST "https://api.bitbucket.org/2.0/repositories/{mOwner}/{mSlug}/commit/`git rev-parse HEAD`/approve"
        return true;
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        private String mUser;

        private String mPassword;

        /**
         * In order to load the persisted global configuration, you have to
         * call load() in the constructor.
         */
        public DescriptorImpl() {
            load();
        }

        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            // Indicates that this builder can be used with all kinds of project types 
            return true;
        }

        /**
         * This human readable name is used in the configuration screen.
         */
        public String getDisplayName() {
            return "Approve commit on Bitbucket";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            mUser = formData.getString("user");
            mPassword = formData.getString("password");

            save();

            return super.configure(req, formData);
        }

        public String getUser() {
            return mUser;
        }

        public String getPassword() {
            return mPassword;
        }
    }
}

