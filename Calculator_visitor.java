import java.util.*;
import java.lang.Double;

public class Calculator_visitor extends CalculatorBaseVisitor<Pair> {
    public static HashMap<String, Pair> variables = new HashMap();

    // distancia
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
            // valor nullo para ajudar nas multiplicações e divisões
            put("", 1.0);
        }
    };

    // Peso
    Map<String, Double> massa = new HashMap() {
        {
            // grams
            put("kg", 1000.0);
            put("hg", 100.0);
            put("dag", 10.0);
            put("g", 1.0);
            put("dg", 0.1);
            put("cg", 0.01);
            put("mg", 0.001);
            // imperial
            put("lb", 453.59); // pound ( 1 pound = 0.0453 gramas)
            put("oz", 453.59 / 16); // ounce (1 ounce = 1/16 pounds)
            // valor nullo para ajudar nas multiplicações e divisões
            put("", 1.0);
        }
    };

    @Override
    public Pair visitPara(CalculatorParser.ParaContext ctx) {
        return visit(ctx.expr());
    }

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

        Pair a = variables.get(ctx.ID(0).getText());
        Map<String, Double> conversor = getConversor(a); // dicionario para saber qual o tipo de grandeza

        Pair temp = new Pair(null, ctx.ID(1).getText());
        if (!compativel(a, temp)) {
            System.out.println("Medidas incompativeis");
            System.exit(1);
        }

        Pair b = new Pair(a.getValor(), ctx.ID(1).getText());
        Double val = a.getValor() * conversor.get(a.getUnidade()) / conversor.get(ctx.ID(1).getText());
        val = (double) Math.round(val * 100000d) / 100000d;

        Pair c = new Pair(val, b.getUnidade());
        variables.put(ctx.ID(0).getText(), c);

        return c;
    }

    @Override
    public Pair visitConvert(CalculatorParser.ConvertContext ctx) {

        Pair a = visit(ctx.expr());
        Map<String, Double> conversor = getConversor(a); // dicionario para saber qual o tipo de grandeza

        Pair b = new Pair(a.getValor(), ctx.ID().getText());

        Double val = a.getValor() * conversor.get(a.getUnidade()) / conversor.get(ctx.ID().getText());
        val = (double) Math.round(val * 100000d) / 100000d;
        Pair c = new Pair(val, b.getUnidade());
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

        String op = ctx.op.getText(); // '+' ou '-'
        Double resultado = 0.0;
        Pair c = null; // inicializar a variavel

        if (op.equals("+")) {
            if (a.getUnidade().equals(b.getUnidade())) { // Se ambos estiverem na mesma unidade ele dá o resultado
                // final nessa unidade
                c = new Pair(a.getValor() + b.getValor(), a.getUnidade());
            } else {
                if (compativel(a, b)) { // verifica se ambos são do mesmo tipo de grandeza (ex: ambos são distancia)
                    c = Calc_resultado(a, b, "+");
                } else {
                    System.out.println("Medidas incompativeis");
                    System.exit(1);
                }
            }
        } else {
            if (a.getUnidade().equals(b.getUnidade())) { // Se ambos estiverem na mesma unidade ele dá o resultado
                // final nessa unidade
                c = new Pair(a.getValor() - b.getValor(), a.getUnidade());
            } else {
                if (compativel(a, b)) { // verifica se ambos são do mesmo tipo de grandeza (ex: ambos são distancia)
                    c = Calc_resultado(a, b, "-");
                } else {
                    System.out.println("Medidas incompativeis");
                    System.exit(1);
                }
            }
        }
        return c;
    }

    // ex: 4 kg * 2 mg = (4*2) 1000g * 0.001g = (8 * 1) (g*g) = 8g^2
    //
    @Override
    public Pair visitMulDiv(CalculatorParser.MulDivContext ctx) {
        Pair a = visit(ctx.left);
        Pair b = visit(ctx.right);

        String[] parts_a = a.getUnidade().split("/"); // ex : 10m/s -> [m , g]
        String[] parts_b = b.getUnidade().split("/");

        String mul_a = parts_a[0];
        String mul_b = parts_b[0];

        String div_a = "";
        String div_b = "";

        if (parts_a.length > 1) {
            div_a = parts_a[1];
        }
        if (parts_b.length > 1) {
            div_b = parts_b[1];
        }
        // -----

        String op = ctx.op.getText();
        Double resultado = 0.0;
        Pair c = new Pair(0.0, "teste");

        if (op.equals("*")) {

        }

        return c;
    }

    // IF
    @Override
    public Pair visitStatement(CalculatorParser.StatementContext ctx) {

        List<CalculatorParser.ConditionContext> conditions = ctx.condition();

        // boolean evaluatedCondition = false;
        int temp = 0; // Variavel para guardar qual os comandos a executar quando é a conditicao é
                      // verdadeira

        // Percorrer as condições
        for (CalculatorParser.ConditionContext condition : conditions) {
            Pair a = visit(condition.left);
            Pair b = visit(condition.right);

            // Verificar se as duas grandezas são compativeis
            if (!compativel(a, b)) {
                System.out.println("Valores incompativeis");
                System.exit(1);
            }

            // Avaliar a condição
            String op = condition.Comparator().getText();
            if (avaliar(a, b, op)) {
                visit(ctx.main(0));
                return new Pair(0.0, "");
            }
            temp++;
        }

        // Visitar o else caso os outros não sejam true
        if (ctx.main(temp) != null)
            visit(ctx.main(temp));

        return new Pair(0.0, "");
    }

     // FOR
     @Override
     public Pair visitForStatement(CalculatorParser.StatementContext ctx) {
     }
 
      // WHILE
    @Override
    public Pair visitWhileStatement(CalculatorParser.StatementContext ctx) {
    }



    // -------------------------------------------------------------------------------------------------------------------//

    // Verifica se são compativeis
    public boolean compativel(Pair a, Pair b) {

        if (distancia.containsKey(a.getUnidade()) && distancia.containsKey(b.getUnidade()))
            return true;
        else if (massa.containsKey(a.getUnidade()) && massa.containsKey(b.getUnidade()))
            return true;
        else
            return false;

    }

    // Calcula o resultado de 2 grandezas diferentes (adicao e subtracao)
    public Pair Calc_resultado(Pair a, Pair b, String op) {

        Double val_a;
        Double val_b;
        Double d_resultado = 0.0;

        Map<String, Double> conversor = getConversor(a); // dicionario que vai ser usado nas proximas operacoes

        String unidade = ""; // Para anotar a unidade do resultado final
        Double value = 1.0;
        // Descobrir a unidade SI
        for (Map.Entry entry : conversor.entrySet()) {
            if (value.equals(entry.getValue()) && !entry.getKey().toString().equals("")) {
                unidade = entry.getKey().toString();
                break;
            }
        }
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

    // Avalia o resultado de 2 comparações
    public boolean avaliar(Pair a, Pair b, String op) {
        Map<String, Double> conversor = getConversor(a);

        Double val_a = convert(a, conversor);
        Double val_b = convert(b, conversor);

        switch (op) {
        case ">":
            if (val_a.compareTo(val_b) > 0)
                return true;
            break;
        case ">=":
            if (val_a.compareTo(val_b) >= 0)
                return true;
            break;
        case "==":
            if (val_a.compareTo(val_b) == 0) {
                return true;
            }
            break;
        case "!=":
            if (val_a.compareTo(val_b) != 0)
                return true;
            break;
        case "<=":
            if (val_a.compareTo(val_b) <= 0)
                return true;
            break;
        case "<":
            if (val_a.compareTo(val_b) < 0)
                return true;
            break;
        }
        return false;
    }

    // Retorna o mapa com a unidades
    public Map<String, Double> getConversor(Pair a) {

        Map<String, Double> conversor = distancia;

        if (distancia.containsKey(a.getUnidade()))
            conversor = distancia;
        else if (massa.containsKey(a.getUnidade()))
            conversor = massa;
        // else if (tempo.containsKey(a.getUnidade()))
        // conversor = tempo;

        return conversor;
    }

}