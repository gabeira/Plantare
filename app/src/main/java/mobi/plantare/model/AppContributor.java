package mobi.plantare.model;

/**
 * Created by gabriel on 3/23/17.
 */

public class AppContributor {

    private String userLogin;
    private String avatarUrl;
    private String gitHubHtmlUrl;
    private int contributions;

    public AppContributor(String userLogin, String avatarUrl, String gitHubHtmlUrl, int contributions) {
        this.userLogin = userLogin;
        this.avatarUrl = avatarUrl;
        this.gitHubHtmlUrl = gitHubHtmlUrl;
        this.contributions = contributions;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getGitHubHtmlUrl() {
        return gitHubHtmlUrl;
    }

    public void setGitHubHtmlUrl(String gitHubHtmlUrl) {
        this.gitHubHtmlUrl = gitHubHtmlUrl;
    }

    public int getContributions() {
        return contributions;
    }

    public void setContributions(int contributions) {
        this.contributions = contributions;
    }
}
