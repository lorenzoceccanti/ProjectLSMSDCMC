package it.unipi.largescale.pixelindex.view.dropdown;

import it.unipi.largescale.pixelindex.view.impl.ListSelector;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class RegisteredMenu {
    ArrayList<String> options;
    ListSelector listSelector;
    int selection = -1;

    public RegisteredMenu() {
        options = new ArrayList<>();
        options.add("Change info");
        options.add("Change password");
        options.add("Search by game");
        options.add("Search by company");
        options.add("Advanced search");
        options.add("Exit app");
    }
    public int displayMenu(String username){
        listSelector = new ListSelector("Welcome " + username);
        listSelector.addOptions(options, "registered_menu", "Make your choice");
        selection = listSelector.askUserInteraction("registered_menu");
        return selection;
    }
}