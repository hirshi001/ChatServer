package client;

public class StartScreen extends Screen{
    @Override
    public void tick() {
        System.out.println("Welcome to the chat room!");
        while(true){
            System.out.println("Enter 1 to login or 2 to register");
            String line = scanner.nextLine();
            if("1".equals(line)) {
                setScreen(new LoginScreen());
                break;
            }else if("2".equals(line)) {
                setScreen(new RegisterScreen());
                break;
            } else {
                System.out.println("Invalid input");
            }
        }
    }
}
