import java.util.*;

import java.lang.Double;
import java.text.DecimalFormat;

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

        if (!compativel(a, b)) {
            System.out.println("Impossivel de fazer conversão");
            System.exit(1);
        }

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
    // multiplicações e divisões
    @Override
    public Pair visitMulDiv(CalculatorParser.MulDivContext ctx) {
        Pair a = visit(ctx.left);
        Pair b = visit(ctx.right);

        // Obter e organizar os valores

        String[] parts_a = a.getUnidade().split("/"); // ex : 10m^2/g -> [m^2 , g]
        String[] parts_b = b.getUnidade().split("/");

        Double[] expoentes = { 1.0, 1.0, 1.0, 1.0 };
        // ordem: 10 m^1/s^2 * 10 cm^3/l^4 (expoentes: [1 , 2 , 3 ,4] )
        //

        Pair mul_a = new Pair(a.getValor(), parts_a[0].split("\\^")[0]); // 10 m^2/g ---> 10 m
        Pair mul_b = new Pair(b.getValor(), parts_b[0].split("\\^")[0]);

        String div_a_temp = "";
        String div_b_temp = "";

        if (parts_a.length > 1) {
            div_a_temp = parts_a[1].split("\\^")[0];
        }
        if (parts_b.length > 1) {
            div_b_temp = parts_b[1].split("\\^")[0];
        }

        Pair div_a = new Pair(1.0, div_a_temp); // 10 m^2/g ---> 1 g
        Pair div_b = new Pair(1.0, div_b_temp);

        // ex: 10 m^2 / g * 3 cm
        // mul_a = m^2
        // div_a = g
        // mul_b = cm
        // div_b = ""
        // -----

        // Atualizar a tabela de expoentes
        if (parts_a[0].split("\\^").length > 1)
            expoentes[0] = Double.parseDouble(parts_a[0].split("\\^")[1]);
        if (parts_b[0].split("\\^").length > 1)
            expoentes[1] = Double.parseDouble(parts_b[0].split("\\^")[1]);
        if (!div_a_temp.equals("") && parts_a[1].split("\\^").length > 1)
            expoentes[2] = Double.parseDouble(parts_a[1].split("\\^")[1]);
        if (!div_b_temp.equals("") && parts_b[1].split("\\^").length > 1)
            expoentes[3] = Double.parseDouble(parts_b[1].split("\\^")[1]);

        // Converter para as unidades SI
        mul_a = convert_SI(mul_a, expoentes[0]);
        mul_b = convert_SI(mul_b, expoentes[1]);
        div_a = convert_SI(div_a, expoentes[2]);
        div_b = convert_SI(div_b, expoentes[3]);

        // Valores obtidos
        // System.out.println("ok");---------------------------------------------------
        /**
         * Exemplo de como guarda a informação: 30 cm/g^3 * 2m^2 ----------------------
         * expoentes = [mult_a , mult_b , div_a , div_b] ------------------------------
         * Pair mul_a = 0.3 m --------------------------------------------------------
         * Pair mul_b = 2 m -----------------------------------------------------------
         * Pair div_a = 1 g -----------------------------------------------------------
         * Pair div_b = 1 "" ----------------------------------------------------------
         * expoentes = [ 1 , 2 , 3 ,1]
         */
        // Fazer as contas-------------------------------------------------------------
        String op = ctx.op.getText();
        Pair c = new Pair(0.0, "");

        Double resultado = 0.0;
        String[] unidade = { mul_a.getUnidade(), mul_b.getUnidade(), "/" + div_a.getUnidade(), div_b.getUnidade() };

        if (unidade[2].equals("/"))
            unidade[2] = "";

        /**
         * Vai guardar o nome da unidade do resultado ----------------------------------
         * ex: 30cm/g^3 * 2m^2 ---------------------------------------------------------
         * unidade = [ cm , m , /g , ""]
         * 
         * 
         * 
         */

        DecimalFormat format = new DecimalFormat("0.#"); // Para remover zeros
        /*
         * if (op.equals("*")) { resultado = mul_a.getValor() * mul_b.getValor(); if
         * (compativel(mul_a, mul_b)) {
         * 
         * if ((expoentes[0] + expoentes[1]) == 0.0) unidade[0] = ""; else unidade[0] =
         * mul_a.getUnidade() + "^" + format.format(expoentes[0] + expoentes[1]);
         * unidade[1] = ""; expoentes[0] += expoentes[1];
         * 
         * if (compativel(mul_a, div_b) && !div_b.getUnidade().equals("")) { if
         * (expoentes[0] > expoentes[3]) { unidade[0] = mul_a.getUnidade() + "^" +
         * format.format(expoentes[0] - expoentes[3]); unidade[0] =
         * unidade[0].replace("1", ""); // quando a potencia é 1 unidade[3] = "";
         * expoentes[3] = expoentes[3] - expoentes[0]; } else if (expoentes[0] <
         * expoentes[3]) { unidade[0] = ""; unidade[3] = mul_a.getUnidade() + "^" +
         * format.format(expoentes[0] + expoentes[3]); unidade[3] =
         * unidade[3].replace("1", ""); // quando a potencia é 1 expoentes[3] =
         * expoentes[3] - expoentes[0]; } else { unidade[0] = ""; unidade[3] = "";
         * expoentes[3] = expoentes[3] - expoentes[0]; } }
         * 
         * unidade[2] = ""; if (compativel(div_a, div_b) &&
         * !div_b.getUnidade().equals("") && !div_a.getUnidade().equals("")) { if
         * ((expoentes[3] + expoentes[2]) == 0.0) { unidade[3] = ""; } else unidade[3] =
         * div_a.getUnidade() + "^" + format.format(expoentes[2] + expoentes[3]);
         * 
         * } else if (div_b.getUnidade().equals("") && div_a.getUnidade().equals("")) {
         * unidade[3] = ""; } else { unidade[3] = "/" + div_a.getUnidade() + " " +
         * div_b.getUnidade(); }
         * 
         * } else {
         * 
         * } }
         * 
         * else { System.out.println(""); }
         */
        if (op.equals("*")) {
            resultado = mul_a.getValor() * mul_b.getValor();
            // ------------------//
            if (compativel(mul_a, div_a)) {
                unidade[0] = mul_a.getUnidade() + "^" + format.format(expoentes[0] - expoentes[2]);
                unidade[2] = "";
                expoentes[0] -= expoentes[2];
                if (expoentes[0] - expoentes[2] == 0.0) {
                    unidade[0] = "";
                }
                div_a = new Pair(0.0, "");
            }
            if (compativel(mul_a, div_b)) {
                unidade[0] = mul_a.getUnidade() + "^" + format.format(expoentes[0] - expoentes[3]);
                unidade[3] = "";
                expoentes[0] -= expoentes[3];
                if (expoentes[0] - expoentes[3] == 0.0) {
                    unidade[0] = "";
                }
                div_b = new Pair(0.0, "");

            }
            if (compativel(mul_a, mul_b)) {
                unidade[0] = mul_a.getUnidade() + "^" + format.format(expoentes[0] + expoentes[1]);
                unidade[1] = "";
                expoentes[0] += expoentes[1];
                if (expoentes[0] + expoentes[1] == 0.0) {
                    unidade[0] = "";
                }
                mul_b = new Pair(0.0, "");
            }
            // ------------------//
            if (compativel(div_a, mul_b)) {
                if (expoentes[1] - expoentes[2] == 0.0) {
                    unidade[1] = "";
                    unidade[2] = "";
                } else if (expoentes[2].compareTo(expoentes[1]) > 0) {
                    unidade[1] = "";
                    unidade[2] = div_a.getUnidade() + "^" + format.format(expoentes[2] - expoentes[1]);
                } else {
                    unidade[1] = div_a.getUnidade() + "^" + format.format(expoentes[2] - expoentes[1]);
                    unidade[2] = "";
                }
                expoentes[2] -= expoentes[1];
                mul_b = new Pair(0.0, "");
            }
            if (compativel(div_a, div_b)) {
                if (expoentes[3] + expoentes[2] == 0.0) {
                    unidade[3] = "";
                    unidade[2] = "";
                } else if (expoentes[2].compareTo(expoentes[3]) > 0) {
                    unidade[3] = "";
                    unidade[2] = "/" + div_a.getUnidade() + "^" + format.format(expoentes[2] + expoentes[3]);
                } else {
                    unidade[3] = "/" + div_a.getUnidade() + "^" + format.format(expoentes[2] + expoentes[3]);
                    unidade[2] = "";
                }
                expoentes[2] += expoentes[3];
                div_b = new Pair(0.0, "");

            }
            // ------------------//
            if (compativel(mul_b, div_b)) {
                if (expoentes[3] + expoentes[1] == 0.0) {
                    unidade[3] = "";
                    unidade[1] = "";
                } else if (expoentes[1].compareTo(expoentes[3]) > 0) {
                    unidade[3] = "";
                    unidade[1] = div_a.getUnidade() + "^" + format.format(expoentes[1] - expoentes[3]);
                } else {
                    unidade[3] = div_a.getUnidade() + "^" + format.format(expoentes[1] - expoentes[3]);
                    unidade[1] = "";
                }
                div_b = new Pair(0.0, "");
                expoentes[1] -= expoentes[3];

            }

        }

        String unidadefinal = "";

        for (String fin : unidade) {
            unidadefinal += fin;
        }

        c = new Pair(resultado, unidadefinal);

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

    // -------------------------------------------------------------------------------------------------------------------//

    // Verifica se são compativeis
    public boolean compativel(Pair a, Pair b) {

        String aa = a.getUnidade().split("^")[0]; // ex: 1 m^2 + 1 cm^2
        String bb = b.getUnidade().split("^")[0];

        if (a.getUnidade().equals("") || b.getUnidade().equals("")) {
            return false;
        }

        if (distancia.containsKey(aa) && distancia.containsKey(bb)) {
            // System.out.println("compativeis");
            return true;
        } else if (massa.containsKey(aa) && massa.containsKey(bb)) {
            // System.out.println("compativeis");
            return true;
        } else
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

    // Retorna o mapa com a unidades (se for metros returna a tabela com os metros)
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

    // Funcão para converter um valor para a respetiva unidade SI ( usada no visit
    // da multiplicação e divisão)
    public Pair convert_SI(Pair a, Double exp) {

        if (a.getUnidade().equals("")) { // Em situações na mult/div em que (a = 2m/g) o a --> 2m/ 1 ""
            a = new Pair(1.0, "");
            return a;
        }

        String unidade = "";
        Map<String, Double> conversor = getConversor(a); // dicionario para saber qual o tipo de grandeza
        Double temp = a.getValor() * Math.pow(conversor.get(a.getUnidade()), exp);

        temp = (double) Math.round(temp * 100000d) / 100000d; // arredondar
        boolean d = true; // Para quando o Pair não tem dimensão ( em situações em que na mult/div
                          // 1cm ----> será separado em (1cm / 1 ""))
        Double value = 1.0;

        for (Map.Entry entry : conversor.entrySet()) {
            if (value.equals(entry.getValue()) && !entry.getKey().toString().equals("")) {
                unidade = entry.getKey().toString();
                d = false;
                break;
            }
        }

        a = new Pair(temp, unidade);

        return a;

    }

}