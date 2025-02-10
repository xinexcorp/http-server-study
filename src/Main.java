import winter.Winter;

public class Main {
    public static void main(String[] args) {
        // new BasicPrintExample().run();
        // new BasicCharEchoExample().run();
        // new BasicLineEchoExample().run();
        // new BasicHttpExample().run();

        System.out.println("Hello Winter");
        Winter winter = new Winter();

        //noinspection InfiniteLoopStatement
        while (true) {
            winter.loop();
        }
    }
}