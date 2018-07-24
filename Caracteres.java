

public class Caracteres {
    static String digitos = "[0-9]";
    static String letras = "[a-z|A-Z]";
    static String aritmeticos = "[+-/*]";
    static String ID = "(_|"+letras+") (_|"+letras+"|"+digitos+")*";
    static String espaco_branco = "[ ]";
    static String quebra_linha = "[\n]";

    public boolean compara_branco(String caractere){
        if(caractere.matches(espaco_branco))
            return true;
        else return false;
    }

    public boolean compara_letra(String caractere){
        if(caractere.matches(letras)){
            return true;
        }else{
            return false;
        }
    }

    public boolean compara_digito(String caractere){
        if(caractere.matches(digitos)){
            return true;
        }else{
            return false;
        }
    }


}
