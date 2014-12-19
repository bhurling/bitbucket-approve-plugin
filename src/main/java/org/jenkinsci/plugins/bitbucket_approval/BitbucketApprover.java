package org.jenkinsci.plugins.bitbucket_approval;

import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
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

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@SuppressWarnings("unused") // This class will be loaded using its Descriptor.
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

        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(30, TimeUnit.SECONDS);
        client.setReadTimeout(60, TimeUnit.SECONDS);

        Request.Builder builder = new Request.Builder();
        Request request = builder.header("Authorization", getDescriptor().getBasicAuth())
                .url(makeUrl())
                .method("POST", null).build();

        try {
            Response response = client.newCall(request).execute();

            return response.isSuccessful();
        } catch (IOException e) {
            e.printStackTrace(listener.getLogger());
        }

        return false;
    }

    private String makeUrl() {
        String commitHash = ""; // TODO fetch commit hash from git

        return String.format("https://api.bitbucket.org/2.0/repositories/%s/%s/commit/%s/approve", mOwner, mSlug, commitHash);
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

        public String getBasicAuth() {
            return Credentials.basic(mUser, mPassword);
        }
    }
}

