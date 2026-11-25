package br.fiesp.senai.unisenai.saborexpress.pdv.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.regex.Pattern;

public final class FormatUtils {

    private static final Locale PT_BR = new Locale("pt", "BR");
    private static final NumberFormat MOEDA = NumberFormat.getCurrencyInstance(PT_BR);
    private static final Pattern TELEFONE_PATTERN = Pattern.compile("^\\(\\d{2}\\)\\s\\d{4,5}-\\d{4}$");

    private FormatUtils() {}

    public static String formatarMoeda(BigDecimal valor) {
        if (valor == null) return MOEDA.format(BigDecimal.ZERO);
        return MOEDA.format(valor);
    }

    public static String formatarMoeda(double valor) {
        return MOEDA.format(valor);
    }

    public static BigDecimal parseBigDecimal(String texto) throws IllegalArgumentException {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException("Valor não pode ser vazio.");
        }
        String limpo = texto.trim().replace("R$", "").replace(" ", "").replace(".", "").replace(",", ".");
        try {
            return new BigDecimal(limpo);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Valor monetário inválido. Use formato: 19,99");
        }
    }

    public static Integer parseInteiro(String texto) throws IllegalArgumentException {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException("Quantidade não pode ser vazia.");
        }
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Quantidade inválida. Use número inteiro (ex.: 10).");
        }
    }

    public static String formatarTelefone(String telefone) {
        if (telefone == null || telefone.trim().isEmpty()) return "";
        String apenasDigitos = telefone.replaceAll("\\D", "");
        if (apenasDigitos.length() == 10) {
            return String.format("(%s) %s-%s",
                    apenasDigitos.substring(0, 2),
                    apenasDigitos.substring(2, 6),
                    apenasDigitos.substring(6));
        } else if (apenasDigitos.length() == 11) {
            return String.format("(%s) %s-%s",
                    apenasDigitos.substring(0, 2),
                    apenasDigitos.substring(2, 7),
                    apenasDigitos.substring(7));
        }
        return telefone;
    }

    public static boolean telefoneValido(String telefone) {
        if (telefone == null || telefone.trim().isEmpty()) return true;
        return TELEFONE_PATTERN.matcher(telefone.trim()).matches();
    }

    public static String removerMascaraTelefone(String telefone) {
        if (telefone == null) return "";
        return telefone.replaceAll("\\D", "");
    }
}