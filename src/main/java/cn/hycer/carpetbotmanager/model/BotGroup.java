package cn.hycer.carpetbotmanager.model;

import java.util.ArrayList;
import java.util.List;

public class BotGroup {

    private String name;
    private String description;
    private List<String> bots = new ArrayList<>();

    public BotGroup() {}

    public BotGroup(String name, String description, List<String> bots) {
        this.name = name;
        this.description = description;
        this.bots = bots;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getBots() {
        return bots;
    }

    public void setBots(List<String> bots) {
        this.bots = bots;
    }
}
