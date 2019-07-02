public class Pair {

    Double valor;
    String unidade;

    public Pair(Double valor, String unidade) {
        this.valor = valor;
        this.unidade = unidade;
    }

    public Double getValor() {
        return this.valor;
    }

    public String getUnidade() {
        return this.unidade;
    }

    @Override
    public String toString() {
        if (unidade == null)
            return this.valor.toString();
        return this.valor.toString() + " " + this.unidade.toString();
    }

}