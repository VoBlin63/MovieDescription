package ru.buryachenko.moviedescription.utilities;

import static ru.buryachenko.moviedescription.Constant.CONFIG_KEY_SHOW_ONLY_FIT_FILTER;
import static ru.buryachenko.moviedescription.Constant.CONFIG_KEY_SLEEP_SECONDS_BETWEEN_LOAD_PAGES;
import static ru.buryachenko.moviedescription.Constant.CONFIG_KEY_USE_ONLY_WIFI;
import static ru.buryachenko.moviedescription.Constant.CONFIG_KEY_USE_OVERVIEW;

public class Config {
    private static Config config;
    private boolean isUseOverview = true;
    private boolean isShowOnlyFitFilter = true;
    private int sleepSecondsBetweenLoadPages = 7;
    private boolean isUseOnlyWiFi = true;

    public void load() {
        isUseOverview = Boolean.parseBoolean(SharedPreferencesOperation.getInstance().load(CONFIG_KEY_USE_OVERVIEW, "FALSE"));
        isShowOnlyFitFilter = Boolean.parseBoolean(SharedPreferencesOperation.getInstance().load(CONFIG_KEY_SHOW_ONLY_FIT_FILTER, "TRUE"));
        sleepSecondsBetweenLoadPages = Integer.parseInt(SharedPreferencesOperation.getInstance().load(CONFIG_KEY_SLEEP_SECONDS_BETWEEN_LOAD_PAGES, "7"));
        isUseOnlyWiFi = Boolean.parseBoolean(SharedPreferencesOperation.getInstance().load(CONFIG_KEY_USE_ONLY_WIFI, "TRUE"));
    }

    public void save() {
        SharedPreferencesOperation.getInstance().save(CONFIG_KEY_USE_OVERVIEW, String.valueOf(isUseOverview));
        SharedPreferencesOperation.getInstance().save(CONFIG_KEY_SHOW_ONLY_FIT_FILTER, String.valueOf(isShowOnlyFitFilter));
        SharedPreferencesOperation.getInstance().save(CONFIG_KEY_SLEEP_SECONDS_BETWEEN_LOAD_PAGES, String.valueOf(sleepSecondsBetweenLoadPages));
        SharedPreferencesOperation.getInstance().save(CONFIG_KEY_USE_ONLY_WIFI, String.valueOf(isUseOnlyWiFi));
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

    public boolean isShowOnlyFitFilter() {
        return isShowOnlyFitFilter;
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

    public void setShowOnlyFitFilter(boolean showOnlyFitFilter) {
        isShowOnlyFitFilter = showOnlyFitFilter;
    }

    public void setSleepSecondsBetweenLoadPages(int sleepSecondsBetweenLoadPages) {
        this.sleepSecondsBetweenLoadPages = sleepSecondsBetweenLoadPages;
    }

    public void setUseOnlyWiFi(boolean useOnlyWiFi) {
        isUseOnlyWiFi = useOnlyWiFi;
    }
}
