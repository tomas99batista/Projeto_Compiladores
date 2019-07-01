import java.awt.desktop.SystemSleepEvent;
import java.util.*;

public class Calculator_visitor extends CalculatorBaseVisitor<Pair> {
    public static HashMap<String, Pair> variables = new HashMap();

    Map<String, Double> distancia = new HashMap() {
        {
            // meters
            put("km", 1000.0);
            put("hm", 100.0);
            put("dam", 10.0);
            put("m", 1.0);
            put("dm", 0.1);
            put("cm", 0.01);
            put("mm", 0.001);
            // imperial
            put("in", 0.0254); // inch ( 1 inch = 0.254 metros)
            put("ft", 0.3048); // foot
            put("yd", 0.9144); // yard
            put("fur", 201.1168); // furlong
            put("mi", 1609.344); // mile
            put("Nmi", 1852.0); // nautical mile
        }
    };

    // Alterar os valores
    Map<String, Double> massa = new HashMap() {
        {
            // meters
            put("km", 1000.0);
            put("hm", 100.0);
            put("dam", 10.0);
            put("m", 1.0);
            put("dm", 0.1);
            put("cm", 0.01);
            put("mm", 0.001);
            // imperial
            put("in", 1 / 0.0254); // inch ( 1 inch = 0.0254 metros)
            put("ft", 1 / 0.3048); // foot
            put("yd", 1 / 0.9144); // yard
            put("fur", 1 / 201.1168); // furlong
            put("mi", 1 / 1609.344); // mile
            put("Nmi", 1 / 1852.0); // nautical mile
        }
    };

    // Escrever variaveis
    @Override
    public Pair visitAssignValue(CalculatorParser.AssignValueContext ctx) {

        // System.out.println(ctx.expr().getText());

        Pair a = visit(ctx.expr());
        variables.put(ctx.ID().getText(), a);
        // System.out.println(variables);
        return a;
    }

    // Passar uma variavel de uma grandeza para outra
    @Override
    public Pair visitRedefineGreatness(CalculatorParser.RedefineGreatnessContext ctx) {

        Map<String, Double> conversor = distancia; // dicionario para saber qual o tipo de grandeza
        Pair a = variables.get(ctx.ID(0).getText());

        if (distancia.containsKey(a.getUnidade()))
            conversor = distancia;
        // else if (massa.containsKey(a.getUnidade()))
        // conversor = mass;
        // else if (tempo.containsKey(a.getUnidade()))
        // conversor = tempo;

        Pair b = new Pair(a.getValor(), ctx.ID(1).getText());
        Double val = a.getValor() * conversor.get(a.getUnidade()) / conversor.get(ctx.ID(1).getText());
        val = (double) Math.round(val * 100000d) / 100000d;

        Pair c = new Pair(val, b.getUnidade());
        variables.put(ctx.ID(0).getText(), c);

        return c;
    }

    @Override
    public Pair visitConvert(CalculatorParser.ConvertContext ctx) {
        Map<String, Double> conversor = distancia; // dicionario para saber qual o tipo de grandeza
        Pair a = visit(ctx.expr());

        if (distancia.containsKey(a.getUnidade()))
            conversor = distancia;
        // else if (massa.containsKey(a.getUnidade()))
        // conversor = mass;
        // else if (tempo.containsKey(a.getUnidade()))
        // conversor = tempo;

        Pair b = new Pair(a.getValor(), ctx.ID().getText());

        Double val = a.getValor() * conversor.get(a.getUnidade()) / conversor.get(ctx.ID().getText());
        val = (double) Math.round(val * 100000d) / 100000d;
        Pair c = new Pair(val, b.getUnidade());
        System.out.println(c.toString());
        return c;

    }

    // Retorna o valor de uma variavel
    @Override
    public Pair visitVariable(CalculatorParser.VariableContext ctx) {
        Pair a = variables.get(ctx.ID().getText());
        if (a == null) {
            System.out.print("variavel não está definida");
            System.exit(1);
        }
        return a;
    }

    // Print
    @Override
    public Pair visitPrint(CalculatorParser.PrintContext ctx) {

        Pair a;
        if (ctx.expr() != null)
            a = visit(ctx.expr());
        else
            a = visit(ctx.assignment());
        System.out.println(">>" + a.toString());
        return null;
    }

    // Retorna o valor de um numero e a unidade ( ex: return 10 m)
    @Override
    public Pair visitUni(CalculatorParser.UniContext ctx) {
        Double valor = Double.valueOf(ctx.valor().Number().getText());
        String unidade = ctx.valor().ID().getText();

        Pair a = new Pair(valor, unidade);
        return a;
    }

    // Somar ou subtrair
    @Override
    public Pair visitAddSub(CalculatorParser.AddSubContext ctx) {

        Pair a = visit(ctx.left);
        Pair b = visit(ctx.right);

        String op = ctx.op.getText();
        Double resultado = 0.0;
        Pair c = null;

        if (op.equals("+")) {
            if (compativel(a, b)) { // verifica se ambos são do mesmo tipo de grandeza (ex: ambos são distancia)

                if (a.getUnidade().equals(b.getUnidade())) { // Se ambos estiverem na mesma unidade ele dá o resultado
                                                             // final nessa unidade
                    c = new Pair(a.getValor() + b.getValor(), a.getUnidade());
                } else { // Se estiverem em unidades diferentes converteas para SI
                    c = Calc_resultado(a, b, "+");
                }
            } else {
                System.out.println("Medidas incompativeis");
                System.exit(1);
            }
        } else {
            if (compativel(a, b)) {
                if (a.getUnidade().equals(b.getUnidade())) {
                    c = new Pair(a.getValor() - b.getValor(), a.getUnidade());
                } else {
                    c = Calc_resultado(a, b, "-");
                }
            } else {
                System.out.println("Medidas incompativeis");
                System.exit(1);
            }
        }
        return c;
    }

    // -------------------------------------------------------------------------------------------------------------------//

    // Verifica se são compativeis
    public boolean compativel(Pair a, Pair b) {

        if (distancia.containsKey(a.getUnidade()) && distancia.containsKey(b.getUnidade()))
            return true;
        else
            return false;

    }

    // Calcula o resultado de 2 grandezas diferentes
    public Pair Calc_resultado(Pair a, Pair b, String op) {

        Double val_a;
        Double val_b;
        Double d_resultado = 0.0;

        Map<String, Double> conversor = distancia; // dicionario que vai ser usado nas proximas operacoes

        if (distancia.containsKey(a.getUnidade()))
            conversor = distancia;
        // else if (massa.containsKey(a.getUnidade()))
        // conversor = mass;
        // else if (tempo.containsKey(a.getUnidade()))
        // conversor = tempo;

        String unidade = null; // Para anotar a unidade do resultado final
        Double value = 1.0;
        for (Map.Entry entry : conversor.entrySet()) {
            if (value.equals(entry.getValue())) {
                unidade = entry.getKey().toString();
                break; // breaking because its one to one map
            }
        }
        // System.out.println(unidade);

        val_a = convert(a, conversor);
        val_b = convert(b, conversor);

        switch (op) {
        case "+":
            d_resultado = val_a + val_b;
            break;
        case "-":
            d_resultado = val_a - val_b;
            break;
        }

        Pair resultado = new Pair(d_resultado, unidade);

        return resultado;

    }

    // Converte para a mesma grandeza
    public Double convert(Pair a, Map<String, Double> conversor) {
        Double mult = conversor.get(a.getUnidade());
        Double resultado = a.getValor() * mult;

        return (double) Math.round(resultado * 100000d) / 100000d;
    }

}