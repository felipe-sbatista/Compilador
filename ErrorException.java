public class ErrorException extends Exception{

    public ErrorException(Token token){
        super(Codigo.getNome(token.numero) + "Linha: "+ token.linha + "Coluna: "+ token.coluna + "\n Simbolo: "+ token.simbolo);
        System.exit(-1);
    }

    public ErrorException(Token token, String operacao, int escopo){
        super("Linha: "+ token.linha + "Coluna: "+ token.coluna + "\n"+ operacao+  "\nEscopo: "+ escopo);
        System.exit(-1);
    }

    public ErrorException(Token token, String operacao, int escopo, String variavel){
        super("Linha: "+ token.linha + "Coluna: "+ token.coluna + "\n"+ operacao + " " + variavel +  "\nEscopo: "+ escopo);
        System.exit(-1);
    }

    public ErrorException(Token token, String operacao, int escopo, Codigo tipoRecebedor, Codigo tipoAtribuinte){
        super("Linha: "+ token.linha + "Coluna: "+ token.coluna + "\n"+ operacao + tipoRecebedor + " e " + tipoAtribuinte +  "\nEscopo: "+ escopo);
        System.exit(-1);
    }

    public ErrorException(String msg){
        super(msg);
    }
}
