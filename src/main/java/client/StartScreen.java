package client;

public class StartScreen extends Screen{
    @Override
    public void tick() {
        System.out.println("Welcome to the chat room!");
        while(true){
            System.out.println("Enter 1 to login or 2 to register");
            String line = scanner.nextLine();
            if(line.equals("1")) {
                setScreen(new LoginScreen());
                break;
            }else if(line.equals("2")) {
                setScreen(new RegisterScreen());
                break;
            } else {
                System.out.println("Invalid input");
            }
        }
    }
}
