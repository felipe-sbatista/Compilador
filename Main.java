
import java.io.*;

public class Main {

    private static String endereco_arquivo = null;

    public static void main(String[] args) throws IOException, ErrorException {
       if (args.length == 1) {
            endereco_arquivo = args[0];
        } else {
           throw new ErrorException("Erro de argumentos!");
        }

        FileReader arquivo = new FileReader(endereco_arquivo);
        BufferedReader reader = new BufferedReader(arquivo);
        Parser parser = new Parser(reader);
        parser.run();
    }
}
