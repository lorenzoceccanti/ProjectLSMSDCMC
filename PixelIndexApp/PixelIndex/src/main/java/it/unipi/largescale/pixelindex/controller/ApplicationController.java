package it.unipi.largescale.pixelindex.controller;

import it.unipi.largescale.pixelindex.view.dropdown.RegisteredMenu;

public class ApplicationController {
    private UnregisteredUserController unregisteredUserController;
    private RegisteredUserController registeredUserController;
    private ModeratorController moderatorController;
    private RegisteredMenu registeredMenu;
    private Runnable[] functionsUnregistered;
    private Runnable[] functionsRegistered;
    private String sessionUsername;
    private int loginOutcome = -1;


    public ApplicationController() {

        unregisteredUserController = new UnregisteredUserController();
        int fun = unregisteredUserController.showUnregisteredDropdown();
        if(fun == 0 || fun == 5)
        {
            registeredUserController = new RegisteredUserController(unregisteredUserController.getUsername(), unregisteredUserController.getDateOfBirth(), false);
            registeredUserController.execute();
        } else if(fun == 6){
            moderatorController = new ModeratorController(unregisteredUserController.getUsername(), unregisteredUserController.getDateOfBirth());
            moderatorController.execute();
        }
    }

        /*
        BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();
        ConsistencyThread consistencyThread = new ConsistencyThread(taskQueue);
        consistencyThread.start();
        GameService gameService = ServiceLocator.getGameService();
         */

        // Prova inserimento gioco con consistency

        /*
        Game game = new Game();
        game.setName("GREZZO 3");
        game.setSummary("Grezzo 2 sequel");
        game.setCategory("main_game");
        try {
            game.setId(gameService.insertGameOnDocument(game));
            consistencyThread.addTask(() -> {
                try {
                    gameService.insertGameOnGraph(game);
                    System.out.println("Consistency insertion SUCCEDED");
                } catch (ConnectionException e) {
                    System.out.println("Consistency insertion FAILED");
                    return;
                }

                consistencyThread.addTask(() -> {
                    System.out.println("MongoDB updated");
                });
            });
        } catch(ConnectionException e) {
            System.out.println("Document DB insertion FAILED");
        }


        // Prova ban in eventual consistency

        ModeratorService moderatorService = ServiceLocator.getModeratorService();
        try {
            moderatorService.banUser("ale1968");
            consistencyThread.addTask(() -> {
                try {
                    moderatorService.deleteUserFromGraph("ale1968");
                    System.out.println("Ban: consistency SUCCEDED");
                } catch (ConnectionException e) {
                    System.out.println("Ban: consistency FAILED");
                    return;
                }
            });
        } catch (ConnectionException ex) {
            System.out.println("Document DB removal failed");
        }

        consistencyThread.stopThread();
        // display_choices()

        // utente conferma roba

        // parte il servizio confermato
        */
}
