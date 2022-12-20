package client;

import com.hirshi001.networking.network.client.Client;

import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public abstract class Screen {

    public static final Scanner scanner = new Scanner(System.in);

    public static Client client;

    public Screen(){
    }

    public abstract void tick() throws ExecutionException, InterruptedException;

    public static void setScreen(Screen screen){
        ChatClient.screen = screen;
    }

}
