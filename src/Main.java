import core.FixedTimestepGame;
import core.Game;
import core.VariableTimestepGame;

public class Main {

    public static void main(String[] args) {
        Game g = new VariableTimestepGame();
        //Game g = new FixedTimestepGame();
        g.start();
        //new HelloWorld().run();
    }
}
