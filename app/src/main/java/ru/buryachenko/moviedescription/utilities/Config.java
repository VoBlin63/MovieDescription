package ru.buryachenko.moviedescription.utilities;

public class Config
{
    private static Config config;
    private boolean isUseOverview = true;
    private boolean isPerfectFilterOnly = true;
    private int sleepSecondsBetweenLoadPages = 7;
    private boolean isUseOnlyWiFi = true;

    public void load() {
        //TODO
    }

    public void save() {
        //TODO
    }

    private Config() {
        this.load();
    }

    public static Config getInstance() {
        if (config == null) {
            config = new Config();
        }
        return config;
    }

    public boolean isUseOverview() {
        return isUseOverview;
    }

    public boolean isPerfectFilterOnly() {
        return isPerfectFilterOnly;
    }

    public int getSleepSecondsBetweenLoadPages() {
        return sleepSecondsBetweenLoadPages;
    }

    public boolean isUseOnlyWiFi() {
        return isUseOnlyWiFi;
    }

    public void setUseOverview(boolean useOverview) {
        isUseOverview = useOverview;
    }

    public void setPerfectFilterOnly(boolean perfectFilterOnly) {
        isPerfectFilterOnly = perfectFilterOnly;
    }

    public void setSleepSecondsBetweenLoadPages(int sleepSecondsBetweenLoadPages) {
        this.sleepSecondsBetweenLoadPages = sleepSecondsBetweenLoadPages;
    }

    public void setUseOnlyWiFi(boolean useOnlyWiFi) {
        isUseOnlyWiFi = useOnlyWiFi;
    }
}
